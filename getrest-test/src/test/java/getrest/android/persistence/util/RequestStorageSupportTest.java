/*
 * Copyright 2013 Alexey Hanin
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
package getrest.android.persistence.util;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import getrest.android.util.Lists;
import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import org.junit.runner.RunWith;

@RunWith(RobolectricTestRunner.class)
public class RequestStorageSupportTest {
    @Test
    public void testShouldMatchRequestUri() throws Exception {

        final RequestStorageSupport.UriMatch match = RequestStorageSupport.match(
            RequestStorageSupport.getRequestUri("123456"));

        assertEquals("Must match request URI", RequestStorageSupport.MATCH_REQUEST, match.getCode());
        assertEquals("Must extract request id", Lists.newArrayList("123456"), match.getParameters());
    }

    @Test
    public void testShouldMatchResponseUri() throws Exception {

        final RequestStorageSupport.UriMatch match = RequestStorageSupport.match(
            RequestStorageSupport.getResponseUri("123456"));

        assertEquals("Must match response URI", RequestStorageSupport.MATCH_RESPONSE,
                     match.getCode());
        assertEquals("Must extract request id", Lists.newArrayList("123456"), match.getParameters());
    }

    @Test
    public void testShouldMatchRequestStatusUri() throws Exception {

        final RequestStorageSupport.UriMatch match = RequestStorageSupport.match(
            RequestStorageSupport.getRequestStatusUri("123456"));

        assertEquals("Must match response URI",
                     RequestStorageSupport.MATCH_REQUEST_STATUS,
                     match.getCode());


        assertEquals("Must extract request id", Lists.newArrayList("123456"), match.getParameters());
    }
}
