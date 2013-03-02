/*
 * Copyright 2013 Alexey Hanin
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

import android.net.Uri;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import getrest.android.core.impl.RequestManagerImpl;
import getrest.android.persistence.Storage;
import getrest.android.persistence.util.RequestStorageSupport;

import getrest.android.util.Lists;
import getrest.android.util.Provider;
import static junit.framework.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.invocation.InvocationOnMock;

import org.mockito.stubbing.Answer;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class RequestManagerImplTest {

    private List<Storage> storageList = Lists.newArrayList();
    private Storage storage = mock(Storage.class);
    private Request request = mock(Request.class);
    private RequestManagerImpl requestManager;

    @Before
    public void setUp() throws Exception {

        final Provider<List<Storage>> storageListProvider = mock(Provider.class);
        when(storageListProvider.get()).thenAnswer(new Answer<List<Storage>>() {
                public List<Storage> answer(final InvocationOnMock invocation) throws Throwable {
                    return storageList;
                }
            });

        requestManager = new RequestManagerImpl(storageListProvider);
    }

    @Test
    public void testShouldPersistSupportedRequest() throws Exception {
        storageList = Lists.newArrayList(storage);
        when(storage.supports(any(Uri.class), any(Class.class))).thenReturn(true);

        requestManager.persistRequest("1234567890", request);

        verify(storage).persist(RequestStorageSupport.getRequestUri("1234567890"), request);
    }

    @Test
    public void testShouldNotPersistUnsupportedRequest() throws Exception {
        storageList = Lists.newArrayList(storage);
        when(storage.supports(any(Uri.class), any(Class.class))).thenReturn(false);

        try {
            requestManager.persistRequest("1234567890", request);
            fail("Must raise " + IllegalStateException.class.getName());
        } catch (final IllegalStateException ex) {

            // expected behaviour
        }
    }
}
