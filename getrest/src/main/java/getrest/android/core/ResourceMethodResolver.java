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

import getrest.android.config.ResourceMethod;

/**
 * {@link ResourceMethodResolver} is used to search within the {@link Application} a {@link ResourceMethod} that is best
 * matching a request.
 */
public interface ResourceMethodResolver {
    /**
     * Find a resource method matching the request. The result must be consistent. That is, same request must result
     * in the same resource method object.
     *
     * @param request
     * @return {@link ResourceMethod} matched the request, or {@code null} if request does not match any methods defined
     */
    ResourceMethod getResourceMethod(final Request request);
}
