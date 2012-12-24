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
import android.os.Handler;
import getrest.android.client.RequestCallbackFactory;
import getrest.android.client.RequestExecutor;
import getrest.android.client.impl.RestfulClientImpl;

/**
 * @author aha
 * @since 2012-01-13
 */
public abstract class RestfulClient implements RequestExecutor {

    private Handler callbackHandler = new Handler();

    private RequestCallbackFactory requestCallbackFactory;

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

    protected abstract void init(Context context);

    /**
     * Detach {@link RestfulClient} and release all retained resources.
     */
    public abstract void detach();

    /**
     * Start the client and replays all unfinished requests.
     */
    public abstract void replay();

    /**
     * Start the client.
     */
    public abstract void start();

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

    public void setCallbackHandler(final Handler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    protected Handler getCallbackHandler() {
        return callbackHandler;
    }

}
