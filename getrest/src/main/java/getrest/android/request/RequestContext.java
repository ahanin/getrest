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

package getrest.android.request;

import getrest.android.config.ResourceMethod;
import getrest.android.ext.MessageBodyWriter;
import getrest.android.resource.Packer;
import getrest.android.resource.ResourceContext;
import getrest.android.runtime.GetrestRuntime;
import getrest.android.service.Representation;

public interface RequestContext {

    GetrestRuntime getRuntime();

    @Deprecated
    ResourceContext getResourceContext();

    Packer getPacker();

    <T> MessageBodyWriter<Representation> getMessageBodyWriter();

    RequestManager getRequestManager();

    ResourceMethod getResourceMethod();
}
