/**
 * Copyright
 */
package getrest.android.testapp;

import android.app.Activity;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import getrest.android.RestfulClient;

import getrest.android.client.RequestCallback;

import getrest.android.http.HttpRequest;
import getrest.android.http.HttpRequestBuilder;
import getrest.android.http.Method;
import getrest.android.core.Request;
import getrest.android.core.Response;
import getrest.android.core.ResponseParcelable;

import getrest.android.testapp.domain.Note;
import getrest.android.util.TypeLiteral;

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
                final Note note = new Note();

                note.setSubject("Groceries");
                note.setText("Tomatoes\nMeet\nFish\n");

                final HttpRequest httpRequest = HttpRequestBuilder.newHttpRequestBuilder()
                        .withMethod(Method.POST)
                        .withEntity(note)
                        .build();

                final RequestFuture future = restfulClient.execute(httpRequest, RequestFuture.class);

                future.setRequestCallback(new RequestCallback<HttpRequest>() {
                    public void onPending(final HttpRequest request) {
                        Toast.makeText(MainActivity.this, "Pending...",
                                Toast.LENGTH_SHORT).show();
                    }

                    public void onExecuting(final HttpRequest request) {
                        Toast.makeText(MainActivity.this,
                                "Executing...", Toast.LENGTH_SHORT).show();
                    }

                    public void onError(final HttpRequest request) {
                        Toast.makeText(MainActivity.this,
                                "Error in request:(\n" + request.getRequestId(),
                                Toast.LENGTH_SHORT).show();
                    }

                    public void onFinished(final HttpRequest request) {
                        Toast.makeText(MainActivity.this,
                                "Finished :)", Toast.LENGTH_SHORT).show();
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
