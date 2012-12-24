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

import getrest.android.service.ServiceRequest;
import getrest.android.service.ServiceRequestExecutor;
import getrest.android.service.ServiceResponse;

import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import java.net.URI;


/**
 * @author aha
 * @since 2012-01-13
 */
public class HttpServiceRequestExecutor implements ServiceRequestExecutor {

    public void execute(final ServiceRequest request,
        final ServiceResponse response) throws IOException {
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        final HttpUriRequest httpRequest = createHttpRequest(request);

        final HttpResponse httpResponse;

        httpResponse = httpClient.execute(httpRequest);
        response.setEntity(new RepresentationHttpEntity(
                httpResponse.getEntity()));

        final org.apache.http.Header[] allHeaders = httpResponse.getAllHeaders();
        final Headers headers = new Headers();

        for (org.apache.http.Header httpHeader : allHeaders) {
            for (HeaderElement headerElement : httpHeader.getElements()) {
                headers.add(new Header(headerElement.getName(),
                        headerElement.getValue()));
            }
        }

        response.setHeaders(headers);

        // TODO finish HTTP request implementation
    }

    private HttpUriRequest createHttpRequest(final ServiceRequest request) {
        final Method method = request.getMethod();

        if (method == null) {
            throw new IllegalArgumentException(
                "Method must be specified in request");
        }

        final HttpUriRequest httpRequest;

        if (Method.GET.equals(method)) {
            httpRequest = new HttpGet();
        } else if (Method.POST.equals(method)) {
            final HttpPost post = new HttpPost();
            post.setURI(URI.create(request.getUri().toString()));
            post.setEntity(new HttpEntityRepresentation(request.getEntity()));
            httpRequest = post;
        } else if (Method.DELETE.equals(method)) {
            httpRequest = new HttpDelete();
        } else {
            throw new IllegalStateException("Unsupported method");
        }

        if (request.getHeaders().count() > 0) {
            for (Header header : request.getHeaders()) {
                httpRequest.addHeader(header.getName(), header.getValue());
            }
        }

        return httpRequest;
    }
}
