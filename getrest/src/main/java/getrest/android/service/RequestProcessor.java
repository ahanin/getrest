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

import getrest.android.util.Provider;
import getrest.android.util.WorkerQueue;

class RequestProcessor implements WorkerQueue.Worker<RequestTuple> {

    private final Provider<RequestManager> requestManagerProvider;

    public RequestProcessor(final Provider<RequestManager> requestManagerProvider) {
        this.requestManagerProvider = requestManagerProvider;
    }

    public void execute(final RequestTuple requestTuple) {

        Exception error = null;
        Object result = null;

        final String requestId = requestTuple.getRequestId();
        final RequestManager requestManager = requestManagerProvider.get();

        final RequestFutureSupport requestFutureSupport = requestManager.getRequestFutureSupport(
            requestTuple);

        try {
            requestManager.updateRequestStatus(requestId, RequestStatus.EXECUTING);

            Loggers.getServiceLogger().trace("executing request: {0}", requestTuple.getRequest());

            requestFutureSupport.fireOnExecuting();

            result = requestTuple.getRequest().execute();
        } catch (final Exception ex) {
            error = ex;
            requestTuple.getCallerContext().getHandler().post(new ExceptionLogger(ex));
        }

        if (error != null) {
            requestFutureSupport.fireOnError(error);
        } else {
            requestFutureSupport.fireOnCompleted(result);
        }
    }

    private static class ExceptionLogger implements Runnable {

        private final Exception ex;

        public ExceptionLogger(final Exception ex) {
            this.ex = ex;
        }

        public void run() {
            Loggers.getServiceLogger().error("Exception during execution of a request", ex);
        }
    }
}
