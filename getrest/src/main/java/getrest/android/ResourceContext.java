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
package getrest.android;

import getrest.android.entity.Marshaller;
import getrest.android.entity.Packer;
import getrest.android.request.Request;
import getrest.android.request.RequestContext;
import getrest.android.service.Representation;
import getrest.android.service.ServiceRequestExecutor;

public class ResourceContext {

    public RequestContext getRequestContext(Request request) {
        // TODO return associated ResourceContext
        throw new UnsupportedOperationException();
    }

    public <T> Packer<T> getPacker() {
        // TODO return entity packer
        throw new UnsupportedOperationException();
    }

    public <T> Marshaller<T, Representation> getMarshaller() {
        // TODO return entity marshaller
        throw new UnsupportedOperationException();
    }

    public ServiceRequestExecutor getServiceRequestExecutor() {
        // TODO return ServiceRequestExecutor
        throw new UnsupportedOperationException();
    }

}
