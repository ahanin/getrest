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

import getrest.android.client.RequestRegistry;
import getrest.android.core.Request;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RequestRegistryEditorImpl implements RequestRegistry.Editor {

    private final Map<String, RequestRegistry.Entry> newEntries = new HashMap<String, RequestRegistry.Entry>();
    private final Set<String> removedIds = new HashSet<String>();

    private RequestRegistryEntryFactory entryFactory = new RequestRegistryEntryFactory();

    private final TransactionalRequestEntryStorage entryStorage;

    public RequestRegistryEditorImpl(final TransactionalRequestEntryStorage entryStorage) {
        this.entryStorage = entryStorage;
    }

    public void setEntryFactory(final RequestRegistryEntryFactory entryFactory) {
        this.entryFactory = entryFactory;
    }

    public void put(final Request request) {
        newEntries.put(request.getRequestId(), entryFactory.create(request));
    }

    public void remove(final String requestId) {
        removedIds.add(requestId);
    }

    public void commit() {
        entryStorage.commitChanges(removedIds, newEntries);
    }

}
