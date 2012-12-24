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
package getrest.android.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import getrest.android.http.Header;
import getrest.android.http.Headers;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
public class HeadersTest {
    private Headers headers;

    @Before
    public void setUp() throws Exception {
        headers = new Headers();
    }

    @Test
    public void testHeaderShouldBeNullInitially() throws Exception {
        assertNull(headers.get("Accept-Language"));
    }

    @Test
    public void testShouldAddHeader() throws Exception {
        headers.add(new Header("Accept-Encoding", "gzip"));
        assertThat(headers.get("Accept-Encoding"),
            equalTo(Arrays.asList(new Header("Accept-Encoding", "gzip"))));
    }

    @Test
    public void testShouldAddMultipleHeaders() throws Exception {
        headers.add(new Header("Accept-Encoding", "gzip"));
        headers.add(new Header("Accept-Encoding", "zip"));
        assertThat(headers.get("Accept-Encoding"),
            equalTo(Arrays.asList(new Header("Accept-Encoding", "gzip"),
                    new Header("Accept-Encoding", "zip"))));
    }

    @Test
    public void testShouldBeIterable() throws Exception {
        final Header a = new Header("Accept-Encoding", "gzip");
        final Header b = new Header("Content-Type", "text/plain");

        headers.add(a);
        headers.add(b);

        final ArrayList<Header> iterated = new ArrayList<Header>(2);

        for (Header header : headers) {
            iterated.add(header);
        }

        assertThat(iterated, equalTo(Arrays.asList(a, b)));
    }
}
