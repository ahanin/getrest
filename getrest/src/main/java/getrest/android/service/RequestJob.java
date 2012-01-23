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

package getrest.android.service;

import getrest.android.request.Handler;
import getrest.android.request.HandlerException;
import getrest.android.request.Request;
import getrest.android.request.Response;
import getrest.android.request.Status;

/**
 * @author aha
 * @since 2012-01-13
 */
public class RequestJob implements Runnable {

    private Request request;

    private RequestCallback callback;
    private RequestEventBus requestEventBus;

    public RequestJob(final Request request) {
        this.request = request;
    }

    public void setCallback(final RequestCallback callback) {
        this.callback = callback;
    }

    public void setRequestEventBus(final RequestEventBus requestEventBus) {
        this.requestEventBus = requestEventBus;
    }

    public void run() {
        final String requestId = request.getRequestId();
        requestEventBus.fireExecuting(requestId);

        final Response response = new Response();
        response.setRequest(request);

        try {
            final ServiceContext serviceContext = ServiceContext.forRequest(request);
            final Handler requestProcessor = serviceContext.getRequestHandler(request);
            requestProcessor.handle(request, response);
            if (callback != null) {
                callback.onResponse(response);
            }
        } catch (HandlerException e) {
            response.setStatus(Status.UNEXPECTED_EXCEPTION);
        } finally {
            requestEventBus.fireFinished(requestId);
        }
    }

}
