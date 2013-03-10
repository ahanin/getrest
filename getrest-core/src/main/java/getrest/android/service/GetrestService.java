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

import getrest.android.client.impl.GetrestClientImpl;

import getrest.android.core.Request;
import getrest.android.core.RequestFuture;
import getrest.android.event.Event;
import getrest.android.event.RequestPendingEvent;

public interface GetrestService {
    <R extends Request<V>, V> RequestFuture<V> execute(final R request,
                                                       final CallerContext callerContext);

    <R extends Request<V>, V> RequestFuture<V> obtainRequestFuture(final R request,
                                                                   final CallerContext callerContextAdapter);

    void publishEvent(Event event);
}
