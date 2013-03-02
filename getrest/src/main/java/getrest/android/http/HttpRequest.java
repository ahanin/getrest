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

import getrest.android.core.AbstractRequest;
import getrest.android.core.Error;
import getrest.android.core.Request;

/**
 * 
 * @author aha
 *
 * @since 2012-01-13
 */
public class HttpRequest extends AbstractRequest implements Request {

    private Method method;
    private Object entity;
    private Class entityType;
    private Class returnType;
    private long nanoTime;
    private Error error;

    public Method getMethod() {
        return method;
    }

    public void setMethod(final Method method) {
        this.method = method;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(final Object entity) {
        this.entity = entity;
    }

    public Class getEntityType() {
        return entityType;
    }

    public void setEntityType(final Class entityType) {
        this.entityType = entityType;
    }

    public Class getReturnType() {
        return returnType;
    }

    public void setReturnType(final Class returnType) {
        this.returnType = returnType;
    }

    public long getNanoTime() {
        return nanoTime;
    }

    public void setNanoTime(final long nanoTime) {
        this.nanoTime = nanoTime;
    }

    public Error getError() {
        return error;
    }

    public void setError(final Error error) {
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }

    public Object execute() {
        throw new UnsupportedOperationException();
    }
}
