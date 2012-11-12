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
import getrest.android.client.impl.RestfulClientImpl;
import getrest.android.core.Method;
import getrest.android.core.Response;
import getrest.android.util.TypeLiteral;

/**
 * @author aha
 * @since 2012-01-13
 */
public abstract class RestfulClient {

    private String base;

    private RequestCallbackFactory requestCallbackFactory;

    public String getBase() {
        return base;
    }

    public void setBase(final String baseUrl) {
        this.base = baseUrl;
    }

    protected abstract void init(Context context);

    /**
     * Detach {@link RestfulClient} and release all retained resources.
     */
    public abstract void detach();

    /**
     * Create request with uri.
     *
     * @param uri
     * @return
     */
    public abstract <R> RequestAutomate<R> newRequest(String uri);

    private Uri buildUri(final String path) {
        return this.base == null ? Uri.parse(path) : Uri.parse(gluePath(base, path));
    }

    private String gluePath(final String baseUrl, final String path) {
        final StringBuilder sb = new StringBuilder(baseUrl);
        if (sb.charAt(sb.length() - 1) != '/' && !path.startsWith("/")) {
            sb.append('/');
        }
        sb.append(path);
        return sb.toString();
    }

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

    public static RestfulClient getInstance(Context context, Object applicationId) {
        final RestfulClient client = new RestfulClientImpl(applicationId);
        final String applicationBase = "/"; // TODO retrieve preconfigured application's base uri
        client.setBase(applicationBase);
        client.init(context);
        return client;
    }

    public static RestfulClient getInstance(Context context, String base, Object applicationId) {
        final RestfulClient client = new RestfulClientImpl(applicationId);
        client.setBase(base);
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

    public abstract Response getResponse(String requestId);

    public interface RequestAutomate<R> {

        RequestAutomate<R> withUri(Uri uri);

        RequestAutomate<R> withMethod(Method method);

        RequestAutomate<R> withHeader(String name, String value);

        <T> RequestAutomate<R> withEntity(T entity);

        <T> RequestAutomate<T> withResponseType(Class<T> responseType);

        <T> RequestAutomate<T> withResponseType(TypeLiteral<T> typeLiteral);

        R execute();

    }
}
