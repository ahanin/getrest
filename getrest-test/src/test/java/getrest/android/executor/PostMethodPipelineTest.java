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
package getrest.android.executor;

import getrest.android.core.Headers;
import getrest.android.core.MediaType;
import getrest.android.core.Pack;
import getrest.android.core.Request;
import getrest.android.core.ResponseParcelable;

import getrest.android.ext.MessageBodyWriter;

import getrest.android.request.RequestContext;
import getrest.android.request.RequestLifecycle;

import getrest.android.resource.Packer;

import getrest.android.service.Representation;
import getrest.android.service.ServiceRequest;
import getrest.android.service.ServiceRequestExecutor;
import getrest.android.service.ServiceResponse;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.invocation.InvocationOnMock;

import org.mockito.stubbing.Answer;

import java.io.OutputStream;
public class PostMethodPipelineTest {
    private PostMethodPipeline requestExecutor;
    private Request request;
    private MessageBodyWriter<Representation> messageBodyWriter;
    private Packer packer;
    private RequestContext requestContext;
    private RequestLifecycle requestLifecycle;
    private ServiceRequestExecutor serviceRequestExecutor;
    private ResponseParcelable responseParcelable;

    @Before
    public void setUp() throws Exception {
        request = new Request();
        responseParcelable = new ResponseParcelable();

        requestExecutor = new PostMethodPipeline();
        requestLifecycle = mock(RequestLifecycle.class);
        requestContext = mock(RequestContext.class);
        serviceRequestExecutor = mock(ServiceRequestExecutor.class);

        messageBodyWriter = mock(MessageBodyWriter.class);
        packer = mock(Packer.class);

        when(requestContext.getMessageBodyWriter()).thenReturn(messageBodyWriter);
        when(requestContext.getPacker()).thenReturn(packer);

        requestExecutor.setRequestLifecycle(requestLifecycle);
        requestExecutor.setRequestContext(requestContext);
        requestExecutor.setServiceRequestExecutor(serviceRequestExecutor);
    }

    @Test
    public void testShouldExecuteRequest() throws Exception {
        requestExecutor.handle(request, responseParcelable);

        verify(serviceRequestExecutor)
            .execute(any(ServiceRequest.class), any(ServiceResponse.class));
    }

    @Test
    public void testShouldCallService() throws Exception {
        final Pack requestEntityPack = mock(Pack.class);

        request.setEntity(requestEntityPack);

        doNothing().when(messageBodyWriter)
            .write(requestEntityPack, null, null, null);

        requestExecutor.handle(request, responseParcelable);

        verify(serviceRequestExecutor)
            .execute(any(ServiceRequest.class), any(ServiceResponse.class));
    }

    @Test
    public void testShouldUseRequestEntity() throws Exception {
        final Pack requestEntityPack = mock(Pack.class);

        request.setEntity(requestEntityPack);

        final Object requestEntity = new Object();

        when(requestEntityPack.unpack()).thenReturn(requestEntity);

        doNothing().when(messageBodyWriter)
            .write(eq(requestEntity), any(MediaType.class), any(Headers.class),
            any(OutputStream.class));

        final ArgumentCaptor<ServiceRequest> serviceRequestCaptor = ArgumentCaptor.forClass(ServiceRequest.class);
        final ArgumentCaptor<ServiceResponse> serviceResponseCaptor = ArgumentCaptor.forClass(ServiceResponse.class);

        doNothing().when(serviceRequestExecutor)
            .execute(serviceRequestCaptor.capture(),
            serviceResponseCaptor.capture());

        requestExecutor.handle(request, responseParcelable);

        final ServiceRequest serviceRequest = serviceRequestCaptor.getValue();

        verify(messageBodyWriter)
            .write(eq(requestEntity), any(MediaType.class), any(Headers.class),
            any(OutputStream.class));
    }

    @Test
    public void testShouldUnmarshallResponse() throws Exception {
        final Pack requestEntityPack = mock(Pack.class);

        request.setEntity(requestEntityPack);

        final Object requestEntity = new Object();

        when(requestEntityPack.unpack()).thenReturn(requestEntity);

        final Representation representation = mock(Representation.class);

        doNothing().when(messageBodyWriter)
            .write(requestEntity, null, null, null);

        final ArgumentCaptor<ServiceRequest> serviceRequestCaptor = ArgumentCaptor.forClass(ServiceRequest.class);
        final ArgumentCaptor<ServiceResponse> serviceResponseCaptor = ArgumentCaptor.forClass(ServiceResponse.class);

        doAnswer(new Answer() {
                public Object answer(final InvocationOnMock invocation)
                    throws Throwable {
                    final Object[] args = invocation.getArguments();
                    final ServiceResponse serviceResponse = (ServiceResponse) args[1];

                    serviceResponse.setEntity(representation);

                    return null;
                }
            }).when(serviceRequestExecutor)
            .execute(serviceRequestCaptor.capture(),
            serviceResponseCaptor.capture());

        final ArgumentCaptor<Representation> responseEntityCaptor = ArgumentCaptor.forClass(Representation.class);
        final Object responseEntity = mock(Object.class);

        when(messageBodyWriter.unmarshal(responseEntityCaptor.capture()))
            .thenReturn(responseEntity);

        final Pack responseEntityPack = mock(Pack.class);

        when(packer.pack(responseEntity)).thenReturn(responseEntityPack);

        requestExecutor.handle(request, responseParcelable);

        assertThat(responseParcelable.getEntity(),
            sameInstance(responseEntityPack));
    }
}
