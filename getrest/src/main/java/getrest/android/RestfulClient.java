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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import getrest.android.entity.Pack;
import getrest.android.request.Method;
import getrest.android.request.Request;
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

    private static final Logger LOGGER = LoggerFactory.getLogger("GetRest:RestfulClient");

    private Context androidContext;

    private ServiceContext serviceContext;

    public void setContext(Context context) {
        this.androidContext = context;
    }

    public void setServiceContext(final ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
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

        LOGGER.debug("POST: requestId={}, url={}, entity={}", requestId, url, entity);

        final Pack<T> pack = serviceContext.pack(url, Method.POST, entity);

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
        throw new UnsupportedOperationException();
    }

    /**
     * Pushes a DELETE request for processing.
     *
     * @param url resource url
     * @return unique request id
     */
    public String delete(Uri url) {
        throw new UnsupportedOperationException();
    }

}
