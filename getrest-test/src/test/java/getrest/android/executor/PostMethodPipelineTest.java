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

import getrest.android.resource.Marshaller;
import getrest.android.core.Pack;
import getrest.android.resource.Packer;
import getrest.android.core.Request;
import getrest.android.request.RequestContext;
import getrest.android.request.RequestLifecycle;
import getrest.android.core.Response;
import getrest.android.service.Representation;
import getrest.android.service.ServiceRequest;
import getrest.android.service.ServiceRequestExecutor;
import getrest.android.service.ServiceResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
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
        request = mock(Request.class);
        response = mock(Response.class);

        requestExecutor = new PostMethodPipeline();
        requestLifecycle = mock(RequestLifecycle.class);
        requestContext = mock(RequestContext.class);
        serviceRequestExecutor = mock(ServiceRequestExecutor.class);

        marshaller = mock(Marshaller.class);
        packer = mock(Packer.class);


        when(requestContext.getMarshaller()).thenReturn(marshaller);
        when(requestContext.getPacker()).thenReturn(packer);

        requestExecutor.setRequest(request);
        requestExecutor.setRequestLifecycle(requestLifecycle);
        requestExecutor.setRequestContext(requestContext);
        requestExecutor.setServiceRequestExecutor(serviceRequestExecutor);
    }

    @Test
    public void testExecute() throws Exception {
        final Pack requestEntityPack = mock(Pack.class);
        final Object requestEntity = new Object();
        when(requestEntityPack.unpack()).thenReturn(requestEntity);
        when(request.getEntity()).thenReturn(requestEntityPack);

        final Representation requestRepresentation = mock(Representation.class);
        when(marshaller.marshal(requestEntityPack)).thenReturn(requestRepresentation);

        final Object unmarshalledResult = new Object();
        when(marshaller.unmarshal(any(Representation.class))).thenReturn(unmarshalledResult);
        final Pack resultEntityPack = mock(Pack.class);
        when(packer.pack(unmarshalledResult)).thenReturn(resultEntityPack);

        requestExecutor.handle(request, response);

        verify(response).setEntity(resultEntityPack);
    }

    @Test
    public void testShouldCallService() throws Exception {
        final Pack requestEntityPack = mock(Pack.class);
        when(request.getEntity()).thenReturn(requestEntityPack);

        final Representation marshalledEntity = mock(Representation.class);
        when(marshaller.marshal(requestEntityPack)).thenReturn(marshalledEntity);

        requestExecutor.handle(request, response);

        verify(serviceRequestExecutor).execute(any(ServiceRequest.class), any(ServiceResponse.class));
    }

    @Test
    public void testShouldUseRequestEntity() throws Exception {
        final Pack requestEntityPack = mock(Pack.class);
        when(request.getEntity()).thenReturn(requestEntityPack);
        final Object requestEntity = new Object();
        when(requestEntityPack.unpack()).thenReturn(requestEntity);

        final Representation marshalledEntity = mock(Representation.class);
        when(marshaller.marshal(requestEntity)).thenReturn(marshalledEntity);

        final ArgumentCaptor<ServiceRequest> serviceRequestCaptor = ArgumentCaptor.forClass(ServiceRequest.class);
        final ArgumentCaptor<ServiceResponse> serviceResponseCaptor = ArgumentCaptor.forClass(ServiceResponse.class);

        doNothing().when(serviceRequestExecutor).execute(serviceRequestCaptor.capture(),
                serviceResponseCaptor.capture());

        requestExecutor.handle(request, response);

        final ServiceRequest serviceRequest = serviceRequestCaptor.getValue();
        assertThat(serviceRequest.getEntity(), sameInstance(marshalledEntity));
    }

    @Test
    public void testShouldUnmarshallResponse() throws Exception {
        final Pack requestEntityPack = mock(Pack.class);
        when(request.getEntity()).thenReturn(requestEntityPack);
        final Object requestEntity = new Object();
        when(requestEntityPack.unpack()).thenReturn(requestEntity);

        final Representation marshalledEntity = mock(Representation.class);
        when(marshaller.marshal(requestEntity)).thenReturn(marshalledEntity);

        final ArgumentCaptor<ServiceRequest> serviceRequestCaptor = ArgumentCaptor.forClass(ServiceRequest.class);
        final ArgumentCaptor<ServiceResponse> serviceResponseCaptor = ArgumentCaptor.forClass(ServiceResponse.class);

        doNothing().when(serviceRequestExecutor).execute(serviceRequestCaptor.capture(),
                serviceResponseCaptor.capture());

        final ArgumentCaptor<Representation> responseEntityCaptor = ArgumentCaptor.forClass(Representation.class);
        final Object responseEntity = mock(Object.class);
        when(marshaller.unmarshal(responseEntityCaptor.capture())).thenReturn(responseEntity);
        final Pack responseEntityPack = mock(Pack.class);
        when(packer.pack(responseEntity)).thenReturn(responseEntityPack);

        requestExecutor.handle(request, response);

        verify(response).setEntity(responseEntityPack);

        assertThat(responseEntityCaptor.getValue(), sameInstance(serviceResponseCaptor.getValue().getEntity()));
    }

    @Test
    public void testWorkflowOrder() throws Exception {
        final InOrder inOrder = inOrder(requestLifecycle, request, requestContext, marshaller, serviceRequestExecutor);

        final Object requestEntity = new Object();
        final Pack requestEntityPack = mock(Pack.class);
        when(request.getEntity()).thenReturn(requestEntityPack);
        when(requestEntityPack.unpack()).thenReturn(requestEntity);

        final ArgumentCaptor<ServiceResponse> serviceResponse = ArgumentCaptor.forClass(ServiceResponse.class);
        final ArgumentCaptor<Representation> responseRepresentation = ArgumentCaptor.forClass(Representation.class);

        requestExecutor.handle(request, response);

        inOrder.verify(requestLifecycle).beforeMarshal();
        inOrder.verify(marshaller).marshal(requestEntity);
        inOrder.verify(requestLifecycle).afterMarshal();

        inOrder.verify(serviceRequestExecutor).execute(any(ServiceRequest.class), serviceResponse.capture());
        inOrder.verify(requestLifecycle).beforeUnmarshal();
        inOrder.verify(marshaller).unmarshal(responseRepresentation.capture());
        inOrder.verify(requestLifecycle).afterUnmarshal();
    }

}
