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

import getrest.android.request.Method;
import getrest.android.request.Request;
import getrest.android.request.RequestContext;
import getrest.android.request.RequestLifecycle;
import getrest.android.request.Response;
import getrest.android.service.ServiceRequestExecutor;

/**
 * @author aha
 * @since 2012-01-18
 */
public class RequestExecutorImpl implements RequestExecutor {

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

    public Response execute() {
        final Method method = request.getMethod();

        if (method == null) {
            throw new IllegalArgumentException("Method must not be null");
        }

        final RequestPipeline pipeline;

        if (Method.GET.equals(method)) {
            // TODO implement GET method pipeline
            throw new UnsupportedOperationException("GET method is not supported yet");
        } else if (Method.POST.equals(method)) {
            final PostMethodPipeline postPipeline = new PostMethodPipeline();
            postPipeline.setRequest(request);
            postPipeline.setRequestContext(requestContext);
            postPipeline.setRequestLifecycle(requestLifecycle);
            postPipeline.setServiceRequestExecutor(serviceRequestExecutor);

            pipeline = postPipeline;
        } else if (Method.PUT.equals(method)) {
            // TODO implement PUT method pipeline
            throw new UnsupportedOperationException("PUT method is not supported yet");
        } else if (Method.DELETE.equals(method)) {
            // TODO implement DELETE method pipeline
            throw new UnsupportedOperationException("PUT method is not supported yet");
        } else {
            throw new IllegalArgumentException("Request method is unsupported: " + method.getName());
        }

        return pipeline.execute(request);
    }

}
