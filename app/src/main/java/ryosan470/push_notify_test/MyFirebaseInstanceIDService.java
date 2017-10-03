package ryosan470.push_notify_test;


import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.InvalidParameterException;
import com.amazonaws.services.sns.model.SetEndpointAttributesRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "AwsSnsInstanceIdService";
    private static final String APPLICATION_ARN = "arn:aws:sns:ap-northeast-1:610199206347:app/GCM/ryosan470.push_notify_test";
    private static final String ENDPOINT = "https://sns.ap-northeast-1.amazonaws.com";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        AmazonSNSClient client = new AmazonSNSClient(generateAWSCredentials());
        client.setEndpoint(ENDPOINT);

        String endpointArn = createEndpointArn(token, client);
        HashMap<String, String> attr = new HashMap<>();
        attr.put("Token", token);
        attr.put("Enabled", "true");
        SetEndpointAttributesRequest req = new SetEndpointAttributesRequest().withEndpointArn(endpointArn).withAttributes(attr);
        Log.d(TAG, "Connected to server " + ENDPOINT);
        client.setEndpointAttributes(req);
    }

    private String createEndpointArn(String token, AmazonSNSClient client) {
        String endpointArn = "";
        try {
            Log.d(TAG, "Creating platform endpoint with token " + token);
            CreatePlatformEndpointRequest req = new CreatePlatformEndpointRequest()
                    .withPlatformApplicationArn(APPLICATION_ARN)
                    .withToken(token);
            CreatePlatformEndpointResult res = client.createPlatformEndpoint(req);
            endpointArn = res.getEndpointArn();
        } catch (InvalidParameterException ipe) {

        }
        storeEndpointArn(endpointArn);
        return endpointArn;
    }
    private AWSCredentials generateAWSCredentials() {
        return new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return getString(R.string.AWS_ACCESS_KEY);
            }

            @Override
            public String getAWSSecretKey() {
                return getString(R.string.AWS_SECRET_KEY);
            }
        };
    }

    private void storeEndpointArn(String endpointArn) {
        // Save endpointArn to a shared preference to cache device token
    }

    private String getEndpointArn() {
        // Get endpointArn from a shared preference
        return "null";
    }
}
