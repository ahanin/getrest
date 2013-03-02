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
package getrest.android.persistence;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import getrest.android.core.Request;
import getrest.android.core.RequestStatus;

import getrest.android.persistence.util.RequestStorageSupport;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.invocation.InvocationOnMock;

import org.mockito.stubbing.Answer;

import java.lang.reflect.Modifier;

@RunWith(RobolectricTestRunner.class)
public class AbstractRequestStorageTest {

    private AbstractRequestStorage requestStorage;

    @Before
    public void setUp() throws Exception {
        requestStorage = mock(AbstractRequestStorage.class,
                              new Answer() {
                public Object answer(final InvocationOnMock invocation) throws Throwable {
                    if (Modifier.isAbstract(invocation.getMethod().getModifiers())) {
                        return Mockito.RETURNS_DEFAULTS.answer(invocation);
                    } else {
                        return Mockito.CALLS_REAL_METHODS.answer(invocation);
                    }
                }
            });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShouldPersistRequest() {

        final Request request = mock(Request.class);
        requestStorage.persist(RequestStorageSupport.getRequestUri("123456"), request);

        verify(requestStorage).persistRequest("123456", request);
    }

    @Test
    public void testShouldPersistResponse() {

        final Object response = new Object();
        requestStorage.persist(RequestStorageSupport.getResponseUri("123456"), response);

        verify(requestStorage).persistResponse("123456", response);
    }

    @Test
    public void testShouldPersistRequestStatus() {
        requestStorage.persist(RequestStorageSupport.getRequestStatusUri("123456"),
                               RequestStatus.PENDING);

        verify(requestStorage).persistRequestStatus("123456", RequestStatus.PENDING);
    }

    @Test
    public void testShouldDelegateContainsMethodForRequest() {
        when(requestStorage.containsRequest("123456")).thenReturn(true);
        assertTrue("Must return result of delegated method call",
                   requestStorage.contains(RequestStorageSupport.getRequestUri("123456")));

        verify(requestStorage).containsRequest("123456");
    }

    @Test
    public void testShouldDelegateContainsMethodForResponse() {
        when(requestStorage.containsResponse("123456")).thenReturn(true);
        assertTrue("Must return result of delegated method call",
                   requestStorage.contains(RequestStorageSupport.getResponseUri("123456")));

        verify(requestStorage).containsResponse("123456");
    }

    @Test
    public void testShouldDelegateContainsMethodForRequestStatus() {
        when(requestStorage.containsRequestStatus("123456")).thenReturn(true);
        assertTrue("Must return result of delegated method call",
                   requestStorage.contains(RequestStorageSupport.getRequestStatusUri("123456")));

        verify(requestStorage).containsRequestStatus("123456");
    }

    @Test
    public void testShouldLoadRequest() {

        final Request request = mock(Request.class);
        when(requestStorage.loadRequest("123456")).thenReturn(request);
        assertSame("Must return result of delegated method call",
                   request,
                   requestStorage.load(RequestStorageSupport.getRequestUri("123456")));
    }

    @Test
    public void testShouldLoadResponse() {

        final Object response = new Object();
        when(requestStorage.loadResponse("123456")).thenReturn(response);
        assertSame("Must return result of delegated method call",
                   response,
                   requestStorage.load(RequestStorageSupport.getResponseUri("123456")));
    }

    @Test
    public void testShouldLoadRequestStatus() {
        when(requestStorage.loadRequestStatus("123456")).thenReturn(RequestStatus.PENDING);
        assertSame("Must return result of delegated method call",
                   RequestStatus.PENDING,
                   requestStorage.load(RequestStorageSupport.getRequestStatusUri("123456")));
    }
}
