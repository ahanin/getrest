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

import java.util.Set;

public interface RequestRegistry {

    interface Entry {

        String getRequestId();

    }

    interface Editor {

        /**
         * Stores information about pending request.
         *
         * @param requestId pending request
         */
        void put(String requestId);

        /**
         * Removes request from registry.
         *
         * @param requestId id of the request to be removed
         */
        void remove(String requestId);

        /**
         * Commits pending changes to registry.
         */
        void commit();

    }

    /**
     * Returns registry editor.
     *
     * @return instance of {@link getrest.android.client.RequestRegistry.Editor}
     */
    Editor edit();

    /**
     * Retrieves currently stored entries.
     *
     * @return set of entries, which correspond to previously stored requests
     */
    Set<Entry> getEntries();

    /**
     * Retrieves request entry.
     *
     * @param requestId request id
     * @return request {@link Entry}, or {@code null}, if there are none
     */
    Entry getEntry(String requestId);

}
