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

import java.util.Collections;
import java.util.Map;

public class MediaType {

    public static final MediaType ANY = new MediaType("*/*");

    private final String type = "*";
    private final String subtype = "*";
    private final Map<String,String> params = Collections.emptyMap();

    public MediaType(final String mediaType) {
        // TODO parse media type
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public Map<String,String> getParams() {
        return params;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MediaType mediaType = (MediaType) o;

        if (params != null ? !params.equals(mediaType.params) : mediaType.params != null) return false;
        if (subtype != null ? !subtype.equals(mediaType.subtype) : mediaType.subtype != null) return false;
        if (type != null ? !type.equals(mediaType.type) : mediaType.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (subtype != null ? subtype.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder().append(type).append("/")
                                                    .append(subtype);

        if (params.isEmpty()) {
            for (Map.Entry<String,String> entry : params.entrySet()) {
                sb.append("; ").append(entry.getKey()).append("=").append('"')
                  .append(entry.getValue()).append('"');
            }
        }

        return sb.toString();
    }
}
