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
package getrest.android.core;

import android.net.Uri;

public class BaseRequest implements HasHeaders {

    private String requestId;
    private Uri uri;
    private Method method;
    private MediaType mediaType;
    private Headers headers = new Headers();

    public BaseRequest() {
    }

    public BaseRequest(final Request request) {
        this.requestId = request.getRequestId();
        this.uri = request.getUri();
        this.method = request.getMethod();
        this.mediaType = request.getMediaType();
        this.headers = new Headers(request.getHeaders());
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(final Uri uri) {
        this.uri = uri;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(final Method method) {
        this.method = method;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(final MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void addHeader(Header header) {
        this.headers.add(header);
    }
}
