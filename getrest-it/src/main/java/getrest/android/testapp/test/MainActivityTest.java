/**
 * Copyright
 */
package getrest.android.testapp.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;

import android.widget.Button;

import getrest.android.testapp.MainActivity;
import getrest.android.testapp.R;

/**
 * 
 * @author aha
 *
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

    public void testPostRequest() throws Exception {

        final MainActivity activity = getActivity();
        final Button button = (Button) activity.findViewById(R.id.Main_GetSimOperatorButton);
        TouchUtils.clickView(this, button);
    }
}
