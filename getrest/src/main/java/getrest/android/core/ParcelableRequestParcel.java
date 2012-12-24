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

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableRequestParcel<T extends Request> implements RequestParcel<T> {
    private T request;

    public ParcelableRequestParcel(final T request) {
        this.request = request;
    }

    public T getRequest() {
        return request;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(final Parcel parcel, final int flags) {
        parcel.writeParcelable((Parcelable) request, 0);
    }

    public static class CREATOR implements Parcelable.Creator<RequestParcel> {
        public RequestParcel createFromParcel(final Parcel parcel) {
            return new ParcelableRequestParcel((Request) parcel.readParcelable(ParcelableRequestParcel.class.getClassLoader()));
        }

        public RequestParcel[] newArray(final int size) {
            return new RequestParcel[size];
        }
    }
}
