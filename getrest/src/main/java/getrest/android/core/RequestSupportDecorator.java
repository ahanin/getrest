/*
 * Copyright 2013 Alexey Hanin
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
package getrest.android.core;

import getrest.android.client.RequestExecutor;

import getrest.android.util.Preconditions;

public class RequestSupportDecorator<T extends Request> implements RequestSupport<T> {

    private static final RunnableExecutor RUNNABLE_EXECUTOR = new RunnableExecutor();
    private T request;
    private RequestSupport<T> delegate;

    public RequestSupportDecorator(final T request, final RequestSupport<T> delegate) {
        this.delegate = delegate;
        this.request = request;
    }

    public RequestExecutor getRequestExecutor() {

        final RequestExecutor executor = delegate.getRequestExecutor();

        if (executor == null) {
            Preconditions.checkState(request instanceof Runnable, "Request mut be Runnable");

            return RUNNABLE_EXECUTOR;
        } else {

            return null;
        }
    }

    public <E> E getContext(final Class<E> type) {

        final E context = delegate.getContext(type);

        return (context != null) ? context : null;
    }

    public <R> R createResponse(final Class<R> responseType) {

        final R response = delegate.createResponse(responseType);

        if (response != null) {

            return response;
        }

        throw new UnsupportedOperationException("No default response type creation is implemented");
    }
}
