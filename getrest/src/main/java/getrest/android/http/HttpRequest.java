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
package getrest.android.http;

import android.net.Uri;

import android.os.Parcel;
import android.os.Parcelable;

import getrest.android.core.Error;
import getrest.android.core.ErrorState;
import getrest.android.core.Pack;
import getrest.android.core.Request;

/**
 * 
 * @author aha
 *
 * @since 2012-01-13
 */
public class HttpRequest extends BaseRequest implements Parcelable, Request {

    private Pack entity;
    private Class entityType;
    private Class returnType;
    private long nanoTime;
    private Error error;

    public Pack getEntity() {

        return entity;
    }

    public void setEntity(final Pack entity) {
        this.entity = entity;
    }

    public Class getEntityType() {

        return entityType;
    }

    public void setEntityType(final Class entityType) {
        this.entityType = entityType;
    }

    public Class getReturnType() {

        return returnType;
    }

    public void setReturnType(final Class returnType) {
        this.returnType = returnType;
    }

    public long getNanoTime() {

        return nanoTime;
    }

    public void setNanoTime(final long nanoTime) {
        this.nanoTime = nanoTime;
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
        parcel.writeSerializable(entityType);
        parcel.writeSerializable(returnType);
        parcel.writeParcelable(getUri(), 0);
        parcel.writeInt(getMethod().getId());
        parcel.writeString(getMediaType().toString());
        parcel.writeParcelable(entity, 0);
        parcel.writeLong(nanoTime);
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

            final HttpRequest request = new HttpRequest();
            request.setRequestId(parcel.readString());
            request.setEntityType((Class) parcel.readSerializable());
            request.setReturnType((Class) parcel.readSerializable());
            request.setUri((Uri) parcel.readParcelable(Uri.class.getClassLoader()));
            request.setMethod(Method.byId(parcel.readByte()));
            request.setMediaType(new MediaType(parcel.readString()));
            request.entity = parcel.readParcelable(Pack.class.getClassLoader());
            request.nanoTime = parcel.readLong();
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
