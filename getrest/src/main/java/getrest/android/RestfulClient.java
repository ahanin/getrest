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

package getrest.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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

import java.util.UUID;

/**
 * @author aha
 * @since 2012-01-13
 */
public abstract class RestfulClient {

    private static final Logger LOGGER = LoggerFactory.getLogger("getrest.client");

    private String serviceId;

    private Context androidContext;

    private RequestEventBroadcastReceiver requestEventReceiver;

    protected final void setServiceId(final String serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Pushes a POST request for processing.
     *
     * @param url    resource url
     * @param entity entity object to be posted
     * @param <T>    entity type
     * @return unique request id
     */
    public <T> String post(Uri url, T entity) {
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

        androidContext.startService(wrapper.asIntent());

        return requestId;
    }

    private String nextRequestId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Pushes a GET request for processing.
     *
     * @param url resource url
     * @return unique request id
     */
    public String get(Uri url) {
        // TODO implement GET method
        throw new UnsupportedOperationException();
    }

    /**
     * Pushes a DELETE request for processing.
     *
     * @param url resource url
     * @return unique request id
     */
    public String delete(Uri url) {
        // TODO implement DELETE method
        throw new UnsupportedOperationException();
    }

    protected final void init(Context context) {
        this.androidContext = context;
        this.requestEventReceiver = new RequestEventBroadcastReceiver();

        context.registerReceiver(requestEventReceiver, new IntentFilter(RequestEventBus.Intents.REQUEST_EVENT_ACTION));
    }

    private static class RequestEventBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final RequestEventWrapper eventWrapper = new RequestEventWrapper(intent);
            LOGGER.debug("Request event received: requestId={0}, eventType={1}", eventWrapper.getRequestId(),
                    RequestEvents.getEventName(eventWrapper.getEventType()));
        }
    }

    /**
     * Create new instance of {@link RestfulClient} and attaches it to the given {@link Context}. When client is no
     * longer needed, {@link #detach()} must be called for clean-up and for releasing all retained resources.
     *
     * @param context
     * @return
     * @see #detach()
     */
    public static RestfulClient getInstance(Context context) {
        final RestfulClientImpl client = new RestfulClientImpl();
        client.init(context);
        return client;
    }

    /**
     * Detach {@link RestfulClient} and release all retained resources.
     */
    public void detach() {
        androidContext.unregisterReceiver(requestEventReceiver);

        requestEventReceiver = null;
    }

    private static class RestfulClientImpl extends RestfulClient {
    }

}
