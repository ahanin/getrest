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
import getrest.android.core.GetrestRuntime;
import getrest.android.core.Pack;
import getrest.android.util.Preconditions;

public final class HttpRequestBuilder {

    private Context context;

    private Method method;
    private Pack entityPack;
    private Object entity;

    private HttpRequestBuilder(final Context context) {
        this.context = context;
    }

    private HttpRequestBuilder() {
    }

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

    public HttpRequestBuilder withEntity(final Pack entityPack) {
        this.entityPack = entityPack;
        return this;
    }

    public HttpRequestBuilder withEntity(final Object entity) {
        this.entity = entity;
        return this;
    }


    public HttpRequest build() {
        Preconditions.checkState(!(this.entityPack != null && this.entity != null),
                "Entity and entity pack cannot are both set, but cannot be sent both at a time");

        final HttpRequest request = new HttpRequest();
        request.setMethod(this.method);
        if (this.entityPack != null) {
            request.setEntity(this.entityPack);
        } else if (this.entity != null) {
            Preconditions.checkState(context != null,
                    "Context must be set to a builder before parcelizing and entity. " +
                            "Use HttpRequestBuilder.newRequestBuilder(Context) to build a context with Context.");

            request.setEntity(GetrestRuntime.getInstance(context).getPacker().pack(this.entity));
        }
        return request;
    }
}
