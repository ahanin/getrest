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
package getrest.android.util;

import java.io.IOException;
import java.io.Serializable;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Sets {
    public static <E> HashSet<E> newHashSet() {
        return new HashSet<E>();
    }

    public static <E> HashSet<E> newHashSet(final E... elements) {
        return new HashSet<E>(Arrays.asList(elements));
    }

    public static <E> Set<E> immutableSet(final Set<E> set) {
        return Collections.unmodifiableSet(set);
    }

    public static <E> Set<E> immutableSet(final E... elements) {
        return Collections.unmodifiableSet(new HashSet<E>(Arrays.asList(elements)));
    }

    public static <E> Set<E> emptySet() {
        return Collections.emptySet();
    }

    public static <E> Set<E> newSetFromMap(final Map<E, Boolean> map) {
        return new SetFromMap<E>(map);
    }

    /**
     * Backported from JDK in order to support {@code setFromMap} in Android SDK levels
     * prior 9.
     *
     * @param <E>
     *
     * @see Collections.SetFromMap
     */
    private static class SetFromMap<E> extends AbstractSet<E> implements Set<E> {

        private final Map<E, Boolean> m; // The backing map
        private transient Set<E> s; // Its keySet

        SetFromMap(final Map<E, Boolean> map) {
            if (!map.isEmpty()) {
                throw new IllegalArgumentException("Map is non-empty");
            }

            m = map;
            s = map.keySet();
        }

        public void clear() {
            m.clear();
        }

        public int size() {
            return m.size();
        }

        public boolean isEmpty() {
            return m.isEmpty();
        }

        public boolean contains(final Object o) {
            return m.containsKey(o);
        }

        public boolean remove(final Object o) {
            return m.remove(o) != null;
        }

        public boolean add(final E e) {
            return m.put(e, Boolean.TRUE) == null;
        }

        public Iterator<E> iterator() {
            return s.iterator();
        }

        public Object[] toArray() {
            return s.toArray();
        }

        public <T> T[] toArray(final T[] a) {
            return s.toArray(a);
        }

        public String toString() {
            return s.toString();
        }

        public int hashCode() {
            return s.hashCode();
        }

        public boolean equals(final Object o) {
            return o == this || s.equals(o);
        }

        public boolean containsAll(final Collection<?> c) {
            return s.containsAll(c);
        }

        public boolean removeAll(final Collection<?> c) {
            return s.removeAll(c);
        }

        public boolean retainAll(final Collection<?> c) {
            return s.retainAll(c);
        }
    }
}
