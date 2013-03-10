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

public abstract class Response<T> {
    public abstract String getRequestId();

    /**
     * Set {@link RequestCallback} that will receive {@link Request}
     * event notifications.
     *
     * @param requestCallback instance of {@link RequestCallback} to receive
     *        notifications
     */
    public abstract void setRequestCallback(final RequestCallback requestCallback);

    public abstract boolean isFinished();

    /**
     * Return response entity. Calling thread will be blocked until the request is finished
     * with any result.
     *
     * @return request response
     */
    public abstract T getEntity();
}
