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
package getrest.android.resource;

import getrest.android.entity.Marshaller;
import getrest.android.entity.Packer;
import getrest.android.request.Request;
import getrest.android.request.RequestContext;
import getrest.android.service.Representation;
import getrest.android.service.ServiceRequestExecutor;

public class ResourceContextImpl implements ResourceContext {

    private Packer packer;
    private Marshaller marshaller;

    private ServiceRequestExecutor serviceRequestExecutor;

    public RequestContext getRequestContext(Request request) {
        // TODO return new RequestContext associated with ResourceContext 
        final DefaultRequestContext requestContext = new DefaultRequestContext();
        requestContext.setPacker(packer);
        requestContext.setMarshaller(marshaller);
        return requestContext;
    }

    public void setPacker(final Packer packer) {
        this.packer = packer;
    }

    public Packer getPacker() {
        return this.packer;
    }

    public <T> void setMarshaller(final Marshaller<T, Representation> marshaller) {
        this.marshaller = marshaller;
    }

    public <T> Marshaller<T, Representation> getMarshaller() {
        return this.marshaller;
    }

    public void setServiceRequestExecutor(final ServiceRequestExecutor serviceRequestExecutor) {
        this.serviceRequestExecutor = serviceRequestExecutor;
    }

    public ServiceRequestExecutor getServiceRequestExecutor() {
        return this.serviceRequestExecutor;
    }

    /**
     * This is a temporary solution.
     * TODO remove when "real" class is implemented
     */
    private static class DefaultRequestContext implements RequestContext {

        private Packer packer;
        private Marshaller marshaller;

        public void setPacker(final Packer packer) {
            this.packer = packer;
        }

        public Packer getPacker() {
            return this.packer;
        }

        public <T> void setMarshaller(final Marshaller<T, Representation> marshaller) {
            this.marshaller = marshaller;
        }

        public <T> Marshaller<T, Representation> getMarshaller() {
            return this.marshaller;
        }
    }

}