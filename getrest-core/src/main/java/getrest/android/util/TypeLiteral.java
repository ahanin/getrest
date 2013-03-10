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

    private final Class<T> aClass;

    @SuppressWarnings("unchecked")
    public TypeLiteral() {
        this.aClass = (Class<T>) getClass();
    }

    private TypeLiteral(final Class<T> aClass) {
        this.aClass = aClass;
    }

    public final Type getType() {

        if (aClass != this.getClass()) {

            return this.aClass;
        }

        final Type genericSuperclass = aClass.getGenericSuperclass();

        if (!(genericSuperclass instanceof ParameterizedType)) {

            return genericSuperclass;
        }

        final ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;

        return (parameterizedType.getActualTypeArguments().length > 0)
               ? parameterizedType.getActualTypeArguments()[0] : Object.class;
    }

    public static <T> TypeLiteral<T> fromClass(final Class<T> aClass) {

        return new TypeLiteral<T>(aClass);
    }
}
