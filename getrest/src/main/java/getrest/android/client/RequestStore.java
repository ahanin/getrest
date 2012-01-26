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

import java.util.Collection;
import java.util.Set;

public interface RequestStore {

    /**
     * Saves request id to the store. Request id will retain in the store until it is removed by calling
     * {@link #remove(String)} or {@link #retainAll}
     *
     * @param requestId request id to store
     */
    void put(String requestId);

    /**
     * List previously stored request ids
     *
     * @return set of previously stored request ids
     */
    Set<String> getRequests();

    /**
     * Removes request id.
     *
     * @param requestId request id to remove
     */
    void remove(String requestId);

    /**
     * Purges request store.
     *
     * @param requestIds ids of the requests to left retain
     */
    void retainAll(Collection<String> requestIds);

}
