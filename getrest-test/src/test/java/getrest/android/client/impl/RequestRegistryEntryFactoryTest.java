/**
 * Copyright
 */
package getrest.android.client.impl;

import com.xtremelabs.robolectric.RobolectricTestRunner;

import getrest.android.client.RequestRegistry;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import org.junit.runner.RunWith;
@RunWith(RobolectricTestRunner.class)
public class RequestRegistryEntryFactoryTest {
    @Test
    public void testShouldCreateEntry() throws Exception {

        final RequestRegistryEntryFactory entryFactory = new RequestRegistryEntryFactory();

        final RequestRegistry.Entry entry = entryFactory.create("12345");

        assertThat(entry.getRequestId(), equalTo("12345"));
    }
}
