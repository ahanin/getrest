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

import android.net.Uri;

/**
 * @author aha
 * @since 2012-01-13
 */
public abstract class RestfulClient {

    /**
     * Pushes a POST request for processing.
     *
     * @param url    resource url
     * @param entity entity object to be posted
     * @param <T>    entity type
     * @return unique request id
     */
    public <T> String post(Uri url, T entity) {
        throw new UnsupportedOperationException();
    }

    /**
     * Pushes a POST request for processing.
     *
     * @param url resource url
     * @return unique request id
     */
    public String get(Uri url) {
        throw new UnsupportedOperationException();
    }

    /**
     * Pushes a DELETE request for processing.
     *
     * @param url resource url
     * @return unique request id
     */
    public String delete(Uri url) {
        throw new UnsupportedOperationException();
    }

}
