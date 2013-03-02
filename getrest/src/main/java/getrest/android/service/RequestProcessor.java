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

import getrest.android.core.Loggers;
import getrest.android.core.RequestManager;
import getrest.android.core.RequestStatus;

import getrest.android.event.RequestCompletedEvent;
import getrest.android.event.RequestErrorEvent;
import getrest.android.event.RequestExecutingEvent;
import getrest.android.event.RequestPendingEvent;

import getrest.android.util.InstanceProvider;
import getrest.android.util.Provider;
import getrest.android.util.WorkerQueue;

class RequestProcessor implements WorkerQueue.Worker<RequestTuple> {

    private final Provider<GetrestService> getrestServiceProvider;
    private final Provider<RequestManager> requestManagerProvider;

    public RequestProcessor(final Provider<GetrestService> getrestServiceProvider,
                            final Provider<RequestManager> requestManagerProvider) {
        this.getrestServiceProvider = getrestServiceProvider;
        this.requestManagerProvider = requestManagerProvider;
    }

    public void execute(final RequestTuple requestTuple) {

        Exception exception = null;
        Object response = null;

        final String requestId = requestTuple.getRequestId();
        final RequestManager requestManager = requestManagerProvider.get();

        try {
            requestManager.updateRequestStatus(requestId, RequestStatus.EXECUTING);

            Loggers.getServiceLogger().trace("executing request: {0}", requestTuple.getRequest());

            getrestServiceProvider.get().publishEvent(new RequestExecutingEvent(requestTuple));

            response = requestTuple.getRequest().execute();
        } catch (final Exception ex) {
            exception = ex;
        }

        if (exception != null) {

            final RequestErrorEvent requestErrorEvent = new RequestErrorEvent(requestTuple);
            requestErrorEvent.setException(exception);

            getrestServiceProvider.get().publishEvent(requestErrorEvent);
        } else {

            final RequestCompletedEvent requestCompletedEvent = new RequestCompletedEvent(requestTuple);
            requestCompletedEvent.setResponse(response);

            getrestServiceProvider.get().publishEvent(requestCompletedEvent);
        }
    }
}
