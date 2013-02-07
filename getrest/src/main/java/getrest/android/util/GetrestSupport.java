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
package getrest.android.util;

import getrest.android.core.Request;

import getrest.android.service.CallerContext;
import getrest.android.service.RequestFutureSupport;
import getrest.android.service.RequestTuple;

public class GetrestSupport {
    public static <R extends Request<V>, V> RequestFutureSupport<R, V> createRequestFutureSupport(final R request,
                                                                                                  final CallerContext callerContext) {

        final RequestFutureSupport<R, V> requestFutureSupport = new RequestFutureSupport<R, V>();
        final RequestTuple<R> requestTuple = new RequestTuple<R>(request,
                                                                 requestFutureSupport,
                                                                 callerContext);
        requestFutureSupport.setRequestTuple(requestTuple);

        return requestFutureSupport;
    }
}
