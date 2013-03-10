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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Headers implements Iterable<Header> {
    private List<Header> headers = new LinkedList<Header>();
    private Map<String,List<Header>> headerMap = new HashMap<String,List<Header>>();

    public Headers() {
    }

    /**
     * Copy constructor.
     *
     * @param headers a base for copying
     */
    public Headers(final Headers headers) {
        for (Header header : headers) {
            add(new Header(header.getName(), header.getValue()));
        }
    }

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

        return (headers == null) ? null : Collections.unmodifiableList(headers);
    }

    public Iterator<Header> iterator() {
        return headers.iterator();
    }

    public int count() {
        return headers.size();
    }

    public void addAll(final Collection<Header> headers) {
        this.headers.addAll(headers);
    }

}
