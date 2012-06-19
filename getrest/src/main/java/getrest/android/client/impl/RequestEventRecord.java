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

package getrest.android.client.impl;

import getrest.android.request.RequestStatus;

class RequestEventRecord {

    private String requestId;

    private RequestStatus requestStatus;

    private Object data;

    @Override
    public String toString() {
        return "RequestEventRecord{" +
                "requestId='" + requestId + '\'' +
                ", requestStatus=" + requestStatus +
                '}';
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(final RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public <T> void setData(final T data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T) data;
    }

}
