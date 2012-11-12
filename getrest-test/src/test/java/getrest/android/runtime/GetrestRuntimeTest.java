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
package getrest.android.runtime;

import android.content.Context;

import android.net.Uri;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import getrest.android.Getrest;

import getrest.android.config.Config;
import getrest.android.config.HasConfig;
import getrest.android.config.Resource;
import getrest.android.config.ResourceMethod;

import getrest.android.core.MediaType;
import getrest.android.core.Method;
import getrest.android.core.Request;

import getrest.android.request.RequestContext;
import getrest.android.util.Sets;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
@RunWith(RobolectricTestRunner.class)
public class GetrestRuntimeTest {

    private Context app;

    private ResourceMethod postBookMethod;

    @Before
    public void setUp() throws Exception {
        final Config.ConfigBuilder config = Getrest.newConfigBuilder();
        final Config.ApplicationBuilder application = Getrest.newApplicationBuilder();

        postBookMethod = new ResourceMethod("/create",
                Sets.newHashSet(new MediaType("text/xml")),
                Sets.newHashSet(Method.POST));

        application.addResource(new Resource("/book", Sets.emptySet(),
                Sets.newHashSet(postBookMethod)));
        config.addApplication(application.build());

        app = mock(Context.class,
                withSettings().extraInterfaces(HasConfig.class));

        when(((HasConfig) app).getGetrestConfig()).thenReturn(config.build());

        when(app.getApplicationContext()).thenReturn(app);
    }

    @Test
    public void testShouldCreateRequestContext() throws Exception {
        final Request request = new Request();

        request.setMethod(Method.POST);
        request.setUri(Uri.parse("/book/create"));

        final RequestContext requestContext = GetrestRuntime.getInstance(app).getRequestContext(request);

        assertEquals(postBookMethod, requestContext.getResourceMethod());
    }

}
