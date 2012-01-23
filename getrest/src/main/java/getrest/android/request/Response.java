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

import android.os.Parcel;
import android.os.Parcelable;
import getrest.android.entity.Pack;

/**
 * @author aha
 * @since 2012-01-13
 */
public class Response implements Parcelable {

    private Request request;
    private Pack entity;
    private boolean isFailed;
    private Status status;

    public Request getRequest() {
        return request;
    }

    public void setRequest(final Request request) {
        this.request = request;
    }

    public Pack getEntity() {
        return entity;
    }

    public void setEntity(final Pack entity) {
        this.entity = entity;
    }

    /**
     * Tells if communication error occurred during the request execution.
     *
     * @return
     */
    public boolean isFailed() {
        return isFailed;
    }

    /**
     * Set failure flag.
     *
     * @param isFailed flag that says if unexpected problem has occurred and prevented request from execution
     */
    public void setFailed(final boolean isFailed) {
        this.isFailed = isFailed;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel parcel, final int flags) {
        parcel.writeParcelable(request, 0);
        parcel.writeParcelable(entity, 0);
        parcel.writeString(Boolean.toString(isFailed));
    }

    public static final Creator<Response> CREATOR = new Creator<Response>() {
        public Response createFromParcel(final Parcel parcel) {
            final Response response = new Response();
            response.request = parcel.readParcelable(Request.class.getClassLoader());
            response.entity = parcel.readParcelable(Pack.class.getClassLoader());
            response.isFailed = Boolean.valueOf(parcel.readString());
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
