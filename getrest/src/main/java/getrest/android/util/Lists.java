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

import java.lang.annotation.Annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Lists {
    public static <T> List<T> newArrayList() {

        return new ArrayList<T>();
    }

    public static <T> List<T> newArrayList(final T... elements) {

        return new ArrayList<T>(Arrays.asList(elements));
    }

    public static <T> List<T> immutableList(final T... elements) {

        return Collections.unmodifiableList(newArrayList(elements));
    }

    public static <T> List<T> immutableList(final List<T> list) {

        return Collections.unmodifiableList(new ArrayList<T>(list));
    }

    public static <T> List<T> emptyList() {

        return Collections.emptyList();
    }

    public static <T> List<T> combine(final List<T>... chunks) {

        int size = 0;

        for (final List<T> chunk : chunks) {
            size += chunk.size();
        }

        final List<T> result = new ArrayList<T>(size);

        for (final List<T> chunk : chunks) {
            result.addAll(chunk);
        }

        return result;
    }
}
