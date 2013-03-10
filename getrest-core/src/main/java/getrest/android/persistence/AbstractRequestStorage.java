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
package getrest.android.persistence;

import android.net.Uri;

import getrest.android.core.Request;
import getrest.android.core.RequestStatus;

import getrest.android.persistence.util.RequestStorageSupport;

import getrest.android.util.Preconditions;

public abstract class AbstractRequestStorage<R extends Request<V>, V> implements Storage {
    public <T> boolean supports(final Uri uri, final Class<?extends T> dataType) {

        final RequestStorageSupport.UriMatch match = RequestStorageSupport.match(uri);
        switch (match.getCode()) {

            case RequestStorageSupport.MATCH_REQUEST:
                return Request.class.isAssignableFrom(dataType);

            case RequestStorageSupport.MATCH_REQUEST_STATUS:
                return RequestStatus.class == dataType;

            case RequestStorageSupport.MATCH_RESPONSE:
                return true;

            default:
                return false;
        }
    }

    public <T> void persist(final Uri uri, final T data) {

        final RequestStorageSupport.UriMatch match = RequestStorageSupport.match(uri);
        Preconditions.checkState(
            (match.getCode() == RequestStorageSupport.MATCH_REQUEST && data != null
                        && data instanceof Request)
            || (match.getCode() == RequestStorageSupport.MATCH_REQUEST_STATUS && data != null
               && data instanceof RequestStatus)
            || (match.getCode() == RequestStorageSupport.MATCH_RESPONSE),
            "Invalid URI or data type: uri={0}, data={1}",
            uri,
            data);

        switch (match.getCode()) {

            case RequestStorageSupport.MATCH_REQUEST:
                persistRequest(match.getParameters().get(0), (R) data);
                break;

            case RequestStorageSupport.MATCH_RESPONSE:
                persistResponse(match.getParameters().get(0), data);
                break;

            case RequestStorageSupport.MATCH_REQUEST_STATUS:
                persistRequestStatus(match.getParameters().get(0), (RequestStatus) data);
                break;
        }
    }

    public boolean contains(final Uri uri) {

        final RequestStorageSupport.UriMatch match = RequestStorageSupport.match(uri);
        Preconditions.checkState(match.getCode() != RequestStorageSupport.MATCH_NO_MATCH,
                                 "Invalid URI: {0}",
                                 uri);

        switch (match.getCode()) {

            case RequestStorageSupport.MATCH_REQUEST:
                return containsRequest(match.getParameters().get(0));

            case RequestStorageSupport.MATCH_RESPONSE:
                return containsResponse(match.getParameters().get(0));

            case RequestStorageSupport.MATCH_REQUEST_STATUS:
                return containsRequestStatus(match.getParameters().get(0));

            default:
                throw new IllegalStateException(
                    "Unknown match code: uri=" + uri + ", code=" + match.getCode());
        }
    }

    public <T> T load(final Uri uri) {

        final RequestStorageSupport.UriMatch match = RequestStorageSupport.match(uri);
        Preconditions.checkState(match.getCode() != RequestStorageSupport.MATCH_NO_MATCH,
                                 "Invalid URI: {0}",
                                 uri);

        switch (match.getCode()) {

            case RequestStorageSupport.MATCH_REQUEST:
                return (T) loadRequest(match.getParameters().get(0));

            case RequestStorageSupport.MATCH_RESPONSE:
                return (T) loadResponse(match.getParameters().get(0));

            case RequestStorageSupport.MATCH_REQUEST_STATUS:
                return (T) loadRequestStatus(match.getParameters().get(0));

            default:
                throw new IllegalStateException(
                    "Unknown match code: uri=" + uri + ", code=" + match.getCode());
        }
    }

    protected abstract void persistRequest(final String requestId, final R request);

    protected abstract void persistResponse(final String requestId, final Object response);

    protected abstract void persistRequestStatus(final String requestId,
                                                 final RequestStatus requestStatus);

    protected abstract boolean containsRequest(final String requestId);

    protected abstract boolean containsResponse(final String requestId);

    protected abstract boolean containsRequestStatus(final String requestStatus);

    protected abstract R loadRequest(final String requestId);

    protected abstract V loadResponse(final String requestId);

    protected abstract RequestStatus loadRequestStatus(final String requestId);
}
