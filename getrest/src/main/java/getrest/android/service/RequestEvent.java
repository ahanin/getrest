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
package getrest.android.service;

public class RequestEvent {

    public static final RequestEvent PENDING = new RequestEvent((byte) 1, "PENDING");
    public static final RequestEvent EXECUTING = new RequestEvent((byte) 2, "EXECUTING");
    public static final RequestEvent FINISHED = new RequestEvent((byte) 3, "FINISHED");

    private byte id;
    private String name;

    private RequestEvent(final byte id, final String name) {
        this.name = name;
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static RequestEvent byId(final byte id) {
        switch (id) {
            case 1:
                return PENDING;
            case 2:
                return EXECUTING;
            case 3:
                return FINISHED;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return "RequestEvent{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final RequestEvent that = (RequestEvent) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}
