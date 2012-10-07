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
import getrest.android.request.RequestStatus;

public class RequestEventBus {

    public static final class Intents {

        private Intents() {
        }

        public static final String REQUEST_STATE_CHANGE_EVENT_ACTION = "getrest.android.intent.action." +
                "REQUEST_STATE_CHANGE_EVENT";

    }

    private Broadcaster broadcaster;

    public void firePending(String requestId) {
        final RequestStatus status = RequestStatus.PENDING;
        broadcastRequestStatusUpdate(requestId, status);
    }

    private void broadcastRequestStatusUpdate(final String requestId, final RequestStatus status) {
        final RequestStateChangeEventWrapper stateChangeEventWrapper = new RequestStateChangeEventWrapper(
                new Intent(Intents.REQUEST_STATE_CHANGE_EVENT_ACTION));
        stateChangeEventWrapper.setRequestId(requestId);
        stateChangeEventWrapper.setRequestState(status);
        broadcaster.sendBroadcast(stateChangeEventWrapper.asIntent());
    }

    public void fireExecuting(String requestId) {
        broadcastRequestStatusUpdate(requestId, RequestStatus.EXECUTING);
    }

    public void fireFinished(String requestId) {
        broadcastRequestStatusUpdate(requestId, RequestStatus.FINISHED);
    }

    public void fireError(final String requestId) {
        broadcastRequestStatusUpdate(requestId, RequestStatus.ERROR);
    }

    public void setBroadcaster(final Broadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }
}
