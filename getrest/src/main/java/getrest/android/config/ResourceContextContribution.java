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
package getrest.android.config;

import getrest.android.resource.Marshaller;
import getrest.android.resource.Packer;
import getrest.android.executor.RequestHandlerFactory;
import getrest.android.request.RequestManager;
import getrest.android.service.Representation;

public interface ResourceContextContribution {

    void setPacker(Packer packer);

    <T> void setMarshaller(Marshaller<T, Representation> marshaller);

    void setRequestHandlerFactory(RequestHandlerFactory requestHandler);

    void setRequestManager(RequestManager requestManager);

}
