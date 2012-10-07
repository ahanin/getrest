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

package getrest.android;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import getrest.android.client.RequestCallbackFactory;
import getrest.android.client.RequestFuture;
import getrest.android.client.impl.RestfulClientImpl;
import getrest.android.core.Method;

/**
 * @author aha
 * @since 2012-01-13
 */
public abstract class RestfulClient {

    private String baseUrl;

    private RequestCallbackFactory requestCallbackFactory;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected abstract void init(Context context);

    /**
     * Detach {@link RestfulClient} and release all retained resources.
     */
    public abstract void detach();

    /**
     * Build a request.
     *
     * @param uri@return {@link RequestBuilder} instance, on which {@link getrest.android.RestfulClient.RequestBuilder#execute()}
     * can be called to execute request
     */
    public abstract RequestBuilder request(Uri uri);

    /**
     * Build a request to a path resource.
     *
     * @param path
     * @return
     */
    public RequestBuilder request(String path) {
        return request(buildUri(path));
    }

    private Uri buildUri(final String path) {
        return this.baseUrl == null ? Uri.parse(path) : Uri.parse(gluePath(baseUrl, path));
    }

    private String gluePath(final String baseUrl, final String path) {
        final StringBuilder sb = new StringBuilder(baseUrl);
        if (sb.charAt(sb.length()-1) != '/' && !path.startsWith("/")) {
            sb.append('/');
        }
        sb.append(path);
        return sb.toString();
    }

    /**
     * Pushes a POST request for processing.
     *
     * @param url    resource url
     * @param entity entity object to be posted
     * @return unique request id
     */
    public abstract <T> RequestFuture post(Uri url, T entity);

    /**
     * Pushes a GET request for processing.
     *
     * @param url resource url
     * @return unique request id
     */
    public abstract RequestFuture get(Uri url);

    /**
     * Pushes a DELETE request for processing.
     *
     * @param url resource url
     * @return unique request id
     */
    public abstract RequestFuture delete(Uri url);

    /**
     * Create new instance of {@link RestfulClient} and attaches it to the given {@link Context}. When client object is
     * no longer needed, {@link #detach()} must be called for clean-up and releasing all retained resources.
     *
     * @param context
     * @return
     * @see #detach()
     */
    public static RestfulClient getInstance(Context context) {
        final RestfulClient client = new RestfulClientImpl();
        client.init(context);
        return client;
    }

    /**
     * Associate {@link RequestCallbackFactory} with the client. This factory will be used to automatically create
     * request callbacks.
     *
     * @param requestCallbackFactory
     */
    public void setRequestCallbackFactory(RequestCallbackFactory requestCallbackFactory) {
        this.requestCallbackFactory = requestCallbackFactory;
    }

    /**
     * Return associated {@link RequestCallbackFactory}
     *
     * @return {@link RequestCallbackFactory} that was previously set by
     *         {@link #setRequestCallbackFactory(RequestCallbackFactory)}, or {@code null} if it was not.
     * @see #setRequestCallbackFactory(getrest.android.client.RequestCallbackFactory)
     */
    protected RequestCallbackFactory getRequestCallbackFactory() {
        return requestCallbackFactory;
    }

    /**
     * Set {@link Handler} in which request callbacks will be executed. Default behaviour is the callback of thread in
     * which client has been created.
     *
     * @param callbackHandler {@link Handler} instance to be used to execute callbacks
     */
    public abstract void setCallbackHandler(Handler callbackHandler);

    /**
     * Start the client and replays all unfinished requests.
     */
    public abstract void replay();

    /**
     * Start the client.
     */
    public abstract void start();

    public abstract RequestFuture getRequestFuture(String requestId);

    public interface RequestBuilder {

        RequestBuilder uri(Uri uri);

        RequestBuilder method(Method method);

        RequestBuilder header(String name, String value);

        <T> RequestBuilder entity(T entity);

        RequestFuture execute();

    }
}
