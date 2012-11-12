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

import getrest.android.config.ResourceContextContribution;
import getrest.android.executor.RequestHandlerFactory;
import getrest.android.core.Request;
import getrest.android.ext.MessageBodyWriter;
import getrest.android.request.RequestContext;
import getrest.android.request.RequestManager;
import getrest.android.runtime.GetrestRuntime;
import getrest.android.service.Representation;

public class ResourceContextImpl implements ResourceContext, ResourceContextContribution {

    private Packer packer;
    private MessageBodyWriter messageBodyWriter;
    private RequestHandlerFactory requestHandlerFactory;
    private RequestManager requestManager;
    private String contentType;
    private Class<?> resourceType;

    public RequestContext getRequestContext(Request request) {
        final GetrestRuntime.DefaultRequestContext requestContext = new GetrestRuntime.DefaultRequestContext();
        requestContext.setPacker(packer);
        requestContext.setMessageBodyWriter(messageBodyWriter);
        return requestContext;
    }

    public ResourceContext getResourceContext() {
        return this;
    }

    public void setPacker(final Packer packer) {
        this.packer = packer;
    }

    public Packer getPacker() {
        return this.packer;
    }

    public <T> void setMessageBodyWriter(final MessageBodyWriter<Representation> messageBodyWriter) {
        this.messageBodyWriter = messageBodyWriter;
    }

    public <T> MessageBodyWriter<T> getMessageBodyWriter() {
        return this.messageBodyWriter;
    }

    public void setRequestHandlerFactory(final RequestHandlerFactory requestHandler) {
        this.requestHandlerFactory = requestHandler;
    }

    public RequestHandlerFactory getRequestHandlerFactory() {
        return requestHandlerFactory;
    }

    public void setRequestManager(final RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getResourceType() {
        return (Class<T>) resourceType;
    }

    public <T> void setResourceType(final Class<T> resourceType) {
        this.resourceType = resourceType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

}
