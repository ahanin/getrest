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

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import getrest.android.client.RequestCallbackFactory;
import getrest.android.client.RequestFuture;
import getrest.android.client.impl.RestfulClientImpl;

/**
 * @author aha
 * @since 2012-01-13
 */
public abstract class RestfulClient {

    private RequestCallbackFactory requestCallbackFactory;

    protected abstract void init(Context context);

    /**
     * Detach {@link RestfulClient} and release all retained resources.
     */
    public abstract void detach();

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
     * @see #saveStateAndDetach(Bundle)
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
     * Save current client state to {@link Bundle} and detach client from associated context. This method must be called
     * on {@link Activity#onSaveInstanceState(Bundle)} callback.
     *
     * @param outState activity state {@link Bundle} to which client's state should be written
     */
    public abstract void saveStateAndDetach(final Bundle outState);

    /**
     * @param savedInstanceState
     */
    public abstract void restoreState(final Bundle savedInstanceState);

}
