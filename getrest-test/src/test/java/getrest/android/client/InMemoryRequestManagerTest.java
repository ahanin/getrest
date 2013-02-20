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
import getrest.android.core.RequestStatus;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
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
        assertThat(requestManager.loadRequest("12345"), nullValue());
    }

    @Test
    public void testShouldSaveRequest() throws Exception {

        final Request request = mock(Request.class);

        requestManager.persistRequest("12345", request);

        assertThat(requestManager.loadRequest("12345"), sameInstance(request));
    }

    @Test
    public void testGetResponseShouldReturnNull() throws Exception {
        assertThat(requestManager.loadResponse("12345"), nullValue());
    }

    @Test
    public void testGetResponseShouldReturnNullBeforeSaved() throws Exception {

        final Request request = mock(Request.class);

        requestManager.persistRequest("12345", request);

        assertThat(requestManager.loadResponse("12345"), nullValue());
    }

    @Test
    public void testShouldNotSaveResponseBeforeRequestAcknowledged() throws Exception {

        final Object response = new Object();

        try {
            requestManager.persistResponse("12345", response);
            fail("Must raise " + IllegalStateException.class.getName());
        } catch (final IllegalStateException ex) {

            // expected behaviour
        }
    }

    @Test
    public void testShouldSaveResponse() throws Exception {

        final Request request = mock(Request.class);
        requestManager.persistRequest("12345", request);

        final Object response = new Object();
        requestManager.persistResponse("12345", response);

        assertThat(requestManager.loadResponse("12345"), sameInstance(response));
    }

    @Test
    public void testRequestStateShouldBeNullUnlessAcknowledged() throws Exception {
        assertThat(requestManager.getRequestStatus("12345"), nullValue());
    }

    @Test
    public void testShouldNotSaveStateForUnacknowledgedRequest() throws Exception {

        try {
            requestManager.updateRequestStatus("12345", RequestStatus.EXECUTING);
            fail("Must raise " + IllegalStateException.class.getName());
        } catch (final IllegalStateException ex) {

            // expected behaviour
        }
    }

    @Test
    public void testShouldSaveRequestState() throws Exception {

        final Request request = mock(Request.class);
        requestManager.persistRequest("12345", request);

        requestManager.updateRequestStatus("12345", RequestStatus.FINISHED);

        assertThat(requestManager.getRequestStatus("12345"), equalTo(RequestStatus.FINISHED));
    }
}
