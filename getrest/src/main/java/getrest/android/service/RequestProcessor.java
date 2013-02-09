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
import getrest.android.core.RequestExecutable;

import getrest.android.util.WorkerQueue;

class RequestProcessor implements WorkerQueue.Worker<RequestTuple> {

    private static RequestProcessor instance = new RequestProcessor();

    private RequestProcessor() {}

    public static RequestProcessor getInstance() {

        return instance;
    }

    public void execute(final RequestTuple requestTuple) {

        Exception error = null;
        Object result = null;

        try {
            Loggers.getServiceLogger().trace("executing request: {0}", requestTuple.getRequest());

            requestTuple.getRequestFutureSupport().fireOnExecuting();

            if (requestTuple instanceof RequestExecutable) {

                final RequestExecutable executable = (RequestExecutable) requestTuple;
                result = executable.execute();

            } else {
                throw new UnsupportedOperationException("non-executable request are not supported");
            }
        } catch (final Exception ex) {
            error = ex;
            Loggers.getServiceLogger().error("Exception during execution of a request", ex);
        }

        if (error != null) {
            requestTuple.getRequestFutureSupport().fireOnError(error);
        } else {
            requestTuple.getRequestFutureSupport().fireOnCompleted(result);
        }
    }
}
