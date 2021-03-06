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
import getrest.android.core.Response;

public class RequestStateChangeEventWrapper {

    private Intent intent;

    private static final String EXTRA_REQUEST_ID = "getrest.android.service.RequestStateChangeEventWrapper.REQUEST_ID";
    private static final String REQUEST_STATE = "getrest.android.service.RequestStateChangeEventWrapper.REQUEST_STATE";

    public RequestStateChangeEventWrapper(final Intent intent) {
        this.intent = intent;
    }

    public void setRequestId(final String requestId) {
        intent.putExtra(EXTRA_REQUEST_ID, requestId);
    }

    public String getRequestId() {
        return intent.getStringExtra(EXTRA_REQUEST_ID);
    }

    public Intent asIntent() {
        return intent;
    }

    public void setRequestState(final RequestStatus requestStatus) {
        intent.putExtra(REQUEST_STATE, requestStatus.getId());
    }

    public RequestStatus getRequestState() {
        return RequestStatus.byId(intent.getByteExtra(REQUEST_STATE, (byte) -1));
    }

}
