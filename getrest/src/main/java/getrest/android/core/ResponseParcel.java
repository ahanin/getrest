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
import getrest.android.http.Header;
import getrest.android.http.Headers;
import getrest.android.http.HeadersHelper;
import getrest.android.http.HasHeaders;
import getrest.android.http.Pack;

/**
 * @author aha
 * @since 2012-01-13
 */
public class ResponseParcel implements Parcelable, HasHeaders {

    private Uri uri;
    private Pack entity;
    private Headers headers = new Headers();
    private Status status;

    public Uri getUri() {
        return uri;
    }

    public void setUri(final Uri uri) {
        this.uri = uri;
    }

    public Pack getEntity() {
        return entity;
    }

    public void setEntity(final Pack entity) {
        this.entity = entity;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel parcel, final int flags) {
        parcel.writeParcelable(uri, 0);
        parcel.writeParcelable(entity, 0);
        HeadersHelper.writeToParcel(parcel, headers);
        parcel.writeInt(status.getResponseCode());
    }

    public static final Creator<ResponseParcel> CREATOR = new Creator<ResponseParcel>() {
        public ResponseParcel createFromParcel(final Parcel parcel) {
            final ResponseParcel responseParcel = new ResponseParcel();
            responseParcel.uri = parcel.readParcelable(Uri.class.getClassLoader());
            responseParcel.entity = parcel.readParcelable(Pack.class.getClassLoader());
            HeadersHelper.readFromParcel(parcel, responseParcel.headers);
            responseParcel.status = Status.forResponseCode(parcel.readInt());
            return responseParcel;
        }

        public ResponseParcel[] newArray(final int size) {
            return new ResponseParcel[size];
        }
    };

    public void setStatus(final Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void addHeader(final Header header) {
        headers.add(header);
    }

}
