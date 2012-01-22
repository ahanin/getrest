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

import getrest.android.request.Response;

public interface RequestFuture {

    boolean isFinished();

    void setRequestCallback(RequestCallback callback);

    /**
     * Return {@link Response}. Calling thread is blocked until the request is finished with whatever result.
     *
     * @return {@link Response}
     */
    Response get();

}
