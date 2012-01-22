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
import getrest.android.request.Response;

public class RequestEventWrapper {

    private Intent intent;

    private static final String EXTRA_REQUEST_ID = "getrest.android.service.RequestEventWrapper.REQUEST_ID";
    private static final String EXTRA_EVENT_TYPE = "getrest.android.service.RequestEventWrapper.EVENT_TYPE";
    private static final String EXTRA_RESPONSE = "getrest.android.service.RequestEventWrapper.RESPONSE";

    public RequestEventWrapper(final Intent intent) {
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

    public void setEventType(final int eventType) {
        intent.putExtra(EXTRA_EVENT_TYPE, eventType);
    }

    public int getEventType() {
        return intent.getIntExtra(EXTRA_EVENT_TYPE, -1);
    }

    public void setPending() {
        setEventType(RequestEvents.PENDING);
    }

    public void setExecuting() {
        setEventType(RequestEvents.EXECUTING);
    }

    public void setFinished() {
        setEventType(RequestEvents.FINISHED);
    }

    public void setResponse(Response response) {
        intent.putExtra(EXTRA_RESPONSE, response);
    }

    public Response getResponse() {
        return intent.getParcelableExtra(EXTRA_RESPONSE);
    }

}
