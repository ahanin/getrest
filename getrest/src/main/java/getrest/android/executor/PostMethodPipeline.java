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

import getrest.android.core.HandlerException;
import getrest.android.core.Headers;
import getrest.android.core.Loggers;
import getrest.android.core.Pack;
import getrest.android.core.Request;
import getrest.android.core.ResponseParcelable;

import getrest.android.ext.MessageBodyWriter;

import getrest.android.request.RequestContext;
import getrest.android.request.RequestLifecycle;

import getrest.android.service.Representation;
import getrest.android.service.ServiceRequest;
import getrest.android.service.ServiceRequestExecutor;
import getrest.android.service.ServiceResponse;

import getrest.android.util.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

// TODO transform to a general-purpose request handler
class PostMethodPipeline implements RequestPipeline {
    private static final Logger LOGGER = Loggers.getServiceLogger();
    private RequestLifecycle requestLifecycle;
    private RequestContext requestContext;
    private ServiceRequestExecutor serviceRequestExecutor;

    public void setRequestLifecycle(final RequestLifecycle requestLifecycle) {
        this.requestLifecycle = requestLifecycle;
    }

    public void setRequestContext(final RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    public void setServiceRequestExecutor(
        final ServiceRequestExecutor serviceRequestExecutor) {
        this.serviceRequestExecutor = serviceRequestExecutor;
    }

    public void handle(final Request request,
        final ResponseParcelable responseParcelable) throws HandlerException {
        final ServiceRequest serviceRequest = new ServiceRequest(request);

        // marshal entity, if needed
        requestLifecycle.beforeMarshal();

        if (request.getEntity() != null) {
            try {
                final Representation representation = getRepresentation(request);

                serviceRequest.setEntity(representation);
            } catch (Exception ex) {
                LOGGER.error("Exception while marshalling entity", ex);
                throw new HandlerException("Exception while marshalling entity",
                    ex);
            }
        }

        requestLifecycle.afterMarshal();

        // execute request
        final ServiceResponse serviceResponse = new ServiceResponse();

        try {
            serviceRequestExecutor.execute(serviceRequest, serviceResponse);
        } catch (IOException ex) {
            throw new HandlerException("I/O exception", ex);
        }

        // unmarshal the entity, if needed
        requestLifecycle.beforeUnmarshal();

        if (serviceResponse.getEntity() != null) {
            try {
                final MessageBodyWriter<Representation> messageBodyWriter =
                    requestContext.getMessageBodyWriter();
                final Object resultUnmarshalled = messageBodyWriter.unmarshal(serviceResponse.getEntity());
                final Pack result = requestContext.getPacker()
                                                  .pack(resultUnmarshalled);

                responseParcelable.setEntity(result);
            } catch (Exception ex) {
                LOGGER.error("Exception while unmarshalling entity", ex);
                throw new HandlerException("Exception while marshalling entity",
                    ex);
            }
        }

        requestLifecycle.afterUnmarshal();
    }

    private Representation getRepresentation(final Request request) {
        final MessageBodyWriter<Representation> messageBodyWriter = requestContext.getMessageBodyWriter();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        final Headers headers = new Headers();

        messageBodyWriter.write(request.getEntity().unpack(),
            request.getMediaType(), headers, outputStream);

        return new ByteArrayBufferRepresentation(outputStream.toByteArray());
    }
}
