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

import getrest.android.executor.RequestExecutor;
import getrest.android.request.Request;
import getrest.android.request.Response;

/**
 * @author aha
 * @since 2012-01-13
 */
public class RequestJob implements Runnable {

    private Request request;

    private RequestCallback callback;

    public RequestJob(final Request request) {
        this.request = request;
    }

    public void setCallback(final RequestCallback callback) {
        this.callback = callback;
    }

    public void run() {
        final ServiceContext serviceContext = ServiceContext.forRequest(request);
        final RequestExecutor requestProcessor = serviceContext.getRequestExecutor(request);
        final Response response = requestProcessor.execute();
        if (callback != null) {
            callback.onResponse(response);
        }
    }

}
