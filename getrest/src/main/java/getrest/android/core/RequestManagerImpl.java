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

import getrest.android.storage.Storage;

import getrest.android.util.Provider;

import java.text.MessageFormat;

import java.util.List;

public class RequestManagerImpl implements RequestManager {

    private final Provider<List<Storage>> storageListProvider;

    public RequestManagerImpl(final Provider<List<Storage>> storageListProvider) {
        this.storageListProvider = storageListProvider;
    }

    public void persistRequest(final String requestId, final Request request) {

        final Uri requestUri = getRequestUri(requestId);
        final Storage validStorage = requireValidStorage(request, requestUri);

        validStorage.persist(requestUri, request);
    }

    private Storage requireValidStorage(final Request request, final Uri requestUri) {

        for (final Storage storage : storageListProvider.get()) {

            if (storage.supports(requestUri, request.getClass())) {

                return storage;
            }
        }

        throw new IllegalStateException(
            MessageFormat.format("Unable to find corresponding storage for: uri={0}, dataType={1}",
                                 requestUri,
                                 request.getClass(),
                                 request));
    }

    private Uri getRequestUri(final String requestId) {

        return new Uri.Builder().scheme("getrest").authority("request").appendPath(requestId).build();
    }

    public Request loadRequest(final String requestId) {

        return null;
    }

    public void persistResponse(final String requestId, final Object response) {

        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object loadResponse(final String requestId) {

        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updateRequestStatus(final String requestId, final RequestStatus status) {

        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updateRequestStatus(final String requestId, final ErrorState errorState,
                                    final String message) {

        //To change body of implemented methods use File | Settings | File Templates.
    }

    public RequestStatus getRequestStatus(final String requestId) {
        Uri.withAppendedPath(getRequestUri(requestId), "status");

        return null;
    }
}
