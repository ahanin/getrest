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
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static getrest.android.client.impl.RequestStorePreferencesImpl.REQUEST_IDS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestStorePreferencesImplTest {

    private Activity activity;
    private SharedPreferences preferences;
    private RequestStorePreferencesImpl store;

    @Before
    public void setUp() throws Exception {
        activity = mock(Activity.class);
        preferences = mock(SharedPreferences.class);
        when(activity.getPreferences(Context.MODE_PRIVATE)).thenReturn(preferences);

        store = new RequestStorePreferencesImpl(activity);
    }

    @Test
    public void testShouldPutRequestIdWhenEmpty() throws Exception {
        when(preferences.contains(REQUEST_IDS)).thenReturn(false);
        final SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(preferences.edit()).thenReturn(editor);

        final InOrder inOrder = inOrder(editor);

        store.put("abc");

        inOrder.verify(editor).putString(REQUEST_IDS, "abc");
        inOrder.verify(editor).commit();
    }

    @Test
    public void testShouldPutRequestIdWhenNonEmpty() throws Exception {
        when(preferences.contains(REQUEST_IDS)).thenReturn(true);
        when(preferences.getString(eq(REQUEST_IDS), any(String.class))).thenReturn("abc");

        final SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(preferences.edit()).thenReturn(editor);

        final InOrder inOrder = inOrder(editor);

        store.put("xyz");

        inOrder.verify(editor).putString(eq(REQUEST_IDS), argThat(new CommaSeparatedValuesMatcher("abc", "xyz")));
        inOrder.verify(editor).commit();
    }

    @Test
    public void testShouldReturnRequestIds() throws Exception {
        when(preferences.contains(REQUEST_IDS)).thenReturn(true);
        when(preferences.getString(eq(REQUEST_IDS), any(String.class))).thenReturn("abc,xyz");

        assertThat(store.getRequests(), equalTo((Set<String>) new HashSet<String>(Arrays.asList("abc", "xyz"))));
    }

    @Test
    public void testShouldReturnEmptySetWhenDoesNotExist() throws Exception {
        when(preferences.contains(REQUEST_IDS)).thenReturn(false);

        assertThat(store.getRequests(), equalTo(Collections.<String>emptySet()));
    }

    @Test
    public void testShouldRemoveRequestId() throws Exception {
        when(preferences.contains(REQUEST_IDS)).thenReturn(true);
        when(preferences.getString(eq(REQUEST_IDS), any(String.class))).thenReturn("abc,xyz");

        final SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(preferences.edit()).thenReturn(editor);

        final InOrder inOrder = inOrder(editor);

        store.remove("xyz");

        inOrder.verify(editor).putString(eq(REQUEST_IDS), argThat(new CommaSeparatedValuesMatcher("abc")));
        inOrder.verify(editor).commit();
    }

    private static class CommaSeparatedValuesMatcher extends ArgumentMatcher<String> {

        private String[] values;

        public CommaSeparatedValuesMatcher(final String... values) {
            this.values = values;
        }

        @Override
        public boolean matches(final Object argument) {
            final String commaSeparatedValues = (String) argument;

            final String[] chunks = commaSeparatedValues.split("\\s*\\,\\s*");
            return new HashSet<String>(Arrays.asList(values)).equals(new HashSet<String>(Arrays.asList(chunks)));
        }

        @Override
        public void describeTo(final Description description) {
            description.appendValueList("[", ",", "]", values);
        }
    }

}
