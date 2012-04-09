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
package getrest.android.client;

import getrest.android.request.Request;
import getrest.android.request.RequestManager;
import getrest.android.request.RequestState;
import getrest.android.request.Response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRequestManager implements RequestManager {

    private final Map<String, RequestManagerEntry> backedMap = new ConcurrentHashMap<String, RequestManagerEntry>();

    public void saveRequest(final Request request) {
        final String requestId = request.getRequestId();
        synchronized (backedMap) {
            if (backedMap.containsKey(requestId)) {
                throw new IllegalStateException("Request with id '" + requestId + "' is already registered");
            }
            backedMap.put(requestId, new RequestManagerEntry(request));
        }
    }

    public Request getRequest(final String requestId) {
        final RequestManagerEntry entry = backedMap.get(requestId);
        return entry != null ? entry.getRequest() : null;
    }

    public void saveResponse(final String requestId, final Response response) {
        final RequestManagerEntry entry = backedMap.get(requestId);
        if (entry == null) {
            throw new IllegalStateException("Request must be acknowledged prior to response");
        }
        entry.setResponse(response);
    }

    public Response getResponse(final String requestId) {
        final RequestManagerEntry entry = backedMap.get(requestId);
        return entry == null ? null : entry.getResponse();
    }

    public void setRequestState(final String requestId, final RequestState state) {
        final RequestManagerEntry entry = backedMap.get(requestId);
        if (entry == null) {
            throw new IllegalStateException("Request must be acknowledged prior to response");
        }
        entry.setRequestState(state);
    }

    public RequestState getRequestState(final String requestId) {
        final RequestManagerEntry entry = backedMap.get(requestId);
        return entry == null ? null : entry.getRequestState();
    }

    private static class RequestManagerEntry {

        private Request request;
        private Response response;
        private RequestState requestState;

        public RequestManagerEntry(final Request request) {
            this.request = request;
        }

        public Request getRequest() {
            return request;
        }

        public Response getResponse() {
            return response;
        }

        public void setResponse(final Response response) {
            this.response = response;
        }

        public void setRequestState(final RequestState requestState) {
            this.requestState = requestState;
        }

        public RequestState getRequestState() {
            return requestState;
        }
    }
}
