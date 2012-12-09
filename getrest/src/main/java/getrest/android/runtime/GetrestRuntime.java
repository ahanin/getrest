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
package getrest.android.runtime;

import android.content.Context;
import getrest.android.client.InMemoryRequestManager;
import getrest.android.config.Config;
import getrest.android.config.HasConfig;
import getrest.android.config.ResourceMethod;
import getrest.android.core.Application;
import getrest.android.core.Pack;
import getrest.android.core.Request;
import getrest.android.ext.MessageBodyWriter;
import getrest.android.request.RequestContext;
import getrest.android.request.RequestManager;
import getrest.android.resource.Packer;
import getrest.android.resource.ResourceContext;
import getrest.android.resource.ResourceContextImpl;
import getrest.android.service.Representation;
import getrest.android.util.Objects;

import java.util.HashMap;

public class GetrestRuntime {

    private static final HashMap<Context,GetrestRuntime> cache = new HashMap<Context,GetrestRuntime>(
        1);

    private static final DefaultPacker DEFAULT_PACKER = new DefaultPacker();

    private RequestManager requestManager = new InMemoryRequestManager();
    private Config config;

    public static GetrestRuntime getInstance(final Context context) {
        final Context applicationContext = context.getApplicationContext();

        if (!cache.containsKey(applicationContext)) {
            synchronized (cache) {
                if (!cache.containsKey(applicationContext)) {
                    if (!(applicationContext instanceof HasConfig)) {
                        throw new IllegalStateException(
                                "Unable to find configuration. Does your Android "
                                        + Application.class.getSimpleName() + " implement "
                                        + HasConfig.class.getName());
                    }

                    synchronized (applicationContext) {
                        cache.put(
                                applicationContext,
                                new GetrestRuntime(
                                        ((HasConfig) applicationContext)
                                                .getGetrestConfig()));
                    }
                }
            }
        }

        return cache.get(applicationContext);
    }

    private GetrestRuntime(final Config config) {
        this.config = config;
    }

    public RequestContext getRequestContext(final Request request) {
        for (Application application : config.getApplications()) {
            final ResourceMethod resourceMethod = application.getResourceMethodResolver()
                    .getResourceMethod(request);

            if (resourceMethod != null) {
                final DefaultRequestContext requestContext = new DefaultRequestContext();
                requestContext.setRuntime(this);
                requestContext.setResourceMethod(resourceMethod);

                final Class entityType = Objects.safeFirstNotNull(request.getEntityType(), resourceMethod.getEntityType());
                if (entityType != null) {
                    requestContext.setEntityPacker(Objects.firstNotNull(application.getPackerResolver()
                            .getPacker(entityType), DEFAULT_PACKER));
                }

                final Class resultType = Objects.safeFirstNotNull(request.getReturnType(), resourceMethod.getResultType());
                if (resultType != null) {
                    requestContext.setEntityPacker(Objects.firstNotNull(application.getPackerResolver()
                            .getPacker(resultType), DEFAULT_PACKER));
                }

                return requestContext;
            }
        }

        throw new IllegalStateException("No matching methods found");
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public static class DefaultRequestContext implements RequestContext {
        private GetrestRuntime runtime;
        private Packer entityPacker;
        private Packer resultPacker;
        private MessageBodyWriter messageBodyWriter;
        private ResourceContextImpl resourceContext;
        private ResourceMethod resourceMethod;

        public GetrestRuntime getRuntime() {
            return runtime;
        }

        public void setRuntime(final GetrestRuntime runtime) {
            this.runtime = runtime;
        }

        public ResourceContext getResourceContext() {
            return resourceContext;
        }

        public void setEntityPacker(final Packer entityPacker) {
            this.entityPacker = entityPacker;
        }

        public Packer getEntityPacker() {
            return this.entityPacker;
        }

        public Packer getResultPacker() {
            return resultPacker;
        }

        public void setResultPacker(final Packer resultPacker) {
            this.resultPacker = resultPacker;
        }

        public void setMessageBodyWriter(final MessageBodyWriter<Representation> messageBodyWriter) {
            this.messageBodyWriter = messageBodyWriter;
        }

        public <T> MessageBodyWriter<Representation> getMessageBodyWriter() {
            return this.messageBodyWriter;
        }

        public RequestManager getRequestManager() {
            return resourceContext.getRequestManager();
        }

        public ResourceMethod getResourceMethod() {
            return resourceMethod;
        }

        public void setResourceMethod(final ResourceMethod resourceMethod) {
            this.resourceMethod = resourceMethod;
        }
    }

    private static class DefaultPacker implements Packer {
        public <T> Pack<T> pack(final T object) {
            throw new UnsupportedOperationException(
                    "Default packing is not implemented yet");
        }
    }
}
