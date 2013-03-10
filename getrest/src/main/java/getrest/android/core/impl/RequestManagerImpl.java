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
package getrest.android.core.impl;

import android.net.Uri;

import getrest.android.core.Request;
import getrest.android.core.RequestManager;
import getrest.android.core.RequestStatus;

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
        persistObject(RequestStorageSupport.getRequestUri(requestId), request);
    }

    private Storage requireValidStorage(final Object object, final Uri requestUri) {
        return requireValidStorage(requestUri, object.getClass());
    }

    private Storage requireValidStorage(final Uri requestUri, final Class<?> dataType) {
        for (final Storage storage : storageListProvider.get()) {
            if (storage.supports(requestUri, dataType)) {
                return storage;
            }
        }

        throw new IllegalStateException(
            MessageFormat.format("Unable to find corresponding storage for: uri={0}, dataType={1}",
                                 requestUri,
                                 dataType));
    }

    public Request loadRequest(final String requestId) {

        final Uri requestUri = RequestStorageSupport.getRequestUri(requestId);
        return loadObject(requestUri);
    }

    public void persistResponse(final String requestId, final Object response) {
        persistObject(RequestStorageSupport.getResponseUri(requestId), response);
    }

    public Object loadResponse(final String requestId) {
        return loadObject(RequestStorageSupport.getResponseUri(requestId));
    }

    private <T> T loadObject(final Uri responseUri) {
        for (final Storage storage : storageListProvider.get()) {
            if (storage.contains(responseUri)) {
                return storage.load(responseUri);
            }
        }

        throw new IllegalStateException(
            "Object was not found in any of the repositories: " + responseUri);
    }

    public void updateRequestStatus(final String requestId, final RequestStatus status) {
        persistObject(RequestStorageSupport.getRequestStatusUri(requestId), status);
    }

    private <T> void persistObject(final Uri uri, final T object) {

        final Storage storage = requireValidStorage(object, uri);
        storage.persist(uri, object);
    }

    public RequestStatus getRequestStatus(final String requestId) {

        final Uri uri = RequestStorageSupport.getRequestStatusUri(requestId);
        return requireValidStorage(RequestStatus.class, uri).load(uri);
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
