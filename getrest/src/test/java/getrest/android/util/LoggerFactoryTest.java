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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class LoggerFactoryTest {

    @Test
    public void testTagCanNotBeLongerThan23Chars() throws Exception {
        try {
            LoggerFactory.getLogger("aaaaaaaaaabbbbbbbbbbcccc");
            fail("If log tag is longer than 23 characters, must raise: " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException ex) {
            // expected behaviour
        }
    }

    @Test
    public void testTagCanBe23CharsOrShorter() throws Exception {
        final Logger logger = LoggerFactory.getLogger("aaaaaaaaaabbbbbbbbbbccc");
        assertThat(logger, not(nullValue()));
    }

}
