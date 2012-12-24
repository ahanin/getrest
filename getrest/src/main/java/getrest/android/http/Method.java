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

import java.util.HashMap;
import java.util.Map;

/**
 * @author aha
 * @since 2012-01-13
 */
public enum Method {

    GET    ((byte) 1),
    POST   ((byte) 2),
    PUT    ((byte) 3),
    DELETE ((byte) 4);

    private byte id;

    private static Map<Byte, Method> INDEX = new HashMap<Byte, Method>();

    static {
        for (Method method : INDEX.values()) {
            INDEX.put(method.getId(), method);
        }
    }

    private Method(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    public static Method byId(final byte id) {
        return INDEX.get(id);
    }

}
