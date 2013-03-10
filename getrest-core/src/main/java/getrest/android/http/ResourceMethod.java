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
package getrest.android.http;

import getrest.android.util.Sets;

import java.util.Set;

public class ResourceMethod extends ResourceEndPoint {

    private final Set<Method> methods;
    private Class entityType;
    private Class resultType;

    public ResourceMethod(final String path,
                          final Set<MediaType> consumableMediaTypes,
                          final Set<Method> methods) {
        super(path, consumableMediaTypes);
        this.methods = Sets.immutableSet(methods);
    }

    public ResourceMethod setEntityType(Class entityType) {
        this.entityType = entityType;
        return this;
    }

    public Class getEntityType() {
        return entityType;
    }

    public ResourceMethod setResultType(final Class resultType) {
        this.resultType = resultType;
        return this;
    }

    public Class getResultType() {
        return resultType;
    }

    public Set<Method> getMethods() {
        return methods;
    }
}
