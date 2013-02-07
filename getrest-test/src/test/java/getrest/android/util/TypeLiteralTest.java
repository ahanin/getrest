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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.List;

public class TypeLiteralTest {
    @Test
    public void testShouldCreateInstanceFromClass() throws Exception {
        assertThat(TypeLiteral.fromClass(String.class).getType(), equalTo((Type) String.class));
    }

    @Test
    public void testShouldReturnTypeOfParameter() throws Exception {

        final Type type = new TypeLiteral<List<String>>() {}
        .getType();

        assertThat(type, instanceOf(ParameterizedType.class));
    }
}
