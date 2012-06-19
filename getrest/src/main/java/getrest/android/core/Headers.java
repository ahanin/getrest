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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Headers implements Iterable<Header>, Parcelable {

    private List<Header> headers = new LinkedList<Header>();

    private Map<String, List<Header>> headerMap = new HashMap<String, List<Header>>();

    public void add(final Header header) {
        headers.add(header);
        requireHeaderBucket(header.getName()).add(header);
    }

    private List<Header> requireHeaderBucket(final String header) {
        final List<Header> bucket;
        final String key = header.toLowerCase();
        if (headerMap.containsKey(key)) {
            bucket = headerMap.get(key);
        } else {
            bucket = new LinkedList<Header>();
            headerMap.put(key, bucket);
        }
        return bucket;
    }

    public List<Header> get(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Header name cannot be null");
        }
        final List<Header> headers = headerMap.get(name.trim().toLowerCase());
        return headers == null ? null : Collections.unmodifiableList(headers);
    }

    public Iterator<Header> iterator() {
        return headers.iterator();
    }

    public int count() {
        return headers.size();
    }

    public int describeContents() {
        return 0;
    }

    public static final Creator<Headers> CREATOR = new Creator<Headers>() {
        public Headers createFromParcel(final Parcel parcel) {
            final Headers headers = new Headers();

            final int count = parcel.readInt();
            for (int i = 0; i < count; i++) {
                headers.add(new Header(parcel.readString(), parcel.readString()));
            }

            return headers;
        }

        public Headers[] newArray(final int size) {
            return new Headers[size];
        }
    };

    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(headers.size());
        if (!headers.isEmpty()) {
            for (Header header : headers) {
                dest.writeString(header.getName());
                dest.writeString(header.getValue());
            }
        }
    }
}
