package oditek.com.hlw.Service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MyInstanceIDListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        oditek.com.hlw.webservices.ApiClient.saveDataWithKeyAndValue(getApplicationContext(), "token", token);
        System.out.println("TokenId===="+token);
    }
}