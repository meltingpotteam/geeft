/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package samurai.geeft.android.geeft.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONObject;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.AssignedActivity;
import samurai.geeft.android.geeft.activities.DonatedActivity;
import samurai.geeft.android.geeft.activities.MainActivity;
import samurai.geeft.android.geeft.activities.WinnerScreenActivity;

public class MyGcmListenerService extends GcmListenerService {

    private final String TAG = getClass().getSimpleName();
    private int key;
    private String doc_id;
    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String custom = data.getString("custom");
        Log.d(TAG, custom);
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        /**
         * "custom": [num ,"doc_id"]
         */

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        parseCostum(custom, key, doc_id);
        sendNotification(message);
        // [END_EXCLUDE]
    }

    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent;
        switch(key){
            case 1:  intent = assignedCase();
                break;
            case 2:  intent = donatedCase();
                break;
            case 3: intent = contactFromUserCase();
                break;
            default:  intent = defaultCase();
                break;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Vibrate for 500 milliseconds

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.gift)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[] {1, 1, 1})
                .setContentTitle("Geeft")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setLights(getResources().getColor(R.color.colorPrimary),2000,2000);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private Intent contactFromUserCase() { //TODO: Replace this with new activity
        Intent intent = new Intent(getApplicationContext(),WinnerScreenActivity.class);
        return intent;
    }

    private Intent defaultCase() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        return intent;
    }

    private Intent donatedCase() { //Case where you are geefter and your Geeft is assigned
        Intent intent = DonatedActivity.newIntent(getApplicationContext(), TagsValue.LINK_NAME_DONATED, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    private Intent assignedCase() {//Case where you are geefted and Geeft is assigned to you
        Intent intent = AssignedActivity
                .newIntent(getApplicationContext(), TagsValue.LINK_NAME_ASSIGNED, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    private void parseCostum(String custom, int num, String doc_id) {
        try {
            JSONObject obj = new JSONObject(custom);
            JSONArray array = obj.getJSONArray("custom");
            key = array.getInt(0);
            doc_id = array.getString(1);
            Log.d(TAG, "key: "+key);
            Log.d(TAG, "doc_id: "+doc_id);
        } catch (org.json.JSONException t) {
            Log.e(TAG, "Could not parse malformed JSON: \"" + custom + "\"");
        }
    }
}
