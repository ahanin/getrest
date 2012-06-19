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

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author aha
 * @since 2012-01-13
 */
public class Request implements HasHeaders, Parcelable {

    private String requestId;
    private Uri uri;
    private Method method;
    private Pack entity;
    private Headers headers = new Headers();
    private long timestamp;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(final Uri uri) {
        this.uri = uri;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(final Method method) {
        this.method = method;
    }

    public Pack getEntity() {
        return entity;
    }

    public void setEntity(final Pack entity) {
        this.entity = entity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void addHeader(Header header) {
        this.headers.add(header);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeString(requestId);
        parcel.writeParcelable(uri, 0);
        parcel.writeInt(method.getId());
        parcel.writeParcelable(entity, 0);
        parcel.writeParcelable(headers, 0);
        parcel.writeLong(timestamp);
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        public Request createFromParcel(final Parcel parcel) {
            final Request request = new Request();
            request.requestId = parcel.readString();
            request.uri = parcel.readParcelable(Uri.class.getClassLoader());
            request.method = Method.byId(parcel.readByte());
            request.entity = parcel.readParcelable(Pack.class.getClassLoader());
            request.headers = parcel.readParcelable(Headers.class.getClassLoader());
            request.timestamp = parcel.readLong();
            return request;
        }

        public Request[] newArray(final int i) {
            return new Request[0];
        }
    };

}
