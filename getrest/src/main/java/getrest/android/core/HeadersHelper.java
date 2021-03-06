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


public class HeadersHelper {
    static void writeToParcel(final Parcel parcel, final Headers headers) {
        parcel.writeInt(headers.count());

        if (headers.count() > 0) {
            for (Header header : headers) {
                parcel.writeString(header.getName());
                parcel.writeString(header.getValue());
            }
        }
    }

    static void readFromParcel(final Parcel parcel, final Headers headers) {
        final int headerCount = parcel.readInt();

        for (int i = 0; i < headerCount; i++) {
            headers.add(new Header(parcel.readString(), parcel.readString()));
        }
    }
}
