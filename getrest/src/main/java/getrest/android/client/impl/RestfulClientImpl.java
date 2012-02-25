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
import android.net.Uri;
import android.os.Handler;
import getrest.android.RestfulClient;
import getrest.android.client.RequestCallback;
import getrest.android.client.RequestCallbackFactory;
import getrest.android.client.RequestFuture;
import getrest.android.client.RequestStore;
import getrest.android.config.Config;
import getrest.android.config.ConfigResolver;
import getrest.android.entity.Pack;
import getrest.android.entity.Packer;
import getrest.android.request.Method;
import getrest.android.request.Request;
import getrest.android.request.RequestController;
import getrest.android.request.RequestState;
import getrest.android.request.Response;
import getrest.android.resource.ResourceContext;
import getrest.android.service.RequestEventBus;
import getrest.android.service.RequestStateChangeEventWrapper;
import getrest.android.service.RequestWrapper;
import getrest.android.service.RestService;
import getrest.android.service.ServiceContext;
import getrest.android.util.Logger;
import getrest.android.util.LoggerFactory;
import getrest.android.util.WorkerQueue;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class RestfulClientImpl extends RestfulClient {

    private static final Logger LOGGER = LoggerFactory.getLogger("getrest.client");

    private Config config;

    private String serviceId;

    private Context androidContext;

    private RequestEventBroadcastReceiver requestEventReceiver;

    private Handler callbackHandler;

    private final Map<String, RequestFutureImpl> futureMap = new ConcurrentHashMap<String, RequestFutureImpl>();

    private final Set<String> replayedRequestIds = new HashSet<String>();

    private final WorkerQueue<RequestEventRecord> eventQueue = new WorkerQueue<RequestEventRecord>(
            new LinkedList<RequestEventRecord>(),
            new RequestEventRecordWorker(this), 5);

    private final AtomicReference<RequestStore> requestStore = new AtomicReference<RequestStore>();

    private boolean isStarted;

    protected final void setServiceId(final String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public void setCallbackHandler(final Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    public RequestFuture getRequestFuture(String requestId) {
        final RequestFuture future;
        if (isStarted) {
            future = futureMap.get(requestId);
        } else {
            future = obtainStoredRequestFuture(requestId);
        }
        return future;
    }

    private RequestFuture obtainStoredRequestFuture(final String requestId) {
        // TODO obtain Request instance for previously stored ids
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> RequestFuture post(Uri url, T entity) {
        final String requestId = nextRequestId();

        LOGGER.debug("POST: requestId={0}, url={1}, entity={2}", requestId, url, entity);

        final ServiceContext serviceContext = ServiceContext.forServiceId(serviceId);

        final ResourceContext resourceContext = serviceContext.getResourceContext(url, Method.POST);
        final Packer packer = resourceContext.getPacker();
        final Pack<T> pack = packer.pack(entity);

        final Request request = new Request();
        request.setUri(url);
        request.setMethod(Method.POST);
        request.setEntity(pack);
        request.setRequestId(requestId);
        request.setTimestamp(System.currentTimeMillis());

        LOGGER.trace("Starting service");

        final RequestWrapper wrapper = new RequestWrapper(new Intent(androidContext, RestService.class));
        wrapper.setRequest(request);

        final RequestController requestController = resourceContext.getRequestController();
        requestController.prepareRequest(request);

        getRequestStore().put(requestId);

        final RequestFutureImpl requestFuture = obtainRequestFuture(requestId, request);

        androidContext.startService(wrapper.asIntent());

        return requestFuture;
    }

    private RequestStore getRequestStore() {
        if (requestStore.get() == null) {
            synchronized (requestStore) {
                if (requestStore.get() == null) {
                    if (!(androidContext instanceof Activity)) {
                        throw new IllegalStateException("Context must be an Activity");
                    }
                    requestStore.set(new RequestStorePreferencesImpl((Activity) androidContext));
                }
            }
        }
        return requestStore.get();
    }

    private RequestFutureImpl obtainRequestFuture(final String requestId, final Request request) {
        final RequestFutureImpl requestFuture = new RequestFutureImpl();
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

    private String nextRequestId() {
        return UUID.randomUUID().toString();
    }

    protected final void init(Context context) {
        this.config = ConfigResolver.getInstance().obtainConfig(context);

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
    public RequestFuture get(Uri url) {
        // TODO implement GET method
        throw new UnsupportedOperationException();
    }

    @Override
    public RequestFuture delete(Uri url) {
        // TODO implement DELETE method
        throw new UnsupportedOperationException();
    }

    @Override
    public void replay() {
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

        final RequestState requestState = stateChangeEventWrapper.getRequestState();
        LOGGER.debug("Request event received: requestId={0}, requestState={1}", requestId,
                requestState);

        final RequestEventRecord eventRecord = new RequestEventRecord();
        eventRecord.setRequestId(requestId);
        eventRecord.setRequestState(requestState);

        eventQueue.add(eventRecord);
    }

    private void handleRequestEvent(final RequestEventRecord eventRecord) {
        synchronized (futureMap) {
            final RequestFutureImpl future = futureMap.get(eventRecord.getRequestId());

            final RequestState requestState = eventRecord.getRequestState();
            if (future == null) {
                LOGGER.warn("Request id " + eventRecord.getRequestId() + " is not registered");
            } else if (RequestState.PENDING.equals(requestState)) {
                callbackHandler.post(new RequestPendingRunnable(future));
            } else if (RequestState.EXECUTING.equals(requestState)) {
                callbackHandler.post(new RequestExecutingRunnable(future));
            } else if (RequestState.FINISHED.equals(requestState)) {
                callbackHandler.post(new RequestFinishedRunnable(future, eventRecord, this));
            }
        }
    }

    private static class RequestEventRecordWorker implements WorkerQueue.Worker<RequestEventRecord> {

        private RestfulClientImpl client;

        private RequestEventRecordWorker(final RestfulClientImpl client) {
            this.client = client;
        }

        public void execute(final RequestEventRecord item) {
            client.handleRequestEvent(item);
        }
    }

    private static class RequestPendingRunnable implements Runnable {
        private final RequestFutureImpl future;

        public RequestPendingRunnable(final RequestFutureImpl future) {
            this.future = future;
        }

        public void run() {
            future.firePending();
        }
    }

    private static class RequestExecutingRunnable implements Runnable {
        private final RequestFutureImpl future;

        public RequestExecutingRunnable(final RequestFutureImpl future) {
            this.future = future;
        }

        public void run() {
            future.fireExecuting();
        }
    }

    private static class RequestFinishedRunnable implements Runnable {

        private final RequestFutureImpl future;
        private final RequestEventRecord eventRecord;
        private RestfulClientImpl client;

        public RequestFinishedRunnable(final RequestFutureImpl future, final RequestEventRecord eventRecord,
                                       final RestfulClientImpl client) {
            this.future = future;
            this.eventRecord = eventRecord;
            this.client = client;
        }

        public void run() {
            final String requestId = eventRecord.getRequestId();
            try {
                future.fireFinished(eventRecord.<Response>getData());
            } finally {
                // TODO rewrite in a nicer manner here
                client.releaseRequest(requestId);
            }
        }
    }

    private void releaseRequest(final String requestId) {
        synchronized (this) {
            futureMap.remove(requestId);
            getRequestStore().remove(requestId);
        }
    }

}
