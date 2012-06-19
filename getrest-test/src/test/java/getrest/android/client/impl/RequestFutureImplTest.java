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

import getrest.android.client.RequestCallback;
import getrest.android.core.Request;
import getrest.android.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class RequestFutureImplTest {

    private RequestFutureImpl requestFuture;

    @Before
    public void setUp() throws Exception {
        requestFuture = new RequestFutureImpl();
    }

    @Test
    public void testFirePendingShouldPassRequest() throws Exception {
        final RequestCallback callback = mock(RequestCallback.class);
        final Request request = mock(Request.class);
        requestFuture.setRequestCallback(callback);
        requestFuture.setRequest(request);

        requestFuture.firePending();

        verify(callback).onPending(request);
    }

    @Test
    public void testFireExecutingShouldPassRequest() throws Exception {
        final RequestCallback callback = mock(RequestCallback.class);
        final Request request = mock(Request.class);
        requestFuture.setRequestCallback(callback);
        requestFuture.setRequest(request);

        requestFuture.fireExecuting();

        verify(callback).onExecuting(request);
    }

    @Test
    public void testFireFinishedShouldPassResponse() throws Exception {
        final RequestCallback callback = mock(RequestCallback.class);
        requestFuture.setRequestCallback(callback);

        final Response response = mock(Response.class);
        requestFuture.fireFinished(response);

        verify(callback).onFinished(response);
    }

    @Test
    public void testShouldFirePending() throws Exception {
        final RequestCallback callback = mock(RequestCallback.class);
        requestFuture.setRequestCallback(callback);

        requestFuture.firePending();

        verify(callback).onPending(any(Request.class));
        verify(callback, never()).onExecuting(any(Request.class));
        verify(callback, never()).onFinished(any(Response.class));
    }

    @Test
    public void testShouldFirePendingIfWasNotFired() throws Exception {
        final RequestCallback callback = mock(RequestCallback.class);
        requestFuture.setRequestCallback(callback);

        final InOrder inOrder = inOrder(callback);

        requestFuture.fireExecuting();

        inOrder.verify(callback).onPending(any(Request.class));
        inOrder.verify(callback).onExecuting(any(Request.class));
        verify(callback, never()).onFinished(any(Response.class));
    }

    @Test
    public void testShouldFireExecutingIfWasNotFired() throws Exception {
        final RequestCallback callback = mock(RequestCallback.class);
        requestFuture.setRequestCallback(callback);

        final InOrder inOrder = inOrder(callback);

        requestFuture.fireFinished(mock(Response.class));

        inOrder.verify(callback).onPending(any(Request.class));
        inOrder.verify(callback).onExecuting(any(Request.class));
        inOrder.verify(callback).onFinished(any(Response.class));
    }

    @Test
    public void testShouldCallCallbackOnlyOncePerEvent() throws Exception {
        final RequestCallback callback = mock(RequestCallback.class);
        requestFuture.setRequestCallback(callback);

        requestFuture.firePending();
        requestFuture.fireExecuting();
        requestFuture.fireFinished(mock(Response.class));

        verify(callback).onPending(any(Request.class));
        verify(callback).onExecuting(any(Request.class));
        verify(callback).onFinished(any(Response.class));

        verifyNoMoreInteractions(callback);
    }
}
