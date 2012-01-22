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

import android.content.Intent;

public class RequestEventBus {

    public static final class Intents {

        private Intents() {
        }

        public static final String REQUEST_EVENT_ACTION = "getrest.android.intent.action.REQUEST_EVENT";

    }

    private Broadcaster broadcaster;

    public void firePending(String requestId) {
        final RequestEventWrapper eventWrapper = new RequestEventWrapper(new Intent(Intents.REQUEST_EVENT_ACTION));
        eventWrapper.setRequestId(requestId);
        eventWrapper.setPending();
        broadcaster.sendBroadcast(eventWrapper.asIntent());
    }

    public void fireExecuting(String requestId) {
        final RequestEventWrapper eventWrapper = new RequestEventWrapper(new Intent(Intents.REQUEST_EVENT_ACTION));
        eventWrapper.setRequestId(requestId);
        eventWrapper.setExecuting();
        broadcaster.sendBroadcast(eventWrapper.asIntent());
    }

    public void fireFinished(String requestId) {
        final RequestEventWrapper eventWrapper = new RequestEventWrapper(new Intent(Intents.REQUEST_EVENT_ACTION));
        eventWrapper.setRequestId(requestId);
        eventWrapper.setFinished();
        broadcaster.sendBroadcast(eventWrapper.asIntent());
    }

    public void setBroadcaster(final Broadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }
}
