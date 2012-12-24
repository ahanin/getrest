/**
 * Copyright
 */
package getrest.android.client.impl;

import android.net.Uri;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import getrest.android.client.RequestRegistry;
import getrest.android.core.Request;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class RequestRegistryEntryFactoryTest {

    @Test
    public void testShouldCreateEntry() throws Exception {
        final RequestRegistryEntryFactory entryFactory = new RequestRegistryEntryFactory();
        final Request request = mock(Request.class);

        when(request.getRequestId()).thenReturn("12345");

        final RequestRegistry.Entry entry = entryFactory.create(request);

        assertThat(entry.getRequestId(), equalTo("12345"));
    }

}
