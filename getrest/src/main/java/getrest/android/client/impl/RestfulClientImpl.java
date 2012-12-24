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
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import getrest.android.RestfulClient;
import getrest.android.client.RequestCallback;
import getrest.android.client.RequestCallbackFactory;
import getrest.android.client.RequestExecutor;
import getrest.android.client.RequestRegistry;
import getrest.android.core.Loggers;
import getrest.android.core.Request;
import getrest.android.core.RequestParcel;
import getrest.android.core.RequestSupport;
import getrest.android.core.Response;
import getrest.android.core.ResponseParcelable;
import getrest.android.core.RequestManager;
import getrest.android.core.RequestStatus;
import getrest.android.core.GetrestRuntime;
import getrest.android.service.RequestEventBus;
import getrest.android.service.RequestStateChangeEventWrapper;
import getrest.android.service.RequestWrapper;
import getrest.android.service.RestService;
import getrest.android.util.Logger;
import getrest.android.util.Preconditions;
import getrest.android.util.TypeLiteral;
import getrest.android.util.WorkerQueue;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class RestfulClientImpl extends RestfulClient implements RequestExecutor, RequestEventHandler {

    private static final Logger LOGGER = Loggers.getClientLogger();

    private GetrestRuntime runtime;

    private Context androidContext;

    private RequestEventBroadcastReceiver requestEventReceiver;

    private final Map<String, ResponseImpl> futureMap = new ConcurrentHashMap<String, ResponseImpl>();

    private final WorkerQueue<RequestEventRecord> eventQueue = new WorkerQueue<RequestEventRecord>(
            new LinkedList<RequestEventRecord>(),
            new RequestEventRecordWorker(this), 5);

    private final AtomicReference<RequestRegistry> requestRegistry = new AtomicReference<RequestRegistry>();

    private boolean isStarted;

    public RestfulClientImpl() {
    }

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

    public void execute(final Request request) {
        throw new UnsupportedOperationException();
    }

    public <R> R execute(final Request request, final Class<R> responseType) {

        LOGGER.trace("Starting service");

        final RequestSupport requestSupport = runtime.getRequestSupport(request);
        final RequestParcel parcel = requestSupport.getRequestParcel();

        final RequestManager requestManager = runtime.getRequestManager();
        requestManager.saveRequest(request);

        RequestRegistry.Editor editor = getRequestRegistry().edit();
        editor.put(request);
        editor.commit();

        final RequestWrapper wrapper = new RequestWrapper(new Intent(androidContext, RestService.class));
        wrapper.setRequestParcel(parcel);

        androidContext.startService(wrapper.asIntent());

        return (R) requestSupport.createResponse(responseType);
    }

    public <R> R execute(final Request request, final TypeLiteral<R> responseTypeLiteral) {
        throw new UnsupportedOperationException();
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
     * @return
     */
    private ResponseImpl obtainRequestFuture(final RequestRegistry.Entry entry) {
        final RequestManager requestManager = runtime.getRequestManager();
        final Request request = requestManager.getRequest(entry.getRequestId());
        if (request == null) {
            LOGGER.warn("Request [{0}] is not acknowledged by request manager", entry.getRequestId());
            return null;
        } else {
            return obtainRequestFuture(request);
        }
    }

    protected final void init(Context context) {
        this.runtime = GetrestRuntime.getInstance(context);

        this.androidContext = context;
        this.requestEventReceiver = new RequestEventBroadcastReceiver(this);

        context.registerReceiver(requestEventReceiver,
                new IntentFilter(RequestEventBus.Intents.REQUEST_STATE_CHANGE_EVENT_ACTION));
    }

    @Override
    public void detach() {
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

    public void start() {
        eventQueue.start();
        isStarted = true;
    }

    private static class RequestEventBroadcastReceiver extends BroadcastReceiver {

        private final RestfulClientImpl client;

        private RequestEventBroadcastReceiver(final RestfulClientImpl client) {
            this.client = client;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            client.onRequestEvent(intent);
        }

    }

    private void onRequestEvent(final Intent intent) {
        final RequestStateChangeEventWrapper stateChangeEventWrapper = new RequestStateChangeEventWrapper(intent);

        final String requestId = stateChangeEventWrapper.getRequestId();

        final RequestStatus requestStatus = stateChangeEventWrapper.getRequestState();
        LOGGER.debug("Request event received: requestId={0}, requestStatus={1}", requestId,
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
                LOGGER.warn("Request event [{0}] cannot be processed, because request [{1}] is not registered",
                        eventRecord, eventRecord.getRequestId());
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
        private final RestfulClientImpl client;

        public RequestFinishedRunnable(final ResponseImpl future, final RequestEventRecord eventRecord,
                                       final RestfulClientImpl client) {
            this.future = future;
            this.eventRecord = eventRecord;
            this.client = client;
        }

        public void run() {
            final String requestId = eventRecord.getRequestId();
            try {
                future.fireFinished(eventRecord.<ResponseParcelable>getData());
            } finally {
                // TODO rewrite in a nicer manner here
                client.releaseRequest(requestId);
            }
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

}

interface RequestEventHandler {
    void handleRequestEvent(RequestEventRecord eventRecord);
}
