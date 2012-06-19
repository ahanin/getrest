/**
 * Copyright
 */
package getrest.android.resource;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import getrest.android.core.Request;
import getrest.android.request.RequestContext;
import getrest.android.request.RequestManager;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
public class ResourceContextImplTest {

    @Test
    public void testShouldCreateRequestContext() throws Exception {
        final ResourceContextImpl resourceContext = new ResourceContextImpl();

        final Marshaller marshaller = mock(Marshaller.class);
        final Packer packer = mock(Packer.class);
        final RequestManager requestManager = mock(RequestManager.class);

        resourceContext.setMarshaller(marshaller);
        resourceContext.setPacker(packer);
        resourceContext.setRequestManager(requestManager);

        final Request request = mock(Request.class);
        final RequestContext requestContext = resourceContext.getRequestContext(request);
        
        assertThat(requestContext.getMarshaller(), sameInstance(marshaller));
        assertThat(requestContext.getPacker(), sameInstance(packer));
        assertThat(requestContext.getRequestManager(), sameInstance(requestManager));
        assertThat((ResourceContextImpl) requestContext.getResourceContext(), sameInstance(resourceContext));
    }

}
