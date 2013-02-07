/*
 * Copyright 2012 Alexey Hanin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package getrest.android.client.impl;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import android.os.Handler;
import android.os.IBinder;

import getrest.android.GetrestClient;

import getrest.android.client.RequestCallback;
import getrest.android.client.RequestCallbackFactory;
import getrest.android.client.RequestRegistry;

import getrest.android.core.GetrestRuntime;
import getrest.android.core.Loggers;
import getrest.android.core.Request;
import getrest.android.core.RequestFuture;
import getrest.android.core.RequestManager;
import getrest.android.core.RequestStatus;
import getrest.android.core.Response;
import getrest.android.core.ResponseParcel;

import getrest.android.service.CallerContext;
import getrest.android.service.DefaultGetrestService;
import getrest.android.service.GetrestService;
import getrest.android.service.GetrestServiceBinder;
import getrest.android.service.RequestEventBus;
import getrest.android.service.RequestStateChangeEventWrapper;

import getrest.android.util.Preconditions;
import getrest.android.util.TypeLiteral;
import getrest.android.util.WorkerQueue;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class GetrestClientImpl extends GetrestClient implements RequestEventHandler {

    private GetrestRuntime runtime;
    private Context androidContext;
    private RequestEventBroadcastReceiver requestEventReceiver;
    private final Map<String, ResponseImpl> futureMap = new ConcurrentHashMap<String, ResponseImpl>();
    private final WorkerQueue<RequestEventRecord> eventQueue = new WorkerQueue<RequestEventRecord>(
        new LinkedList<RequestEventRecord>(),
        new RequestEventRecordWorker(this),
        5);
    private final AtomicReference<RequestRegistry> requestRegistry = new AtomicReference<RequestRegistry>();
    private boolean isStarted;
    private Class<?extends GetrestService> serviceClass = DefaultGetrestService.class;
    private GetrestServiceConnection serviceConnection;
    private final CallerContextAdapter callerContextAdapter = new CallerContextAdapter();

    public GetrestClientImpl() {}

    private Response obtainStoredRequestFuture(final String requestId) {

        final RequestRegistry requestRegistry = getRequestRegistry();
        final RequestRegistry.Entry entry = requestRegistry.getEntry(requestId);

        if (entry == null) {
            throw new IllegalStateException("Request is not registered: " + requestId);
        }

        final RequestManager requestManager = runtime.getRequestManager();
        final Request request = requestManager.getRequest(requestId);

        return obtainRequestFuture(request);
    }

    @Override
    public <V> V executeWithResult(final Request<V> request) {

        return execute(request).get();
    }

    @Override
    public <R extends Request<?extends V>, V> RequestFuture<V> execute(final Request<V> request) {
        Preconditions.checkArgNotNull(request, "request");

        return serviceConnection.getService().execute(request, callerContextAdapter);
    }

    @Override
    public <R> R execute(final Request request, final Class<R> responseType) {

        return execute(request, TypeLiteral.fromClass(responseType));
    }

    @Override
    public <V> V execute(final Request request, final TypeLiteral<V> responseTypeLiteral) {
        Loggers.getClientLogger().trace("Calling {0}: requestId={1}, request={2}",
                                        DefaultGetrestService.class.getSimpleName(),
                                        request.getRequestId(),
                                        request);

        return (V) serviceConnection.getService().execute(request, callerContextAdapter);
    }

    private RequestRegistry getRequestRegistry() {

        if (requestRegistry.get() == null) {

            synchronized (requestRegistry) {

                if (requestRegistry.get() == null) {

                    if (!(androidContext instanceof Activity)) {
                        throw new IllegalStateException("Context must be an Activity");
                    }

                    requestRegistry.set(new RequestRegistryPreferencesImpl((Activity) androidContext));
                }
            }
        }

        return requestRegistry.get();
    }

    private ResponseImpl obtainRequestFuture(final Request request) {

        final String requestId = request.getRequestId();

        final ResponseImpl requestFuture = new ResponseImpl();
        requestFuture.setRequestId(requestId);
        requestFuture.setRequest(request);

        futureMap.put(requestId, requestFuture);

        final RequestCallbackFactory callbackFactory = getRequestCallbackFactory();

        if (callbackFactory != null) {

            final RequestCallback callback = callbackFactory.createCallback(request);

            if (callback != null) {
                requestFuture.setRequestCallback(callback);
            }
        }

        return requestFuture;
    }

    /**
     * Obtain request future for previously registered request.
     *
     * @param entry
     *
     * @return
     */
    private ResponseImpl obtainRequestFuture(final RequestRegistry.Entry entry) {

        final RequestManager requestManager = runtime.getRequestManager();
        final Request request = requestManager.getRequest(entry.getRequestId());

        if (request == null) {
            Loggers.getClientLogger().warn("Request [{0}] is not acknowledged by request manager",
                                           entry.getRequestId());

            return null;
        } else {

            return obtainRequestFuture(request);
        }
    }

    protected final void init(final Context context) {
        this.runtime = GetrestRuntime.getInstance(context);

        this.androidContext = context;
        this.requestEventReceiver = new RequestEventBroadcastReceiver(this);

        serviceClass = DefaultGetrestService.class;

        final Intent intent = new Intent(context, serviceClass);
        context.startService(intent);

        serviceConnection = new GetrestServiceConnection();

        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        context.registerReceiver(requestEventReceiver,
                                 new IntentFilter(
            RequestEventBus.Intents.REQUEST_STATE_CHANGE_EVENT_ACTION));
    }

    @Override
    public void detach() {
        androidContext.unbindService(serviceConnection);
        androidContext.unregisterReceiver(requestEventReceiver);
        requestEventReceiver = null;
    }

    @Override
    public void replay() {

        final Set<RequestRegistry.Entry> entries = getRequestRegistry().getEntries();

        if (futureMap.isEmpty() && !entries.isEmpty()) {

            for (final RequestRegistry.Entry entry : entries) {
                obtainRequestFuture(entry);
            }
        }

        start();
    }

    private void start() {
        eventQueue.start();
        isStarted = true;
    }

    private static class RequestEventBroadcastReceiver extends BroadcastReceiver {

        private final GetrestClientImpl client;

        private RequestEventBroadcastReceiver(final GetrestClientImpl client) {
            this.client = client;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            client.onRequestEvent(intent);
        }
    }

    private void onRequestEvent(final Intent intent) {

        final RequestStateChangeEventWrapper stateChangeEventWrapper = new RequestStateChangeEventWrapper(
            intent);

        final String requestId = stateChangeEventWrapper.getRequestId();

        final RequestStatus requestStatus = stateChangeEventWrapper.getRequestState();
        Loggers.getClientLogger().debug("Request event received: requestId={0}, requestStatus={1}",
                                        requestId,
                                        requestStatus);

        final RequestEventRecord eventRecord = new RequestEventRecord();
        eventRecord.setRequestId(requestId);
        eventRecord.setRequestStatus(requestStatus);

        eventQueue.add(eventRecord);
    }

    public void handleRequestEvent(final RequestEventRecord eventRecord) {
        Preconditions.checkState(getCallbackHandler() != null, "Callback handler cannot be null");

        synchronized (futureMap) {

            final ResponseImpl future = futureMap.get(eventRecord.getRequestId());

            final RequestStatus requestStatus = eventRecord.getRequestStatus();

            if (future == null) {
                Loggers.getClientLogger().warn(
                    "Request event [{0}] cannot be processed, because request [{1}] is not registered",
                    eventRecord,
                    eventRecord.getRequestId());
            } else if (RequestStatus.PENDING.equals(requestStatus)) {
                getCallbackHandler().post(new RequestPendingRunnable(future));
            } else if (RequestStatus.EXECUTING.equals(requestStatus)) {
                getCallbackHandler().post(new RequestExecutingRunnable(future));
            } else if (RequestStatus.FINISHED.equals(requestStatus)) {
                getCallbackHandler().post(new RequestFinishedRunnable(future, eventRecord, this));
            } else if (RequestStatus.ERROR.equals(requestStatus)) {
                getCallbackHandler().post(new RequestErrorRunnable(future));
            }
        }
    }

    private static class RequestEventRecordWorker implements WorkerQueue.Worker<RequestEventRecord> {

        private RequestEventHandler client;

        private RequestEventRecordWorker(final RequestEventHandler client) {
            this.client = client;
        }

        public void execute(final RequestEventRecord item) {
            client.handleRequestEvent(item);
        }
    }

    private static class RequestPendingRunnable implements Runnable {

        private final ResponseImpl future;

        public RequestPendingRunnable(final ResponseImpl future) {
            this.future = future;
        }

        public void run() {
            future.firePending();
        }
    }

    private static class RequestExecutingRunnable implements Runnable {

        private final ResponseImpl future;

        public RequestExecutingRunnable(final ResponseImpl future) {
            this.future = future;
        }

        public void run() {
            future.fireExecuting();
        }
    }

    private static class RequestFinishedRunnable implements Runnable {

        private final ResponseImpl future;
        private final RequestEventRecord eventRecord;
        private final GetrestClientImpl client;

        public RequestFinishedRunnable(final ResponseImpl future,
                                       final RequestEventRecord eventRecord,
                                       final GetrestClientImpl client) {
            this.future = future;
            this.eventRecord = eventRecord;
            this.client = client;
        }

        public void run() {

            final String requestId = eventRecord.getRequestId();

            try {
                future.fireFinished(eventRecord.<ResponseParcel>getData());
            } finally {
                // TODO rewrite in a nicer manner here
                client.releaseRequest(requestId);
            }
        }
    }

    private static class GetrestServiceConnection implements ServiceConnection {

        private GetrestServiceBinder serviceBinder;
        public final Object waitLock = new Object();

        public GetrestServiceConnection() {}

        public void onServiceConnected(final ComponentName componentName, final IBinder iBinder) {

            synchronized (waitLock) {
                serviceBinder = (GetrestServiceBinder) iBinder;

                Loggers.getClientLogger()
                .debug("Service bound: {0}", serviceBinder.getGetrestService());

                waitLock.notifyAll();
            }
        }

        public void onServiceDisconnected(final ComponentName componentName) {

            synchronized (waitLock) {
                serviceBinder = null;

                Loggers.getClientLogger()
                .debug("Service unbound: {0}", serviceBinder.getGetrestService());

                waitLock.notifyAll();
            }
        }

        public GetrestService getService() {

            while (serviceBinder == null) {

                synchronized (waitLock) {

                    while (serviceBinder == null) {

                        try {
                            waitLock.wait();
                        } catch (final InterruptedException ex) {
                            throw new IllegalStateException("Awaiting for service binding interrupted",
                                                            ex);
                        }
                    }
                }
            }

            return serviceBinder.getGetrestService();
        }
    }

    private class RequestErrorRunnable implements Runnable {

        private final ResponseImpl future;

        public RequestErrorRunnable(final ResponseImpl future) {
            this.future = future;
        }

        public void run() {
            future.fireError();
        }
    }

    private void releaseRequest(final String requestId) {

        synchronized (this) {
            futureMap.remove(requestId);

            final RequestRegistry.Editor editor = getRequestRegistry().edit();
            editor.remove(requestId);
            editor.commit();
        }
    }

    private class CallerContextAdapter implements CallerContext {
        public Handler getHandler() {

            return getCallbackHandler();
        }
    }
}


interface RequestEventHandler {
    void handleRequestEvent(final RequestEventRecord eventRecord);
}
