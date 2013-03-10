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
import getrest.android.core.RequestCallback;

/**
 * Used in {@link getrest.android.GetrestClient} to implicitly create {@link getrest.android.core.RequestCallback}s and attach them to corresponding
 * {@link getrest.android.core.Response}s.
 */
public interface RequestCallbackFactory<R extends Request<V>, V> {
/**
     * Create {@link getrest.android.core.RequestCallback}
     *
     * @param request {@link Request} for which callback should be created
     * @return instance of {@link getrest.android.core.RequestCallback}, that will be called for {@link Request} events, or {@code null},
     *         in which case no callback will be notified about request events
     */
    RequestCallback<R, V> createCallback(final R request);
}
