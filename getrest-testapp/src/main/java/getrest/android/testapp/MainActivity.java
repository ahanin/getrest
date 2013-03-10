/**
 * Copyright
 */
package getrest.android.testapp;

import android.app.Activity;

import android.os.Bundle;

import android.telephony.TelephonyManager;

import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import getrest.android.GetrestClient;

import getrest.android.core.AbstractRequest;
import getrest.android.core.Request;
import getrest.android.core.RequestCallbackAdapter;

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

        final Button getSimOperatorButton = (Button) findViewById(R.id.Main_GetSimOperatorButton);

        getSimOperatorButton.setOnClickListener(
            new View.OnClickListener() {
                    public void onClick(final View view) {

                        final Request<String> request = new AbstractRequest<String>() {
                            public String execute() {

                                final TelephonyManager telephonyManager = (TelephonyManager) getSystemService(
                                    TELEPHONY_SERVICE);
                                return telephonyManager.getSimOperatorName();
                            }
                        };

                        getrestClient.execute(request).setRequestCallback(
                            new RequestCallbackAdapter<Request<String>, String>() {
                                    public void onError(final Request request) {
                                        Toast.makeText(MainActivity.this,
                                                       "Failed to read your phone's state :(",
                                                       Toast.LENGTH_SHORT).show();
                                    }

                                    public void onCompleted(final Request request,
                                                            final String response) {
                                        Toast.makeText(MainActivity.this,
                                                       "Your SIM operator name is " + response,
                                                       Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getrestClient.detach();
    }
}
