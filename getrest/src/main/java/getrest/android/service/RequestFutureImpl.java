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
package getrest.android.service;

import getrest.android.core.RequestCallback;

import getrest.android.core.Request;
import getrest.android.core.RequestFuture;

class RequestFutureImpl<R extends Request<V>, V> implements RequestFuture<V> {

    private final RequestFutureSupport<R, V> requestFutureSupport;
    private V result;

    RequestFutureImpl(final RequestFutureSupport<R, V> requestFutureSupport) {
        this.requestFutureSupport = requestFutureSupport;
    }

    public V get() {

        return requestFutureSupport.getResult();
    }

    public void setRequestCallback(final RequestCallback requestCallback) {
        this.requestFutureSupport.setRequestCallback(requestCallback);
    }
}
