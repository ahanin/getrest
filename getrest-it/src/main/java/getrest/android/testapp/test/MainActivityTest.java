/**
 * Copyright
 */
package getrest.android.testapp.test;

import android.test.ActivityInstrumentationTestCase2;
import getrest.android.testapp.MainActivity;

/**
 * @author aha
 * @since 2012-01-16
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super("getrest.android.testapp", MainActivity.class);
    }

    public void testMainActivity() throws Exception {
        final MainActivity activity = getActivity();
        assertNotNull(activity);
    }
}
