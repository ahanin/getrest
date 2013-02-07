/**
 * Copyright
 */
package getrest.android.testapp;

import android.app.Activity;

import android.content.ContentValues;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import getrest.android.GetrestClient;

import getrest.android.client.RequestCallback;

import getrest.android.core.Request;
import getrest.android.core.RequestFuture;

import getrest.android.http.HttpRequest;
import getrest.android.http.HttpRequestBuilder;
import getrest.android.http.Method;

/**
 * 
 * @author aha
 *
 * @since 2012-01-16
 */
public class MainActivity extends Activity {

    private GetrestClient getrestClient;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getrestClient = GetrestClient.attach(this);

        setContentView(R.layout.main);

        final Button postRequestButton = (Button) findViewById(R.id.Main_CreatePostRequestButton);

        postRequestButton.setOnClickListener(
            new View.OnClickListener() {
                    public void onClick(final View view) {

                        final ContentValues note = new ContentValues();
                        note.put("subject", "Groceries");
                        note.put("text", "Potatos, tomatos");

                        final HttpRequest httpRequest = HttpRequestBuilder.newHttpRequestBuilder(
                            MainActivity.this).withMethod(Method.POST).withEntity(note).build();

                        getrestClient.execute(httpRequest).setRequestCallback(
                            new RequestCallback() {
                                    public void onPending(final Request request) {
                                        Toast.makeText(MainActivity.this,
                                                       "Pending...",
                                                       Toast.LENGTH_SHORT).show();
                                    }

                                    public void onExecuting(final Request request) {
                                        Toast.makeText(MainActivity.this,
                                                       "Executing...",
                                                       Toast.LENGTH_SHORT).show();
                                    }

                                    public void onError(final Request request) {
                                        Toast.makeText(MainActivity.this,
                                                       "Error :(",
                                                       Toast.LENGTH_SHORT).show();
                                    }

                                    public void onCompleted(final Request request) {
                                        Toast.makeText(MainActivity.this,
                                                       "Completed! :)",
                                                       Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
        getrestClient.replay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getrestClient.detach();
    }
}
