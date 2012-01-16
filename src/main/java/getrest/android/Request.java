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
package getrest.android;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * @author aha
 * @since 2012-01-13
 */
public class Request implements Parcelable {

    private String requestId;
    private Uri url;
    private Method method;
    private Representation representation;
    private Date date;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    public Uri getUrl() {
        return url;
    }

    public void setUrl(final Uri url) {
        this.url = url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(final Method method) {
        this.method = method;
    }

    public Representation getRepresentation() {
        return representation;
    }

    public void setRepresentation(final Representation representation) {
        this.representation = representation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeString(requestId);
        parcel.writeParcelable(url, 0);
        parcel.writeInt(method.getId());
        parcel.writeParcelable(representation, 0);
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(final Parcel parcel) {
            final Request request = new Request();
            request.requestId = parcel.readString();
            request.url = parcel.readParcelable(Uri.class.getClassLoader());
            request.method = Method.byId(parcel.readByte());
            request.representation = parcel.readParcelable(Representation.class.getClassLoader());
            request.date = new Date(parcel.readLong());
            return request;
        }

        @Override
        public Request[] newArray(final int i) {
            return new Request[0];
        }
    };

}
