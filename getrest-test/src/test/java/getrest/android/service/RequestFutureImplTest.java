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
package getrest.android.service;

import android.os.Handler;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import getrest.android.client.RequestCallback;

import getrest.android.core.Request;
import static getrest.android.util.GetrestSupport.createRequestFutureSupport;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@RunWith(RobolectricTestRunner.class)
@SuppressWarnings("unchecked")
public class RequestFutureImplTest {

    private Request request = mock(Request.class);
    private RequestCallback requestCallback = mock(RequestCallback.class);
    private RequestFutureImpl requestFuture;
    private RequestFutureSupport requestFutureSupport;

    @Before
    public void setUp() throws Exception {

        final CallerContext callerContext = mock(CallerContext.class);
        final Handler mockHandler = new Handler();
        when(callerContext.getHandler()).thenReturn(mockHandler);
        requestFutureSupport = createRequestFutureSupport(request, callerContext);
        requestFuture = new RequestFutureImpl(requestFutureSupport);
    }

    @Test
    public void testShouldInvokeOnPending() throws Exception {
        requestFuture.setRequestCallback(requestCallback);
        requestFutureSupport.fireOnPending();
        verify(requestCallback).onPending(request);
    }

    @Test
    public void testShouldInvokeOnExecuting() throws Exception {
        requestFuture.setRequestCallback(requestCallback);
        requestFutureSupport.fireOnPending();
        requestFutureSupport.fireOnExecuting();
        verify(requestCallback).onExecuting(request);
    }

    @Test
    public void testShouldInvokeOnFinished() throws Exception {
        requestFuture.setRequestCallback(requestCallback);
        requestFutureSupport.fireOnPending();
        requestFutureSupport.fireOnExecuting();
        requestFutureSupport.fireOnCompleted(new Object());
        verify(requestCallback).onCompleted(request);
    }

    @Test
    public void testShouldInvokeOnError() throws Exception {
        requestFuture.setRequestCallback(requestCallback);
        requestFutureSupport.fireOnPending();
        requestFutureSupport.fireOnExecuting();
        requestFutureSupport.fireOnError(new RuntimeException("should not see me"));
        verify(requestCallback).onError(request);
    }

    @Test
    public void testShouldReplayOnPending() throws Exception {
        requestFutureSupport.fireOnPending();

        requestFuture.setRequestCallback(requestCallback);

        verify(requestCallback).onPending(request);
    }

    @Test
    public void testShouldReplayOnExecuting() throws Exception {
        requestFutureSupport.fireOnPending();
        requestFutureSupport.fireOnExecuting();

        requestFuture.setRequestCallback(requestCallback);

        verify(requestCallback).onExecuting(request);
    }

    @Test
    public void testShouldReplayOnCompleted() throws Exception {
        requestFutureSupport.fireOnPending();
        requestFutureSupport.fireOnExecuting();
        requestFutureSupport.fireOnCompleted(new Object());

        requestFuture.setRequestCallback(requestCallback);

        verify(requestCallback).onCompleted(request);
    }

    @Test
    public void testShouldReplayOnError() throws Exception {
        requestFutureSupport.fireOnPending();
        requestFutureSupport.fireOnExecuting();
        requestFutureSupport.fireOnError(new RuntimeException("should not see me"));

        requestFuture.setRequestCallback(requestCallback);

        verify(requestCallback).onError(request);
    }



}
