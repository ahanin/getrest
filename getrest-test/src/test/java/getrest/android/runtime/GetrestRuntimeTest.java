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
import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.HashSet;

@RunWith(RobolectricTestRunner.class)
public class GetrestRuntimeTest {
    private Context app;
    private ResourceMethod postBookMethod;
    private ResourceMethod getBookByIsbnMethod;
    private ResourceMethod deleteBookByIsbnMethod;

    @Before
    public void setUp() throws Exception {
        final Config.ConfigBuilder config = Getrest.newConfigBuilder();
        final Config.ApplicationBuilder application = Getrest.newApplicationBuilder();

        postBookMethod = new ResourceMethod("/",
                Sets.newHashSet(new MediaType("text/xml")),
                Sets.newHashSet(Method.POST));

        getBookByIsbnMethod = new ResourceMethod("/{isbn}",
                Sets.newHashSet(new MediaType("text/xml")),
                Sets.newHashSet(Method.GET));

        deleteBookByIsbnMethod = new ResourceMethod("/{isbn}",
                new HashSet<MediaType>(), Sets.newHashSet(Method.DELETE));

        application.addResource(new Resource("/book", Sets.emptySet(),
                Sets.newHashSet(postBookMethod, getBookByIsbnMethod,
                    deleteBookByIsbnMethod)));
        config.addApplication(application.build());

        app = mock(Context.class,
                withSettings().extraInterfaces(HasConfig.class));

        when(((HasConfig) app).getGetrestConfig()).thenReturn(config.build());

        when(app.getApplicationContext()).thenReturn(app);
    }

    @Test
    public void testShouldMatchMethodByEquality() throws Exception {
        final Request request = createRequest("/book", Method.POST);

        final RequestContext requestContext = GetrestRuntime.getInstance(app)
                                                            .getRequestContext(request);

        assertEquals(postBookMethod, requestContext.getResourceMethod());
    }

    @Test
    public void testShouldMatchGetMethodByUriParam() throws Exception {
        final Request request = createRequest("/book/17283", Method.GET);

        final RequestContext requestContext = GetrestRuntime.getInstance(app)
                                                            .getRequestContext(request);

        assertEquals(getBookByIsbnMethod, requestContext.getResourceMethod());
    }

    @Test
    public void testShouldMatchDeleteMethodByUriParam()
        throws Exception {
        final Request request = createRequest("/book/17283", Method.DELETE);
        final RequestContext requestContext = GetrestRuntime.getInstance(app)
                                                            .getRequestContext(request);

        assertEquals(deleteBookByIsbnMethod, requestContext.getResourceMethod());
    }

    private Request createRequest(final String uri, final Method method) {
        final Request request = new Request();

        request.setMethod(method);
        request.setUri(Uri.parse(uri));

        return request;
    }
}
