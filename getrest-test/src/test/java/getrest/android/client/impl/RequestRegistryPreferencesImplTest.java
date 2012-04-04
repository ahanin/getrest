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
import com.xtremelabs.robolectric.RobolectricTestRunner;
import getrest.android.client.RequestRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class RequestRegistryPreferencesImplTest {

    private Activity activity;
    private SharedPreferences preferences;
    private RequestRegistryPreferencesImpl registryImpl;

    @Before
    public void setUp() throws Exception {
        activity = mock(Activity.class);
        preferences = mock(SharedPreferences.class);
        when(activity.getPreferences(Context.MODE_PRIVATE)).thenReturn(preferences);

        registryImpl = new RequestRegistryPreferencesImpl(activity);
    }

    @Test
    public void testShouldReturnEmptyEntryListWhenNoRequestsStored() throws Exception {
        when(preferences.contains(RequestRegistryPreferencesImpl.ENTRIES)).thenReturn(false);
        assertThat(registryImpl.getEntries(), equalTo(Collections.<RequestRegistry.Entry>emptySet()));
    }

    @Test
    public void testShouldReturnEmptyEntryListWhenEmptyRequestListStored() throws Exception {
        when(preferences.contains(RequestRegistryPreferencesImpl.ENTRIES)).thenReturn(true);
        when(preferences.getString(RequestRegistryPreferencesImpl.ENTRIES, "")).thenReturn(null);
        assertThat(registryImpl.getEntries(), equalTo(Collections.<RequestRegistry.Entry>emptySet()));
    }

    @Test
    public void testShouldReturnRegisteredEntries() throws Exception {
        when(preferences.getString(eq(RequestRegistryPreferencesImpl.ENTRIES), any(String.class)))
                .thenReturn(String.format("12345:%s,67890:%s",
                        Uri.encode("http://disney.com/party"),
                        Uri.encode("http://domain.tld/charity-ball")));

        final Set<RequestRegistry.Entry> entrySet = registryImpl.getEntries();

        final RequestRegistry.Entry[] entries = entrySet.toArray(new RequestRegistry.Entry[entrySet.size()]);
        final RequestRegistry.Entry a, b;
        if ("12345".equals(entries[0].getRequestId())) {
            a = entries[0];
            b = entries[1];
        } else {
            b = entries[0];
            a = entries[1];
        }

        assertThat(a.getRequestId(), equalTo("12345"));
        assertThat(a.getResourceUri(), equalTo(Uri.parse("http://disney.com/party")));

        assertThat(b.getRequestId(), equalTo("67890"));
        assertThat(b.getResourceUri(), equalTo(Uri.parse("http://domain.tld/charity-ball")));
    }

    @Test
    public void testShouldSaveEntries() throws Exception {
        final HashMap<String, RequestRegistry.Entry> newEntries = new HashMap<String, RequestRegistry.Entry>();
        final RequestRegistry.Entry entry = mock(RequestRegistry.Entry.class);
        when(entry.getRequestId()).thenReturn("12345");
        when(entry.getResourceUri()).thenReturn(Uri.parse("http://disney.com/party"));
        newEntries.put("12345", entry);

        final SharedPreferences preferences = mock(SharedPreferences.class);
        when(activity.getPreferences(Context.MODE_PRIVATE)).thenReturn(preferences);
        final SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(preferences.edit()).thenReturn(editor);

        final InOrder inOrder = inOrder(editor);

        registryImpl.commitChanges(Collections.<String>emptySet(), newEntries);

        inOrder.verify(editor).putString(RequestRegistryPreferencesImpl.ENTRIES,
                "12345:http%3A%2F%2Fdisney.com%2Fparty");
        inOrder.verify(editor).commit();
    }

}
