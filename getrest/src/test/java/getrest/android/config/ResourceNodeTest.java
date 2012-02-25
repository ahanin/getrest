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

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ResourceNodeTest {

    @Test
    public void testShouldSupportAsteriskWildcard() throws Exception {
        final ResourceNode node = new ResourceNode("http*");
        assertThat(node.matches("https"), equalTo(true));
    }

    @Test
    public void testShouldSupportQuestionMarkWildcard() throws Exception {
        final ResourceNode node = new ResourceNode("h??p");
        assertThat(node.matches("http"), equalTo(true));
    }

}
