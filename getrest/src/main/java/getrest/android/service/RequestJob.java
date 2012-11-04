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

import getrest.android.core.ErrorState;
import getrest.android.executor.RequestHandlerFactory;
import getrest.android.executor.Handler;
import getrest.android.core.HandlerException;
import getrest.android.core.Request;
import getrest.android.request.RequestContext;
import getrest.android.request.RequestManager;
import getrest.android.request.RequestStatus;
import getrest.android.core.ResponseParcelable;
import getrest.android.resource.ResourceContext;

import java.io.IOException;

/**
 * @author aha
 * @since 2012-01-13
 */
public class RequestJob implements Runnable {

    private Request request;

    private RequestContext requestContext;

    private RequestEventBus requestEventBus;

    public RequestJob(final Request request) {
        this.request = request;
    }

    public void setRequestContext(final RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    public void setRequestEventBus(final RequestEventBus requestEventBus) {
        this.requestEventBus = requestEventBus;
    }

    public void run() {
        final String requestId = request.getRequestId();

        final ResponseParcelable responseParcelable = new ResponseParcelable();
        final ResourceContext resourceContext = requestContext.getResourceContext();

        final RequestHandlerFactory requestHandlerFactory = resourceContext.getRequestHandlerFactory();
        final Handler requestHandler = requestHandlerFactory.getRequestHandler(request);

        final RequestManager requestManager = requestContext.getRequestManager();
        requestManager.setRequestState(requestId, RequestStatus.EXECUTING);

        requestEventBus.fireExecuting(requestId);

        try {
            requestHandler.handle(request, responseParcelable);
            requestManager.saveResponse(requestId, responseParcelable);
            requestManager.setRequestState(requestId, RequestStatus.FINISHED);
            requestEventBus.fireFinished(requestId);
        } catch (HandlerException e) {
            final StringBuilder message = new StringBuilder(e.getMessage());
            final ErrorState errorState;
            if (e.getCause() instanceof IOException) {
                errorState = ErrorState.IO;
            } else {
                errorState = ErrorState.UNKNOWN;
            }

            if (e.getCause() != null) {
                message.append('|').append(e.getCause().getMessage());
            }

            requestEventBus.fireError(requestId);
            requestManager.setRequestState(requestId, errorState, message.toString());
        }

    }

}
