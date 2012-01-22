/**
 * Copyright
 */
package getrest.android.testapp;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import getrest.android.RestfulClient;

/**
 * @author aha
 * @since 2012-01-16
 */
public class MainActivity extends Activity {

    private RestfulClient restfulClient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        restfulClient = RestfulClient.getInstance(this);

        setContentView(R.layout.main);

        final Button postRequestButton = (Button) findViewById(R.id.Main_CreatePostRequestButton);
        postRequestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                final ContentValues values = new ContentValues();
                values.put("title", "Groceries");
                values.put("note", "Tomatoes\nMeat\nFish\n");
                restfulClient.post(Uri.parse("http://10.0.2.2:8080/note"), values);
            }
        });
    }

    @Override
    protected void onDestroy() {
        restfulClient.detach();
        super.onDestroy();
    }
}
