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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import getrest.android.RestfulClient;
import getrest.android.client.RequestFuture;
import getrest.android.entity.Pack;
import getrest.android.entity.Packer;
import getrest.android.request.Method;
import getrest.android.request.Request;
import getrest.android.resource.ResourceContext;
import getrest.android.service.RequestEventBus;
import getrest.android.service.RequestEventWrapper;
import getrest.android.service.RequestEvents;
import getrest.android.service.RequestWrapper;
import getrest.android.service.RestService;
import getrest.android.service.ServiceContext;
import getrest.android.util.Logger;
import getrest.android.util.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RestfulClientImpl extends RestfulClient {

    private static final Logger LOGGER = LoggerFactory.getLogger("getrest.client");

    private String serviceId;

    private Context androidContext;

    private RequestEventBroadcastReceiver requestEventReceiver;

    private final Map<String, RequestFutureImpl> futureMap = new ConcurrentHashMap<String, RequestFutureImpl>();

    protected final void setServiceId(final String serviceId) {
        this.serviceId = serviceId;
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

        final RequestWrapper wrapper = new RequestWrapper(new Intent(androidContext, RestService.class));
        wrapper.setRequest(request);

        LOGGER.trace("Starting service");

        final RequestFutureImpl requestFuture = new RequestFutureImpl();
        requestFuture.setRequest(request);
        futureMap.put(requestId, requestFuture);

        androidContext.startService(wrapper.asIntent());

        return requestFuture;
    }

    private String nextRequestId() {
        return UUID.randomUUID().toString();
    }

    protected final void init(Context context) {
        this.androidContext = context;
        this.requestEventReceiver = new RequestEventBroadcastReceiver(this);

        context.registerReceiver(requestEventReceiver, new IntentFilter(RequestEventBus.Intents.REQUEST_EVENT_ACTION));
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

    private static class RequestEventBroadcastReceiver extends BroadcastReceiver {

        private final RestfulClientImpl client;

        private RequestEventBroadcastReceiver(final RestfulClientImpl client) {
            this.client = client;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            client.onRequestEvent(context, intent);
        }

    }

    private void onRequestEvent(final Context context, final Intent intent) {
        final RequestEventWrapper eventWrapper = new RequestEventWrapper(intent);

        final String requestId = eventWrapper.getRequestId();

        LOGGER.debug("Request event received: requestId={0}, eventType={1}", requestId,
                RequestEvents.getEventName(eventWrapper.getEventType()));

        synchronized (futureMap) {
            final RequestFutureImpl future = futureMap.get(requestId);
            if (future == null) {
                LOGGER.warn("Request id " + requestId + " is not acknowledged");
            } else {
                switch (eventWrapper.getEventType()) {
                    case RequestEvents.PENDING:
                        future.firePending();
                        break;

                    case RequestEvents.EXECUTING:
                        future.fireExecuting();
                        break;

                    case RequestEvents.FINISHED:
                        try {
                            future.fireFinished(eventWrapper.getResponse());
                        } finally {
                            synchronized (future) {
                                futureMap.remove(requestId);
                            }
                        }
                        break;
                }
            }
        }
    }

}
