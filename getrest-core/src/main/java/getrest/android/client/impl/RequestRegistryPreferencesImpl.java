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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import getrest.android.client.RequestRegistry;
import getrest.android.core.Loggers;
import getrest.android.util.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RequestRegistryPreferencesImpl implements RequestRegistry, TransactionalRequestEntryStorage {

    private static final Logger LOGGER = Loggers.getClientLogger();

    static final String ENTRIES = "getrest.android.client.impl.RequestRegistryPreferencesImpl.ENTRIES";

    private final Activity activity;

    private volatile Map<String, Entry> backedMap; // requestId to entry map

    private final Lock writeLock = new ReentrantLock();

    public RequestRegistryPreferencesImpl(final Activity activity) {
        this.activity = activity;
    }

    public Editor edit() {
        return new RequestRegistryEditorImpl(this);
    }

    public Set<Entry> getEntries() {
        return new HashSet<Entry>(getBackedMap().values());
    }

    public Entry getEntry(final String requestId) {
        return getBackedMap().get(requestId);
    }

    private Map<String, Entry> getBackedMap() {
        if (backedMap == null) {
            synchronized (this) {
                if (backedMap == null) {

                    backedMap = new HashMap<String, Entry>();

                    try {
                        writeLock.lock();

                        final SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
                        final String preferencesString = preferences.getString(ENTRIES, null);
                        if (preferencesString != null) {
                            unmarshallEntriesToMap(preferencesString, backedMap);
                        }
                    } finally {
                        writeLock.unlock();
                    }
                }
            }
        }
        return backedMap;
    }

    private Set<Entry> unmarshallEntriesToMap(final String preferencesString, final Map<String, Entry> backedMap) {
        final Set<Entry> entrySet;

        final String[] entryStrings = preferencesString.split(",");

        if (entryStrings.length < 1) {
            return Collections.emptySet();
        } else {
            entrySet = new HashSet<Entry>();
        }

        for (String entryString : entryStrings) {
            final String[] parts = entryString.split(":");
            if (parts.length == 2) {
                final String requestId = parts[0];
                final Uri uri = Uri.parse(Uri.decode(parts[1]));
                backedMap.put(requestId, new RequestRegistryEntryFactory.EntryImpl(requestId));
            } else {
                LOGGER.warn("Invalid request registry entry: {}", entryString);
            }
        }

        return entrySet;
    }

    private String marshall(final Collection<Entry> requestIds) {
        final StringBuilder sb = new StringBuilder();
        for (Iterator<Entry> i = requestIds.iterator(); i.hasNext(); ) {
            final Entry entry = i.next();

            sb.append(entry.getRequestId());

            if (i.hasNext()) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    public void commitChanges(final Set<String> removedIds, final Map<String, Entry> newEntries) {
        try {
            writeLock.lock();

            final Map<String, Entry> backedMap = getBackedMap();
            backedMap.keySet().removeAll(removedIds);
            backedMap.putAll(newEntries);

            final SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putString(ENTRIES, marshall(backedMap.values()));
            editor.commit();
        } finally {
            writeLock.unlock();
        }
    }

}
