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
import getrest.android.entity.Pack;
import getrest.android.request.Method;
import getrest.android.request.Request;

/**
 * @author aha
 * @since 2012-01-16
 */
public class ServiceContext {

    public <T> Pack<T> pack(final Uri uri, final Method post, final T entity) {
        // TODO finish implementation
        throw new UnsupportedOperationException();
    }

    public RequestExecutor getRequestExecutor(final Request request) {
        return new RequestExecutorImpl(request);
    }

}
