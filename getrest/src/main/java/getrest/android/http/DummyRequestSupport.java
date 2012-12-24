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

import android.os.Parcelable;
import getrest.android.client.RequestExecutor;
import getrest.android.core.ParcelableRequestParcel;
import getrest.android.core.Request;
import getrest.android.core.RequestParcel;
import getrest.android.core.RequestSupport;
import getrest.android.util.Preconditions;

public class DummyRequestSupport<T extends Request> implements RequestSupport<T> {

    private T request;

    public DummyRequestSupport(final T request) {
        this.request = request;
    }

    public RequestParcel<T> getRequestParcel() {
        Preconditions.checkState(request instanceof Parcelable, "Request must be Parcelable");
        return new ParcelableRequestParcel<T>(request);
    }

    public RequestExecutor getRequestExecutor() {
        throw new UnsupportedOperationException();
    }

    public <E> E getContext(final Class<E> type) {
        throw new UnsupportedOperationException();
    }

    public <R> R createResponse(final Class<R> responseType) {
        throw new UnsupportedOperationException();
    }
}
