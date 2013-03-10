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
package getrest.android.core;

import getrest.android.service.RequestFutureSupport;
import getrest.android.service.RequestTuple;

public interface RequestManager {
    void persistRequest(final String requestId, final Request request);

    Request loadRequest(final String requestId);

    void persistResponse(final String requestId, final Object response);

    Object loadResponse(final String requestId);

    void updateRequestStatus(final String requestId, final RequestStatus status);

    RequestStatus getRequestStatus(final String requestId);

    <R extends Request<V>, V> RequestFutureSupport<R, V> getRequestFutureSupport(final RequestTuple<R> requestTuple);
}
