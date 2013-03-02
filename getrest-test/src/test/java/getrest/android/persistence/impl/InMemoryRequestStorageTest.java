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
package getrest.android.persistence.impl;

import getrest.android.core.Request;
import getrest.android.core.RequestStatus;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import static org.mockito.Mockito.mock;
public class InMemoryRequestStorageTest {

    private InMemoryRequestStorage inMemoryRequestStorage = new InMemoryRequestStorage();

    @Test
    public void testGetRequestShouldReturnNull() throws Exception {
        assertThat(inMemoryRequestStorage.loadRequest("12345"), nullValue());
    }

    @Test
    public void testShouldSaveRequest() throws Exception {

        final Request request = mock(Request.class);

        inMemoryRequestStorage.persistRequest("12345", request);

        assertThat(inMemoryRequestStorage.loadRequest("12345"), sameInstance(request));
    }

    @Test
    public void testGetResponseShouldReturnNull() throws Exception {
        assertThat(inMemoryRequestStorage.loadResponse("12345"), nullValue());
    }

    @Test
    public void testGetResponseShouldReturnNullBeforeSaved() throws Exception {

        final Request request = mock(Request.class);

        inMemoryRequestStorage.persistRequest("12345", request);

        assertThat(inMemoryRequestStorage.loadResponse("12345"), nullValue());
    }

    @Test
    public void testShouldNotSaveResponseBeforeRequestAcknowledged() throws Exception {

        final Object response = new Object();

        try {
            inMemoryRequestStorage.persistResponse("12345", response);
            fail("Must raise " + IllegalStateException.class.getName());
        } catch (final IllegalStateException ex) {

            // expected behaviour
        }
    }

    @Test
    public void testShouldSaveResponse() throws Exception {

        final Request request = mock(Request.class);
        inMemoryRequestStorage.persistRequest("12345", request);

        final Object response = new Object();
        inMemoryRequestStorage.persistResponse("12345", response);

        assertThat(inMemoryRequestStorage.loadResponse("12345"), sameInstance(response));
    }

    @Test
    public void testRequestStateShouldBeNullUnlessAcknowledged() throws Exception {
        assertThat(inMemoryRequestStorage.loadRequestStatus("12345"), nullValue());
    }

    @Test
    public void testShouldNotSaveStateForUnacknowledgedRequest() throws Exception {
        try {
            inMemoryRequestStorage.persistRequestStatus("12345", RequestStatus.EXECUTING);
            fail("Must raise " + IllegalStateException.class.getName());
        } catch (final IllegalStateException ex) {

            // expected behaviour
        }
    }

    @Test
    public void testShouldSaveRequestState() throws Exception {

        final Request request = mock(Request.class);
        inMemoryRequestStorage.persistRequest("12345", request);

        inMemoryRequestStorage.persistRequestStatus("12345", RequestStatus.FINISHED);

        assertThat(inMemoryRequestStorage.loadRequestStatus("12345"),
                   equalTo(RequestStatus.FINISHED));
    }
}
