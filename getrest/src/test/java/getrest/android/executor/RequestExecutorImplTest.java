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
package getrest.android.executor;

import getrest.android.request.Request;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;

public class RequestExecutorImplTest {

    private RequestExecutorImpl requestExecutor;
    private Request request;

    @Before
    public void setUp() throws Exception {
        requestExecutor = new RequestExecutorImpl();
        request = mock(Request.class);
        requestExecutor.setRequest(request);
    }

    @Test
    public void testMethodMustNotBeNull() throws Exception {
        try {
            requestExecutor.execute();
            fail("When request method is null, must raise: " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException ex) {
            // normal flow
        }
    }

}
