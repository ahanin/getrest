/*
 * Copyright 2013 Alexey Hanin
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

import getrest.android.core.Request;

public class RequestTuple<R extends Request> {

    private final String requestId;
    private final R request;
    private final CallerContext callerContext;

    public RequestTuple(final String requestId, final R request, final CallerContext callerContext) {
        this.requestId = requestId;
        this.request = request;
        this.callerContext = callerContext;
    }

    public String getRequestId() {

        return requestId;
    }

    public R getRequest() {

        return request;
    }

    public CallerContext getCallerContext() {

        return callerContext;
    }
}
