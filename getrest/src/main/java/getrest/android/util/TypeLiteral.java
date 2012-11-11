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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeLiteral<T> {
    public final Type getType() {
        final Type genericSuperclass = getClass()
                                           .getGenericSuperclass();

        if (!(genericSuperclass instanceof ParameterizedType)) {
            return Object.class;
        }

        final ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;

        return (parameterizedType.getActualTypeArguments().length > 0)
        ? parameterizedType.getActualTypeArguments()[0] : Object.class;
    }
}
