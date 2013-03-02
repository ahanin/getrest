/*
 * Copyright 2013 Alexey Hanin
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

import android.net.Uri;

import getrest.android.persistence.Storage;
import getrest.android.persistence.util.RequestStorageSupport;

import getrest.android.service.RequestFutureSupport;
import getrest.android.service.RequestTuple;

import getrest.android.util.Provider;

import java.text.MessageFormat;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class RequestManagerImpl implements RequestManager {

    private final Provider<List<Storage>> storageListProvider;
    private final Map<Request, RequestFutureSupport> requestFutureSupportMap = new WeakHashMap<Request, RequestFutureSupport>();

    public RequestManagerImpl(final Provider<List<Storage>> storageListProvider) {
        this.storageListProvider = storageListProvider;
    }

    public void persistRequest(final String requestId, final Request request) {

        final Uri requestUri = RequestStorageSupport.getRequestUri(requestId);
        final Storage storage = requireValidStorage(request, requestUri);

        storage.persist(requestUri, request);
    }

    private Storage requireValidStorage(final Object object, final Uri requestUri) {
        for (final Storage storage : storageListProvider.get()) {
            if (storage.supports(requestUri, object.getClass())) {
                return storage;
            }
        }

        throw new IllegalStateException(
            MessageFormat.format("Unable to find corresponding storage for: uri={0}, dataType={1}",
                                 requestUri,
                                 object.getClass()));
    }

    public Request loadRequest(final String requestId) {

        final Uri requestUri = RequestStorageSupport.getRequestUri(requestId);

        for (final Storage storage : storageListProvider.get()) {
            if (storage.contains(requestUri)) {
                return storage.load(requestUri);
            }
        }

        return null;
    }

    public void persistResponse(final String requestId, final Object response) {

        final Uri uri = RequestStorageSupport.getResponseUri(requestId);
        final Storage storage = requireValidStorage(response, uri);

        storage.persist(uri, response);
    }

    public Object loadResponse(final String requestId) {

        // TODO finish implementation
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void updateRequestStatus(final String requestId, final RequestStatus status) {

        // TODO finish implementation
        throw new UnsupportedOperationException("not yet implemented");
    }

    public void updateRequestStatus(final String requestId, final ErrorState errorState,
                                    final String message) {

        // TODO finish implementation
        throw new UnsupportedOperationException("not yet implemented");
    }

    public RequestStatus getRequestStatus(final String requestId) {
        return null;
    }

    public <R extends Request<V>, V> RequestFutureSupport<R, V> getRequestFutureSupport(final RequestTuple<R> requestTuple) {
        synchronized (requestFutureSupportMap) {
            if (requestFutureSupportMap.containsKey(requestTuple.getRequest())) {
                return requestFutureSupportMap.get(requestTuple.getRequest());
            }

            final RequestFutureSupport<R, V> requestFutureSupport = new RequestFutureSupport<R, V>();
            requestFutureSupport.setRequestTuple(requestTuple);

            requestFutureSupportMap.put(requestTuple.getRequest(), requestFutureSupport);

            return requestFutureSupport;
        }
    }
}
