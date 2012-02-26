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
package getrest.android.config;

import android.net.Uri;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ConfigTest {

    @Test
    public void testShouldReturnCachedResourceContext() throws Exception {
        final Config config = new Config();
        config.configure("http://twitter.com");
        assertThat(config.getResourceContext(Uri.parse("http://twitter.com")),
                sameInstance(config.getResourceContext(Uri.parse("http://twitter.com"))));
    }

}
