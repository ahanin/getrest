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
import getrest.android.client.RequestFuture;
import getrest.android.client.impl.RestfulClientImpl;

/**
 * @author aha
 * @since 2012-01-13
 */
public abstract class RestfulClient {

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
     * @see #detach()
     */
    public static RestfulClient getInstance(Context context) {
        final RestfulClient client = new RestfulClientImpl();
        client.init(context);
        return client;
    }

}
