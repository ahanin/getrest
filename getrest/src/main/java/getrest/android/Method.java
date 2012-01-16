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

/**
 * @author aha
 * @since 2012-01-13
 */
public final class Method {

    public static final Method GET = new Method((byte) 1);
    public static final Method POST = new Method((byte) 2);
    public static final Method PUT = new Method((byte) 3);
    public static final Method DELETE = new Method((byte) 4);

    private byte id;

    private Method(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    public static Method byId(final byte id) {
        switch (id) {
            case 1:
                return GET;
            case 2:
                return POST;
            case 3:
                return PUT;
            case 4:
                return DELETE;
            default:
                return null;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Method method = (Method) o;

        if (id != method.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
