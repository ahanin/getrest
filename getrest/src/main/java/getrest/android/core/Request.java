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
    private Error error;

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
    public Error getError() {
        return error;
    }

    public void setError(final Error error) {
        this.error = error;
    }

    public boolean hasError() {
        return error != null;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeString(getRequestId());
        parcel.writeParcelable(getUri(), 0);
        parcel.writeInt(getMethod().getId());
        parcel.writeParcelable(entity, 0);
        parcel.writeLong(timestamp);
        writeErrorToParcel(parcel, error);
        final Headers headers = getHeaders();
        HeadersHelper.writeToParcel(parcel, headers);
    }

    private static void writeErrorToParcel(final Parcel parcel, final Error error) {
        if (error == null) {
            parcel.writeString(null);
            parcel.writeString(null);
        } else {
            parcel.writeString(error.getErrorState().name());
            parcel.writeString(error.getMessage());
        }
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
            public Request createFromParcel(final Parcel parcel) {
                final Request request = new Request();
                request.setRequestId(parcel.readString());
                request.setUri((Uri) parcel.readParcelable(
                        Uri.class.getClassLoader()));
                request.setMethod(Method.byId(parcel.readByte()));
                request.entity = parcel.readParcelable(Pack.class.getClassLoader());
                request.timestamp = parcel.readLong();
                request.error = readErrorFromParcel(parcel);

                HeadersHelper.readFromParcel(parcel, request.getHeaders());

                return request;
            }

            public Request[] newArray(final int i) {
                return new Request[0];
            }
        };

    private static Error readErrorFromParcel(final Parcel parcel) {
        final String errorState = parcel.readString();
        final String message = parcel.readString();

        if (errorState == null) {
            return null;
        }

        final Error error = new Error();
        error.setErrorState(ErrorState.valueOf(errorState));
        error.setMessage(message);

        return error;
    }

}
