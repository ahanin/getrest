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

import android.net.Uri;
import getrest.android.client.RequestRegistry;
import getrest.android.core.Request;

public class RequestRegistryEntryFactory {

    public RequestRegistry.Entry create(final String requestId) {
        return new EntryImpl(requestId);
    }

    static class EntryImpl implements RequestRegistry.Entry {
        private final String requestId;

        public EntryImpl(final String requestId) {
            this.requestId = requestId;
        }

        public String getRequestId() {
            return requestId;
        }

        @Override
        public String toString() {
            return "EntryImpl{" +
                    "requestId='" + requestId + '\'' +
                    '}';
        }
    }
}
