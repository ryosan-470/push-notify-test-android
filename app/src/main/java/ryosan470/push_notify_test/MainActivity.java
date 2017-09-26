package ryosan470.push_notify_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.growthbeat.Growthbeat;
import com.growthbeat.model.Client;
import com.growthpush.GrowthPush;
import com.growthpush.model.Environment;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "GrowthPush_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String GROWTH_PUSH_APPLICATION_ID = getString(R.string.GrowthPush_APPLICATION_ID);
        final String GROWTH_PUSH_CREDENTIAL_ID = getString(R.string.GrowthPush_CREDENTIAL_ID);
        final String FIREBASE_SENDER_ID = getString(R.string.Firebase_SENDER_ID);

        GrowthPush.getInstance().initialize(getApplicationContext(), GROWTH_PUSH_APPLICATION_ID, GROWTH_PUSH_CREDENTIAL_ID, Environment.production);
        GrowthPush.getInstance().requestRegistrationId(FIREBASE_SENDER_ID);
        GrowthPush.getInstance().trackEvent("Launch"); // 起動時をイベントとして登録する

        new Thread(new Runnable() {
            @Override
            public void run() {
                Client client = Growthbeat.getInstance().waitClient();
                Log.d(TAG, String.format("clientId is %s", client.getId()));
            }
        }).start();

        Button getDeviceTokenButton = (Button) findViewById(R.id.get_device_token_button);
        getDeviceTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String token = GrowthPush.getInstance().registerGCM(getApplicationContext());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textView = (TextView) findViewById(R.id.device_token_view);
                                textView.setText(token);
                            }
                        });
                    }
                }).start();
            }
        });
    }
}
