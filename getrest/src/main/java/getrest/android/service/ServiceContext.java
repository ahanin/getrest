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

package getrest.android.service;

import android.net.Uri;
import getrest.android.ResourceContext;
import getrest.android.entity.Marshaller;
import getrest.android.entity.Packer;
import getrest.android.executor.RequestExecutor;
import getrest.android.executor.RequestExecutorImpl;
import getrest.android.request.RequestLifecycle;
import getrest.android.request.Method;
import getrest.android.request.Request;
import getrest.android.request.RequestContext;
import getrest.android.util.Logger;
import getrest.android.util.LoggerFactory;

/**
 * @author aha
 * @since 2012-01-16
 */
public class ServiceContext {

    public ResourceContext getResourceContext(final Uri uri, final Method method) {
        // TODO resolve ResourceContext by resource URI and method
        throw new UnsupportedOperationException();
    }

    public ResourceContext getResourceContext(Request request) {
        // TODO resolve ResourceContext by request
        throw new UnsupportedOperationException();
    }

    // TODO move to ResourceContext and reuse by leveraging getResourceContext()
    public RequestExecutor getRequestExecutor(final Request request) {
        final RequestExecutorImpl requestExecutor = new RequestExecutorImpl();
        requestExecutor.setRequest(request);
        requestExecutor.setRequestContext(new DefaultRequestContext());
        requestExecutor.setRequestLifecycle(new DefaultRequestLifecycle());
        return requestExecutor;
    }

    public static ServiceContext forRequest(final Request request) {
        // TODO return ServiceContext based on request attributes
        throw new UnsupportedOperationException();
    }

    /**
     * This is a temporary solution.
     * TODO remove when "real" class is implemented
     */
    private static class DefaultRequestContext implements RequestContext {

        private Packer packer;
        private Marshaller marshaller;

        public <T> void setPacker(final Packer<T> packer) {
            this.packer = packer;
        }

        public <T> Packer<T> getPacker() {
            return this.packer;
        }

        public <T> void setMarshaller(final Marshaller<T, Representation> marshaller) {
            this.marshaller = marshaller;
        }

        public <T> Marshaller<T, Representation> getMarshaller() {
            return this.marshaller;
        }
    }

    /**
     * This is a temporary solution.
     * TODO remove when "real" class is implemented
     */
    private static class DefaultRequestLifecycle implements RequestLifecycle {

        private static final Logger LOGGER = LoggerFactory.getLogger("GetRest:RequestCycle");

        public void beforeMarshal() {
            LOGGER.trace("before marshal");
        }

        public void afterMarshal() {
            LOGGER.trace("after marshal");
        }

        public void beforeUnmarshal() {
            LOGGER.trace("before unmarshal");
        }

        public void afterUnmarshal() {
            LOGGER.trace("after unmarshal");
        }
    }
}
