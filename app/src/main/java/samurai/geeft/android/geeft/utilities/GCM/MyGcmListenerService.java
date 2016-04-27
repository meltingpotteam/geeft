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

package samurai.geeft.android.geeft.utilities.GCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONObject;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.MainActivity;
import samurai.geeft.android.geeft.activities.WinnerScreenActivity;

public class MyGcmListenerService extends GcmListenerService {

    private final String TAG = getClass().getSimpleName();
    private int key;
    private String geeftId;
    private String docUserId;
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
        Log.d(TAG,data.toString());
        String message = data.getString("message");
        String custom = data.getString("custom");
        Log.d(TAG, custom);
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        /**
         * "custom": [key,"geeftId" ,"docUserId"]
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
        parseCostum(custom);
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
            case 3:  intent = showImportantMessage(message);
                break;
            case 4: intent = contactFromUserCase();
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
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setLights(getResources().getColor(R.color.colorPrimary),2000,2000);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private Intent showImportantMessage(String message) {
        Intent intent = MainActivity.newIntent(getApplicationContext(),true,message);
        return intent;
    }


    private Intent assignedCase() {//Case where you are geefted and Geeft is assigned to you
        /*Intent intent = AssignedActivity
                .newIntent(getApplicationContext(), TagsValue.LINK_NAME_ASSIGNED, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
        if(!(geeftId == null && docUserId == null)) {
            Intent intent = WinnerScreenActivity.newIntent(getApplicationContext(), 1, geeftId, docUserId);
            return intent;
        }
        else{
            Intent intent = MainActivity.newIntent(getApplicationContext());
            Log.e(TAG, "An error occurred,DEBUG: geeftId is: " + geeftId + " and docUserId is: " + docUserId);
            return intent;
        }

    }

    private Intent donatedCase() { //Case where you are geefter and your Geeft is assigned
        /*Intent intent = DonatedActivity.newIntent(getApplicationContext(), TagsValue.LINK_NAME_DONATED, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
        if(!(geeftId == null && docUserId == null)) {
            Intent intent = WinnerScreenActivity.newIntent(getApplicationContext(), 2, geeftId, docUserId);
            return intent;
        }
        else{
            Intent intent = MainActivity.newIntent(getApplicationContext());
            Log.e(TAG, "An error occurred,DEBUG: geeftId is: " + geeftId + " and docUserId is: " + docUserId);
            return intent;
        }

    }

    private Intent contactFromUserCase() { //TODO: Replace this with new activity
        if(!(geeftId== null && docUserId == null)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            return intent;
        }
        else{
            Intent intent = MainActivity.newIntent(getApplicationContext());
            Log.e(TAG, "An error occurred,DEBUG: geeftId is: " + geeftId + " and docUserId is: " + docUserId);
            return intent;
        }
    }

    private Intent defaultCase() {
        if(!(geeftId== null && docUserId == null)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            return intent;
        }
        else{
            Intent intent = MainActivity.newIntent(getApplicationContext());
            Log.e(TAG,"An error occurred,DEBUG: geeftId is: " + geeftId + " and docUserId is: " + docUserId);
            return intent;
        }
    }

    private void parseCostum(String custom) {
        try {
            JSONObject obj = new JSONObject(custom);
            JSONArray array = obj.getJSONArray("custom");
            key = array.getInt(0);
            geeftId = array.getString(1);
            docUserId = array.getString(2);

            Log.d(TAG, "key: " + key);
            Log.d(TAG, "geeftId:" + geeftId);
            Log.d(TAG, "doc_id: "+docUserId);
            if(docUserId.equals(""))
                docUserId = null;
            //} catch (org.json.JSONException t) {
        } catch (Exception t) {
            Log.e(TAG, "Could not parse malformed JSON: \"" + custom + "\"");
            //startMainActivity();
        }
    }
    /*private void showAlertDialog() {
        new AlertDialog.Builder(getApplicationContext())
                .setTitle("Errore")
                .setMessage("E' accaduto un errore non previsto.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startMainActivity();
                    }
                }).show();
    }*/
    private void startMainActivity(){
        Intent intent = MainActivity.newIntent(getApplicationContext());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}