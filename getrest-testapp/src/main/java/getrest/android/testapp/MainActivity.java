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
import android.widget.Toast;
import getrest.android.RestfulClient;
import getrest.android.client.RequestCallback;
import getrest.android.client.Response;
import getrest.android.core.Method;
import getrest.android.core.Request;
import getrest.android.core.ResponseParcelable;

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

                final Response future = restfulClient.request(Uri.parse("http://10.0.2.2:8080/note"))
                        .method(Method.POST)
                        .entity(values)
                        .execute();

                future.setRequestCallback(new RequestCallback() {
                    public void onPending(final Request request) {
                        Toast.makeText(MainActivity.this, "Pending...", Toast.LENGTH_SHORT).show();
                    }

                    public void onExecuting(final Request request) {
                        Toast.makeText(MainActivity.this, "Executing...", Toast.LENGTH_SHORT).show();
                    }

                    public void onError(final Request request) {
                        Toast.makeText(MainActivity.this, "Error :(\n" + request.getError(), Toast.LENGTH_SHORT).show();
                    }

                    public void onFinished(final ResponseParcelable request) {
                        Toast.makeText(MainActivity.this, "Finished :)", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        restfulClient.replay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        restfulClient.detach();
    }
}
