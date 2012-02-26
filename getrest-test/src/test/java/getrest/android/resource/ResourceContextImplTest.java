/**
 * Copyright
 */
package getrest.android.resource;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import getrest.android.entity.Marshaller;
import getrest.android.entity.Packer;
import getrest.android.request.Request;
import getrest.android.request.RequestContext;
import getrest.android.request.RequestController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;

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
        final RequestController requestController = mock(RequestController.class);

        resourceContext.setMarshaller(marshaller);
        resourceContext.setPacker(packer);
        resourceContext.setRequestController(requestController);

        final Request request = mock(Request.class);
        final RequestContext requestContext = resourceContext.getRequestContext(request);
        
        assertThat(requestContext.getMarshaller(), sameInstance(marshaller));
        assertThat(requestContext.getPacker(), sameInstance(packer));
        assertThat(requestContext.getRequestController(), sameInstance(requestController));
        assertThat((ResourceContextImpl) requestContext.getResourceContext(), sameInstance(resourceContext));
    }

}
