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

import getrest.android.core.MediaType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Sets {
    public static <E> HashSet<E>newHashSet() {
        return new HashSet<E>();
    }

    public static <E> HashSet<E>newHashSet(final E...elements) {
        return new HashSet<E>(Arrays.asList(elements));
    }

    public static <E> Set<E>immutableSet(final Set<E> set) {
        return Collections.unmodifiableSet(set);
    }

    public static <E> Set<E>immutableSet(final E...elements) {
        return Collections.unmodifiableSet(new HashSet<E>(Arrays.asList(
                    elements)));
    }

    public static Set<MediaType> emptySet() {
        return Collections.emptySet();
    }
}
