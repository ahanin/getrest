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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Sets {
    public static <T> HashSet<T>newHashSet() {
        return new HashSet<T>();
    }

    public static <T> Set<T>immutableSet(final Set<T> set) {
        return Collections.unmodifiableSet(set);
    }
}
