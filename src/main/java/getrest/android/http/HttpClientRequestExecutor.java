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

import getrest.android.Method;
import getrest.android.Request;
import getrest.android.Response;
import getrest.android.service.RequestExecutor;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * @author aha
 * @since 2012-01-13
 */
public class HttpClientRequestExecutor implements RequestExecutor {

    @Override
    public void execute(final Request request, final Response response) {
        final DefaultHttpClient httpClient = new DefaultHttpClient();
        final HttpUriRequest httpRequest = createHttpRequest(request);

        final HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpRequest);
        } catch (IOException ex) {
            response.setFailed(true);
        }

        // TODO finish HTTP request implementation
    }

    private HttpUriRequest createHttpRequest(final Request request) {
        final Method method = request.getMethod();
        if (method == null) {
            throw new IllegalArgumentException("Method must be specified in request");
        }

        final HttpUriRequest httpRequest;
        if (Method.GET.equals(method)) {
            httpRequest = new HttpGet();
        } else if (Method.POST.equals(method)) {
            httpRequest = new HttpPost();
        } else if (Method.DELETE.equals(method)) {
            httpRequest = new HttpDelete();
        } else {
            throw new IllegalStateException("Unsupported method");
        }

        return httpRequest;
    }
}
