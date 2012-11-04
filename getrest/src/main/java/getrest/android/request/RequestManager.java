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
package getrest.android.request;

import getrest.android.core.ErrorState;
import getrest.android.core.Request;
import getrest.android.core.ResponseParcelable;

public interface RequestManager {

    void saveRequest(Request request);

    Request getRequest(String requestId);

    void saveResponse(final String requestId, ResponseParcelable responseParcelable);

    ResponseParcelable getResponse(String requestId);

    void setRequestState(String requestId, RequestStatus status);

    RequestStatus getRequestState(String requestId);

    void setRequestState(String requestId, ErrorState errorState, String message);
}
