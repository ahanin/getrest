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
public class Request extends BaseRequest implements Parcelable {
    private Pack entity;
    private long timestamp;

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

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeString(getRequestId());
        parcel.writeParcelable(getUri(), 0);
        parcel.writeInt(getMethod().getId());
        parcel.writeParcelable(entity, 0);

        final Headers headers = getHeaders();
        HeadersHelper.writeToParcel(parcel, headers);

        parcel.writeLong(timestamp);
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
            public Request createFromParcel(final Parcel parcel) {
                final Request request = new Request();
                request.setRequestId(parcel.readString());
                request.setUri((Uri) parcel.readParcelable(
                        Uri.class.getClassLoader()));
                request.setMethod(Method.byId(parcel.readByte()));
                request.entity = parcel.readParcelable(Pack.class.getClassLoader());

                HeadersHelper.readFromParcel(parcel, request.getHeaders());

                request.timestamp = parcel.readLong();

                return request;
            }

            public Request[] newArray(final int i) {
                return new Request[0];
            }
        };

}
