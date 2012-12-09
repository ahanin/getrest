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

import android.content.Context;
import android.net.Uri;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import getrest.android.Getrest;
import getrest.android.core.MediaType;
import getrest.android.core.Method;
import getrest.android.core.Request;
import getrest.android.ext.MessageBodyReader;
import getrest.android.ext.MessageBodyWriter;
import getrest.android.request.RequestContext;
import getrest.android.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@RunWith(RobolectricTestRunner.class)
public class GenericApplicationTest {

    private Context app;
    private ResourceMethod postBookMethod;
    private ResourceMethod getBookByIsbnMethod;
    private ResourceMethod deleteBookByIsbnMethod;
    private ResourceMethod allNewsSubpathMethod;
    private GenericApplication genericApplication;

    private Request createRequest(final String uri, final Method method) {
        final Request request = new Request();

        request.setMethod(method);
        request.setUri(Uri.parse(uri));

        return request;
    }

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

        deleteBookByIsbnMethod = new ResourceMethod("/{isbn}", Sets.<MediaType>emptySet(),
                Sets.newHashSet(Method.DELETE));

        final Resource bookResource = new Resource("/book", Sets.<MediaType>emptySet(),
                Sets.newHashSet(postBookMethod, getBookByIsbnMethod, deleteBookByIsbnMethod));
        application.addResource(bookResource);

        allNewsSubpathMethod = new ResourceMethod("/*", Sets.<MediaType>emptySet(),
                Sets.newHashSet(Method.GET));

        final Resource newsResource = new Resource("/news", Sets.<MediaType>emptySet(),
                Sets.newHashSet(allNewsSubpathMethod));
        application.addResource(newsResource);

        config.addApplication(application.build());

        app = mock(Context.class,
                withSettings().extraInterfaces(HasConfig.class));

        when(((HasConfig) app).getGetrestConfig()).thenReturn(config.build());

        when(app.getApplicationContext()).thenReturn(app);

        genericApplication = new GenericApplication(Sets.<MessageBodyWriter>emptySet(), Sets.<MessageBodyReader>emptySet(),
                Sets.newHashSet(bookResource, newsResource));
    }

    @Test
    public void testShouldMatchMethodByEquality() throws Exception {
        assertEquals(postBookMethod, genericApplication.getResourceMethodResolver().getResourceMethod(createRequest("/book", Method.POST)));
    }

    @Test
    public void testShouldMatchGetMethodByUriParam() throws Exception {
        assertEquals(getBookByIsbnMethod, genericApplication.getResourceMethod(createRequest("/book/17283", Method.GET)));
    }

    @Test
    public void testShouldMatchDeleteMethodByUriParam() throws Exception {
        assertEquals(deleteBookByIsbnMethod, genericApplication.getResourceMethod(createRequest("/book/17283", Method.DELETE)));
    }

    @Test
    public void testShouldMatchMethodByWildcard() throws Exception {
        assertEquals(allNewsSubpathMethod, genericApplication.getResourceMethod(createRequest("/news/2012-11-18/all-the-jelly-beans-you-can-eat",
                Method.GET)));
    }


}
