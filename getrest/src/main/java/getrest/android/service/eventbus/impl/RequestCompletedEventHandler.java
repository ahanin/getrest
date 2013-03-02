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
package getrest.android.service.eventbus.impl;

import getrest.android.core.Request;

import getrest.android.event.RequestCompletedEvent;
import getrest.android.event.RequestExecutingEvent;

import getrest.android.service.RequestFutureSupport;

import getrest.android.util.Provider;

import java.util.Map;
import java.util.Set;

public class RequestCompletedEventHandler extends AbstractRequestEventHandler<RequestCompletedEvent> {
    public RequestCompletedEventHandler(final Provider<Map<Request, Set<RequestFutureSupport>>> requestFutureSupportMapProvider) {
        super(requestFutureSupportMapProvider);
    }

    @Override
    protected void handleEvent(final RequestCompletedEvent event,
                               final RequestFutureSupport requestFutureSupport) {
        requestFutureSupport.fireOnCompleted(event.getResponse());
    }
}
