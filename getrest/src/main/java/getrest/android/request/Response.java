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

package getrest.android.request;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import getrest.android.entity.Pack;

/**
 * @author aha
 * @since 2012-01-13
 */
public class Response implements Parcelable {

    private Uri uri;
    private Pack entity;
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
        parcel.writeInt(status.getResponseCode());
    }

    public static final Creator<Response> CREATOR = new Creator<Response>() {
        public Response createFromParcel(final Parcel parcel) {
            final Response response = new Response();
            response.uri = parcel.readParcelable(Uri.class.getClassLoader());
            response.entity = parcel.readParcelable(Pack.class.getClassLoader());
            response.status = Status.forResponseCode(parcel.readInt());
            return response;
        }

        public Response[] newArray(final int size) {
            return new Response[size];
        }
    };

    public void setStatus(final Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

}
