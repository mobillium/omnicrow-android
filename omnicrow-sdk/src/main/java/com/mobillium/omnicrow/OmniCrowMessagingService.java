package com.mobillium.omnicrow;

/**
 * Created by oguzhandongul on 31/01/2017.
 */

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Random;

public class OmniCrowMessagingService extends FirebaseMessagingService {

    private static final String TAG = "OmniCrowMsgService";

    public static String value;

    public static String url;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        try {
            Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (remoteMessage.getData()
                         .size() > 0) {
            Random r = new Random();
            int id = r.nextInt(Integer.MAX_VALUE) + 1;
            GenerateNotification.fromJsonPayload(this, false, id, new JSONObject(remoteMessage.getData()));

        }
    }

}