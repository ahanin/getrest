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

import getrest.android.core.Pack;
import getrest.android.core.Request;
import getrest.android.core.Response;

import getrest.android.request.RequestContext;
import getrest.android.request.RequestLifecycle;

import getrest.android.resource.Marshaller;
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
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostMethodPipelineTest {
    private PostMethodPipeline requestExecutor;
    private Request request;
    private Marshaller<Object, Representation> marshaller;
    private Packer packer;
    private RequestContext requestContext;
    private RequestLifecycle requestLifecycle;
    private ServiceRequestExecutor serviceRequestExecutor;
    private Response response;

    @Before
    public void setUp() throws Exception {
        request = new Request();
        response = new Response();

        requestExecutor = new PostMethodPipeline();
        requestLifecycle = mock(RequestLifecycle.class);
        requestContext = mock(RequestContext.class);
        serviceRequestExecutor = mock(ServiceRequestExecutor.class);

        marshaller = mock(Marshaller.class);
        packer = mock(Packer.class);

        when(requestContext.getMarshaller()).thenReturn(marshaller);
        when(requestContext.getPacker()).thenReturn(packer);

        requestExecutor.setRequestLifecycle(requestLifecycle);
        requestExecutor.setRequestContext(requestContext);
        requestExecutor.setServiceRequestExecutor(serviceRequestExecutor);
    }

    @Test
    public void testShouldExecuteRequest() throws Exception {

        requestExecutor.handle(request, response);

        verify(serviceRequestExecutor).execute(any(ServiceRequest.class), any(ServiceResponse.class));
    }

    @Test
    public void testShouldCallService() throws Exception {
        final Pack requestEntityPack = mock(Pack.class);
        request.setEntity(requestEntityPack);

        final Representation marshalledEntity = mock(Representation.class);
        when(marshaller.marshal(requestEntityPack)).thenReturn(marshalledEntity);

        requestExecutor.handle(request, response);

        verify(serviceRequestExecutor)
                .execute(any(ServiceRequest.class), any(ServiceResponse.class));
    }

    @Test
    public void testShouldUseRequestEntity() throws Exception {
        final Pack requestEntityPack = mock(Pack.class);
        request.setEntity(requestEntityPack);

        final Object requestEntity = new Object();
        when(requestEntityPack.unpack()).thenReturn(requestEntity);

        final Representation marshalledEntity = mock(Representation.class);
        when(marshaller.marshal(requestEntity)).thenReturn(marshalledEntity);

        final ArgumentCaptor<ServiceRequest> serviceRequestCaptor = ArgumentCaptor.forClass(ServiceRequest.class);
        final ArgumentCaptor<ServiceResponse> serviceResponseCaptor = ArgumentCaptor.forClass(ServiceResponse.class);

        doNothing().when(serviceRequestExecutor)
                .execute(serviceRequestCaptor.capture(),
                        serviceResponseCaptor.capture());

        requestExecutor.handle(request, response);

        final ServiceRequest serviceRequest = serviceRequestCaptor.getValue();
        assertThat(serviceRequest.getEntity(), sameInstance(marshalledEntity));
    }

    @Test
    public void testShouldUnmarshallResponse() throws Exception {
        final Pack requestEntityPack = mock(Pack.class);
        request.setEntity(requestEntityPack);

        final Object requestEntity = new Object();
        when(requestEntityPack.unpack()).thenReturn(requestEntity);

        final Representation representation = mock(Representation.class);
        when(marshaller.marshal(requestEntity)).thenReturn(representation);

        final ArgumentCaptor<ServiceRequest> serviceRequestCaptor = ArgumentCaptor.forClass(ServiceRequest.class);
        final ArgumentCaptor<ServiceResponse> serviceResponseCaptor = ArgumentCaptor.forClass(ServiceResponse.class);

        doAnswer(new Answer() {
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                final Object[] args = invocation.getArguments();
                final ServiceResponse serviceResponse = (ServiceResponse) args[1];
                serviceResponse.setEntity(representation);
                return null;
            }
        }).when(serviceRequestExecutor).execute(serviceRequestCaptor.capture(), serviceResponseCaptor.capture());

        final ArgumentCaptor<Representation> responseEntityCaptor = ArgumentCaptor.forClass(Representation.class);
        final Object responseEntity = mock(Object.class);
        when(marshaller.unmarshal(responseEntityCaptor.capture()))
                .thenReturn(responseEntity);

        final Pack responseEntityPack = mock(Pack.class);
        when(packer.pack(responseEntity)).thenReturn(responseEntityPack);

        requestExecutor.handle(request, response);

        assertThat(response.getEntity(), sameInstance(responseEntityPack));
    }

}
