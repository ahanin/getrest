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
package getrest.android.http;

import android.content.Context;

public final class HttpRequestBuilder {

    private Method method;
    private Object entity;

    private HttpRequestBuilder(final Context context) {}

    private HttpRequestBuilder() {}

    public static HttpRequestBuilder newHttpRequestBuilder(final Context context) {

        return new HttpRequestBuilder(context);
    }

    public static HttpRequestBuilder newHttpRequestBuilder() {

        return new HttpRequestBuilder();
    }

    public HttpRequestBuilder withMethod(final Method method) {
        this.method = method;

        return this;
    }

    public HttpRequestBuilder withEntity(final Object entity) {
        this.entity = entity;

        return this;
    }

    public HttpRequest build() {

        final HttpRequest request = new HttpRequest();
        request.setMethod(this.method);

        if (this.entity != null) {
            request.setEntity(this.entity);
        }

        return request;
    }
}
