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
import getrest.android.client.RequestExecutor;
import getrest.android.client.RequestFuture;
import getrest.android.client.RequestRegistry;
import getrest.android.config.Config;
import getrest.android.config.ConfigResolver;
import getrest.android.core.Header;
import getrest.android.core.Pack;
import getrest.android.core.Method;
import getrest.android.core.Request;
import getrest.android.request.RequestManager;
import getrest.android.request.RequestStatus;
import getrest.android.core.Response;
import getrest.android.resource.ResourceContext;
import getrest.android.service.RequestEventBus;
import getrest.android.service.RequestStateChangeEventWrapper;
import getrest.android.service.RequestWrapper;
import getrest.android.service.RestService;
import getrest.android.util.Logger;
import getrest.android.util.LoggerFactory;
import getrest.android.util.WorkerQueue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class RestfulClientImpl extends RestfulClient implements RequestExecutor, RequestEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger("getrest.client");

    private Config config;

    private Context androidContext;

    private RequestEventBroadcastReceiver requestEventReceiver;

    private Handler callbackHandler = new Handler();

    private final Map<String, RequestFutureImpl> futureMap = new ConcurrentHashMap<String, RequestFutureImpl>();

    private final WorkerQueue<RequestEventRecord> eventQueue = new WorkerQueue<RequestEventRecord>(
            new LinkedList<RequestEventRecord>(),
            new RequestEventRecordWorker(this), 5);

    private final AtomicReference<RequestRegistry> requestRegistry = new AtomicReference<RequestRegistry>();

    private boolean isStarted;

    @Override
    public void setCallbackHandler(final Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    /**
     * Return {@link RequestFuture}. Unless client is started by calling {@link #replay()}, this method will return
     * futures for currently executing requests, otherwise new synthetic future will be created and will be updated
     * once the client is started.
     *
     * @param requestId request id
     * @return {@link RequestFuture} for currently executing requests
     */
    @Override
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
        final RequestRegistry requestRegistry = getRequestRegistry();
        final RequestRegistry.Entry entry = requestRegistry.getEntry(requestId);
        if (entry == null) {
            throw new IllegalStateException("Request is not registered: " + requestId);
        }

        final ResourceContext resourceContext = config.getResourceContext(entry.getResourceUri());
        final RequestManager requestManager = resourceContext.getRequestManager();
        final Request request = requestManager.getRequest(requestId);

        return obtainRequestFuture(request);
    }

    public RequestFuture execute(final Request request) {

        final ResourceContext resourceContext = config.getResourceContext(request.getUri());

        LOGGER.trace("Starting service");

        final RequestWrapper wrapper = new RequestWrapper(new Intent(androidContext, RestService.class));
        wrapper.setRequest(request);

        final RequestManager requestManager = resourceContext.getRequestManager();
        requestManager.saveRequest(request);

        RequestRegistry.Editor editor = getRequestRegistry().edit();
        editor.put(request);
        editor.commit();

        final RequestFutureImpl requestFuture = obtainRequestFuture(request);

        androidContext.startService(wrapper.asIntent());

        return requestFuture;
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

    private RequestFutureImpl obtainRequestFuture(final Request request) {
        final String requestId = request.getRequestId();

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

    /**
     * Obtain request future for previously registered request.
     *
     * @param entry
     * @return
     */
    private RequestFutureImpl obtainRequestFuture(final RequestRegistry.Entry entry) {
        final Uri uri = entry.getResourceUri();
        final ResourceContext resourceContext = config.getResourceContext(uri);
        final RequestManager requestManager = resourceContext.getRequestManager();
        final Request request = requestManager.getRequest(entry.getRequestId());
        if (request == null) {
            LOGGER.warn("Request [{0}, {1}] is not acknowledged by request manager", entry.getRequestId(), entry.getResourceUri());
            return null;
        } else {
            return obtainRequestFuture(request);
        }
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
    public RequestBuilder request(final Uri uri) {
        return new RequestBuilderImpl(this, this.config).uri(uri);
    }

    @Override
    public <T> RequestFuture post(final Uri url, final T entity) {
        return request(url)
                .method(Method.POST)
                .entity(entity)
                .execute();
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
        synchronized (futureMap) {
            final RequestFutureImpl future = futureMap.get(eventRecord.getRequestId());

            final RequestStatus requestStatus = eventRecord.getRequestStatus();
            if (future == null) {
                LOGGER.warn("Request id " + eventRecord.getRequestId() + " is not registered");
            } else if (RequestStatus.PENDING.equals(requestStatus)) {
                callbackHandler.post(new RequestPendingRunnable(future));
            } else if (RequestStatus.EXECUTING.equals(requestStatus)) {
                callbackHandler.post(new RequestExecutingRunnable(future));
            } else if (RequestStatus.FINISHED.equals(requestStatus)) {
                callbackHandler.post(new RequestFinishedRunnable(future, eventRecord, this));
            } else if (RequestStatus.ERROR.equals(requestStatus)) {
                callbackHandler.post(new RequestErrorRunnable(future));
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

    private class RequestErrorRunnable implements Runnable {

        private final RequestFutureImpl future;

        public RequestErrorRunnable(final RequestFutureImpl future) {
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

    private static class RequestBuilderImpl implements RequestBuilder {

        private Uri uri;
        private Method method;
        private List<Header> headers = new ArrayList<Header>();
        private Object entity;

        private final RequestExecutor requestExecutor;
        private final Config config;

        private RequestBuilderImpl(final RequestExecutor requestExecutor, Config config) {
            this.requestExecutor = requestExecutor;
            this.config = config;
        }

        public RequestBuilder uri(final Uri uri) {
            this.uri = uri;
            return this;
        }

        public RequestBuilder method(final Method method) {
            this.method = method;
            return this;
        }

        public RequestBuilder header(final String name, final String value) {
            this.headers.add(new Header(name, value));
            return this;
        }

        public <T> RequestBuilder entity(final T entity) {
            this.entity = entity;
            return this;
        }

        public RequestFuture execute() {
            if (this.uri == null) {
                throw new IllegalStateException("URI is not set");
            }

            if (this.method == null) {
                throw new IllegalStateException("Method is not set");
            }

            final ResourceContext resourceContext = config.getResourceContext(uri);

            final Request request = new Request();
            request.setRequestId(nextRequestId());
            request.setUri(this.uri);
            request.setMethod(this.method);
            request.getHeaders().addAll(headers);
            request.setTimestamp(System.currentTimeMillis());

            if (this.entity != null) {
                final Pack pack = resourceContext.getPacker().pack(this.entity);
                request.setEntity(pack);
            }

            return requestExecutor.execute(request);
        }

        private String nextRequestId() {
            return UUID.randomUUID().toString();
        }

    }
}

interface RequestEventHandler {
    void handleRequestEvent(RequestEventRecord eventRecord);
}
