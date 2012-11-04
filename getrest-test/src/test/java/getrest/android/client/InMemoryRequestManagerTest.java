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

import getrest.android.core.Request;
import getrest.android.request.RequestStatus;
import getrest.android.core.ResponseParcelable;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InMemoryRequestManagerTest {

    private InMemoryRequestManager requestManager;

    @Before
    public void setUp() throws Exception {
        requestManager = new InMemoryRequestManager();
    }

    @Test
    public void testGetRequestShouldReturnNull() throws Exception {
        assertThat(requestManager.getRequest("12345"), nullValue());
    }

    @Test
    public void testShouldSaveRequest() throws Exception {
        final Request request = mock(Request.class);
        when(request.getRequestId()).thenReturn("12345");

        requestManager.saveRequest(request);

        assertThat(requestManager.getRequest("12345"), sameInstance(request));
    }

    @Test
    public void testGetResponseShouldReturnNull() throws Exception {
        assertThat(requestManager.getResponse("12345"), nullValue());
    }

    @Test
    public void testGetResponseShouldReturnNullBeforeSaved() throws Exception {
        final Request request = mock(Request.class);
        when(request.getRequestId()).thenReturn("12345");

        requestManager.saveRequest(request);

        assertThat(requestManager.getResponse("12345"), nullValue());
    }

    @Test
    public void testShouldNotSaveResponseBeforeRequestAcknowledged() throws Exception {
        final ResponseParcelable responseParcelable = mock(ResponseParcelable.class);
        try {
            requestManager.saveResponse("12345", responseParcelable);
            fail("Must raise " + IllegalStateException.class.getName());
        } catch (IllegalStateException ex) {
            // expected behaviour
        }
    }

    @Test
    public void testShouldSaveResponse() throws Exception {
        final Request request = mock(Request.class);
        when(request.getRequestId()).thenReturn("12345");
        requestManager.saveRequest(request);

        final ResponseParcelable responseParcelable = mock(ResponseParcelable.class);
        requestManager.saveResponse("12345", responseParcelable);

        assertThat(requestManager.getResponse("12345"), sameInstance(responseParcelable));
    }

    @Test
    public void testRequestStateShouldBeNullUnlessAcknowledged() throws Exception {
        assertThat(requestManager.getRequestState("12345"), nullValue());
    }

    @Test
    public void testShouldNotSaveStateForUnacknowledgedRequest() throws Exception {
        try {
            requestManager.setRequestState("12345", RequestStatus.EXECUTING);
            fail("Must raise " + IllegalStateException.class.getName());
        } catch (IllegalStateException ex) {
            // expected behaviour
        }
    }

    @Test
    public void testShouldSaveRequestState() throws Exception {
        final Request request = mock(Request.class);
        when(request.getRequestId()).thenReturn("12345");
        requestManager.saveRequest(request);

        requestManager.setRequestState("12345", RequestStatus.FINISHED);

        assertThat(requestManager.getRequestState("12345"), equalTo(RequestStatus.FINISHED));
    }

}
