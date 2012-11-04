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

package getrest.android.client;

import getrest.android.core.Request;
import getrest.android.core.ResponseParcelable;

import java.util.concurrent.Future;

public abstract class Response<T> extends javax.ws.rs.core.Response {

    public abstract String getRequestId();

    /**
     * Set {@link RequestCallback} that will receive {@link Request} event notifications.
     *
     * @param requestCallback instance of {@link RequestCallback} to receive notifications
     */
    public abstract void setRequestCallback(RequestCallback requestCallback);

    public abstract boolean isFinished();

    /**
     * Return {@link getrest.android.core.ResponseParcelable}. Calling thread will be blocked until the request is finished with any result.
     *
     * @return {@link getrest.android.core.ResponseParcelable}
     */
    public abstract T getEntity();

}
