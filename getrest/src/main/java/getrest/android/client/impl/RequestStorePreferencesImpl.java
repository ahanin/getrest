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
import getrest.android.client.RequestStore;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RequestStorePreferencesImpl implements RequestStore {

    static final String REQUEST_IDS = "getrest.android.client.impl.RequestStorePreferencesImpl.REQUEST_IDS";

    private Activity activity;

    public RequestStorePreferencesImpl(final Activity activity) {
        this.activity = activity;
    }

    public void put(final String requestId) {
        final SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        synchronized (preferences) {
            final Set<String> requestIds;
            if (preferences.contains(REQUEST_IDS)) {
                requestIds = unmarshall(preferences.getString(REQUEST_IDS, null));
            } else {
                requestIds = new HashSet<String>();
            }

            requestIds.add(requestId);

            final SharedPreferences.Editor edit = preferences.edit();
            edit.putString(REQUEST_IDS, marshall(requestIds));

            edit.commit();
        }
    }

    private String marshall(final Set<String> requestIds) {
        final StringBuilder sb = new StringBuilder();
        for (Iterator<String> i = requestIds.iterator(); i.hasNext(); ) {
            sb.append(i.next());
            if (i.hasNext()) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    private Set<String> unmarshall(final String string) {
        return new HashSet<String>(Arrays.asList(string.split("\\s*\\,\\s*")));
    }

    public Set<String> getRequests() {
        final SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        synchronized (preferences) {
            if (preferences.contains(REQUEST_IDS)) {
                return unmarshall(preferences.getString(REQUEST_IDS, null));
            } else {
                return Collections.emptySet();
            }
        }
    }

    public void remove(final String requestId) {
        final SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        synchronized (preferences) {
            if (!preferences.contains(REQUEST_IDS)) {
                return;
            }

            final Set<String> ids = unmarshall(preferences.getString(REQUEST_IDS, null));
            ids.remove(requestId);

            final SharedPreferences.Editor editor = preferences.edit();

            if (!ids.isEmpty()) {
                editor.putString(REQUEST_IDS, marshall(ids));
            } else {
                editor.remove(REQUEST_IDS);
            }

            editor.commit();
        }
    }

    public void retainAll(final Collection<String> requestIds) {
        final SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
        synchronized (preferences) {
            if (!preferences.contains(REQUEST_IDS)) {
                return;
            }

            final Set<String> ids = unmarshall(preferences.getString(REQUEST_IDS, null));
            ids.retainAll(requestIds);

            final SharedPreferences.Editor editor = preferences.edit();

            if (!ids.isEmpty()) {
                editor.putString(REQUEST_IDS, marshall(ids));
            } else {
                editor.remove(REQUEST_IDS);
            }

            editor.commit();
        }
    }
}
