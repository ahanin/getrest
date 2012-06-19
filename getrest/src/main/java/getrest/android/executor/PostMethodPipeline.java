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
import getrest.android.core.Request;
import getrest.android.request.RequestContext;
import getrest.android.request.RequestLifecycle;
import getrest.android.core.Response;
import getrest.android.service.Representation;
import getrest.android.service.ServiceRequest;
import getrest.android.service.ServiceRequestExecutor;
import getrest.android.service.ServiceResponse;

// TODO transform to a general-purpose request handler
class PostMethodPipeline implements RequestPipeline {

    private Request request;

    private RequestLifecycle requestLifecycle;

    private RequestContext requestContext;

    private ServiceRequestExecutor serviceRequestExecutor;

    public void setRequest(final Request request) {
        this.request = request;
    }

    public void setRequestLifecycle(final RequestLifecycle requestLifecycle) {
        this.requestLifecycle = requestLifecycle;
    }

    public void setRequestContext(final RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    public void setServiceRequestExecutor(final ServiceRequestExecutor serviceRequestExecutor) {
        this.serviceRequestExecutor = serviceRequestExecutor;
    }

    public void handle(final Request request, final Response response) {
        // marshal
        requestLifecycle.beforeMarshal();

        final Marshaller<Object, Representation> marshaller = requestContext.getMarshaller();

        final Representation representation = marshaller.marshal(this.request.getEntity().unpack());

        requestLifecycle.afterMarshal();

        final ServiceRequest serviceRequest = new ServiceRequest(this.request);
        serviceRequest.setEntity(representation);

        // execute
        final ServiceResponse serviceResponse = new ServiceResponse();
        serviceRequestExecutor.execute(serviceRequest, serviceResponse);

        // unmarshal
        requestLifecycle.beforeUnmarshal();

        final Object resultUnmarshalled = marshaller.unmarshal(serviceResponse.getEntity());
        final Pack result = requestContext.getPacker().pack(resultUnmarshalled);

        requestLifecycle.afterUnmarshal();

        response.setEntity(result);
    }

}
