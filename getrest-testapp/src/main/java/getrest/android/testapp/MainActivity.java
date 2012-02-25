/**
 * Copyright
 */
package getrest.android.testapp;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import getrest.android.RestfulClient;
import getrest.android.client.RequestCallback;
import getrest.android.client.RequestFuture;
import getrest.android.request.Request;
import getrest.android.request.Response;

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

                final RequestFuture future = restfulClient.post(Uri.parse("http://10.0.2.2:8080/note"), values);
                future.setRequestCallback(new RequestCallback() {
                    public void onPending(final Request request) {
                        Toast.makeText(MainActivity.this, "Pending...", Toast.LENGTH_SHORT).show();
                    }

                    public void onExecuting(final Request request) {
                        Toast.makeText(MainActivity.this, "Executing...", Toast.LENGTH_SHORT).show();
                    }

                    public void onFinished(final Response request) {
                        Toast.makeText(MainActivity.this, "Finished!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        // TODO use new Handler() instance by default (move to RestfulClient)
        restfulClient.setCallbackHandler(new Handler());
        restfulClient.replay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        restfulClient.detach();
    }
}
