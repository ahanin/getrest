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

public interface RequestManager {

    void persistRequest(Request request);

    Request getRequest(String requestId);

    void persistResponse(final String requestId, ResponseParcel responseParcel);

    ResponseParcel getResponse(String requestId);

    void updateRequestStatus(String requestId, RequestStatus status);

    void updateRequestStatus(String requestId, ErrorState errorState, String message);

    RequestStatus getRequestStatus(String requestId);
}
