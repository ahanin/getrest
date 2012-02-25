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
import getrest.android.request.RequestController;
import getrest.android.service.Representation;
import getrest.android.service.ServiceRequestExecutor;

public interface ResourceContext {

    /**
     * Returns new {@link RequestContext} instance for the given {@link Request}.
     *
     * @param request {@link Request} object to create {@link RequestContext} for
     * @return new instance of {@link RequestContext} associated with given {@link Request}
     */
    RequestContext getRequestContext(Request request);

    Packer getPacker();

    <T> Marshaller<T, Representation> getMarshaller();

    ServiceRequestExecutor getServiceRequestExecutor();

    RequestController getRequestController();

}
