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

import java.util.HashMap;
import java.util.Map;

public class Status {

    private int responseCode;
    private String name;

    private static final Map<Integer, Status> CACHE = new HashMap<Integer, Status>();

    public static final Status UNEXPECTED_EXCEPTION = Status.create(-1, "Unexpected exception");

    private static Status create(final int statusCode, final String message) {
        if (CACHE.containsKey(statusCode)) {
            throw new IllegalStateException("Status with code '" + statusCode + "' is already defined.");
        }
        final Status status = new Status(statusCode, message);
        CACHE.put(statusCode, status);
        return status;
    }

    private Status(final int responseCode, final String name) {
        this.responseCode = responseCode;
        this.name = name;
    }

    public static Status forResponseCode(final int statusCode) {
        if (!CACHE.containsKey(statusCode)) {
            synchronized (CACHE) {
                if (!CACHE.containsKey(statusCode)) {
                    return create(statusCode, "STATUS: " + statusCode);
                }
            }
        }
        return CACHE.get(statusCode);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Status status = (Status) o;

        if (responseCode != status.responseCode) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return responseCode;
    }
}
