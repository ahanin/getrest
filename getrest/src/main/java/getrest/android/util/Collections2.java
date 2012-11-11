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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Collections2 {

    public static <E> Collection<E> filter(Iterable<E> collection, Predicate<E> predicate) {
        return filter(collection, predicate, Collections2.<E>arrayListProvider());
    }

    public static <E, C extends Collection<E>> C filter(final Iterable<E> collection, final Predicate<E> predicate,
                                                         final Provider<C> provider) {
        final C filtered = provider.get();

        for (E e : collection) {
            if (predicate.accept(e)) {
                filtered.add(e);
            }
        }

        return filtered;
    }

    public static <T> Provider<Set<T>> hashSetProvider() {
        return new HashSetProvider<T>();
    }

    public static <T> Provider<List<T>> arrayListProvider() {
        return new ArrayListProvider<T>();
    }

    private static class HashSetProvider<T> implements Provider<Set<T>> {
        @SuppressWarnings("unchecked")
        private static final Provider<Set<?>> INSTANCE = new HashSetProvider();

        public Set<T> get() {
            return new HashSet<T>();
        }
    }

    private static class ArrayListProvider<T> implements Provider<List<T>> {
        @SuppressWarnings("unchecked")
        private static final Provider<Set<?>> INSTANCE = new ArrayListProvider();

        public List<T> get() {
            return new ArrayList<T>();
        }
    }
}
