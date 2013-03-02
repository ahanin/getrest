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
package getrest.android.persistence.util;

import android.content.UriMatcher;

import android.net.Uri;

import getrest.android.util.Lists;

import java.util.List;

public class RequestStorageSupport {

    public static final int MATCH_NO_MATCH = 0;
    public static final int MATCH_REQUEST = 1;
    public static final int MATCH_REQUEST_STATUS = 2;
    public static final int MATCH_RESPONSE = 3;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI("requests", "request/*", MATCH_REQUEST);
        uriMatcher.addURI("requests", "request/*/status", MATCH_REQUEST_STATUS);
        uriMatcher.addURI("requests", "request/*/response", MATCH_RESPONSE);
    }

    private RequestStorageSupport() {}

    public static class UriMatch {

        private int code;
        private List<String> parameters = Lists.emptyList();

        public UriMatch(final int code, final List<String> parameters) {
            this.code = code;
            this.parameters = Lists.immutableList(parameters);
        }

        public int getCode() {
            return code;
        }

        public List<String> getParameters() {
            return parameters;
        }
    }

    public static UriMatch match(final Uri uri) {

        final int matchCode = uriMatcher.match(uri);
        final List<String> parameters;
        final int code;
        switch (matchCode) {

            case MATCH_REQUEST:
            case MATCH_RESPONSE:
            case MATCH_REQUEST_STATUS:
                parameters = Lists.newArrayList(uri.getPathSegments().get(1));
                code = matchCode;
                break;

            default:
                parameters = Lists.emptyList();
                code = MATCH_NO_MATCH;
                break;
        }

        return new UriMatch(code, parameters);
    }

    public static Uri getRequestUri(final String requestId) {
        return new Uri.Builder().scheme("getrest").authority("requests").appendPath("request").appendPath(
            requestId).build();
    }

    public static Uri getResponseUri(final String requestId) {
        return Uri.withAppendedPath(getRequestUri(requestId), "response");
    }

    public static Uri getRequestStatusUri(final String requestId) {
        return Uri.withAppendedPath(getRequestUri(requestId), "status");
    }
}
