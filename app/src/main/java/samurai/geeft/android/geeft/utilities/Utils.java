package samurai.geeft.android.geeft.utilities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.Rest;
import com.baasbox.android.json.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import samurai.geeft.android.geeft.activities.LoginActivity;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by danybr-dev on 01/03/16.
 */
public class Utils {
    private static final String TAG = "Utils";

    public void startLoginActivity(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    public static void fillGeeftFromDocument(Geeft geeft, BaasDocument documentFetched){
        if(documentFetched != null) {
            Log.d(TAG,"documentFetched id:" + documentFetched.getId() + " and author:" + documentFetched.getAuthor());
            geeft.setId(documentFetched.getId());
            if (documentFetched.getString("username") != null) {
                geeft.setUsername(documentFetched.getString("username"));
                Log.d(TAG, documentFetched.getString("username"));
            }
            geeft.setFullname(documentFetched.getString("name"));
            geeft.setBaasboxUsername(documentFetched.getString("baasboxUsername"));
            geeft.setGeeftImage(documentFetched.getString("image") + BaasUser.current().getToken());
            //Append ad image url your session token!
            geeft.setGeeftDescription(documentFetched.getString("description"));
            geeft.setUserProfilePic(documentFetched.getString("profilePic"));
            geeft.setCreationTime(getCreationTimestamp(documentFetched));
            geeft.setDeadLine(documentFetched.getLong("deadline"));
            geeft.setUserFbId(documentFetched.getString("userFbId"));

            geeft.setAutomaticSelection(documentFetched.getBoolean("automaticSelection"));
            geeft.setAllowCommunication(documentFetched.getBoolean("allowCommunication"));

            geeft.setUserLocation(documentFetched.getString("location"));
            geeft.setUserCap(documentFetched.getString("cap"));
            geeft.setGeeftTitle(documentFetched.getString("title"));
            geeft.setDimensionRead(documentFetched.getBoolean("allowDimension"));
            geeft.setGeeftHeight(documentFetched.getInt("height"));
            geeft.setGeeftWidth(documentFetched.getInt("width"));
            geeft.setGeeftDepth(documentFetched.getInt("depth"));
            geeft.setDonatedLinkId(documentFetched.getString("donatedLinkId"));
            geeft.setAssigned(documentFetched.getBoolean("assigned"));
            geeft.setTaken(documentFetched.getBoolean("taken"));
            geeft.setGiven(documentFetched.getBoolean("given"));
            geeft.setIsFeedbackLeftByGeefter(documentFetched.getBoolean(TagsValue.FLAG_IS_FEEDBACK_LEFT_BY_GEEFTER));
            geeft.setIsFeedbackLeftByGeefted(documentFetched.getBoolean(TagsValue.FLAG_IS_FEEDBACK_LEFT_BY_GEEFTED));
        }
        else{
            Log.e(TAG,"Error! DocumentFetched is null!");
        }
    }

    private static long getCreationTimestamp(BaasDocument d){ //return timestamp of _creation_date of document
        String date = d.getCreationDate();
        //Log.d(TAG,"_creation_date is:" + date);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date creation_date = dateFormat.parse(date);
            return creation_date.getTime(); //Convert timestamp in string
        }catch (java.text.ParseException e){
            Log.e(TAG,"ERRORE FATALE : " + e.toString());
        }
        return -1;

    }

    public static void sendAlertPush(final String receiverUsername,String message){

        BaasBox.rest().async(Rest.Method.GET, "plugin/push.send?receiverName=" + receiverUsername
                + "&message=" + message.replace(" ","%20"), new BaasHandler<JsonObject>() {
            @Override
            public void handle(BaasResult<JsonObject> baasResult) {
                if (baasResult.isSuccess()) {
                    Log.d(TAG, "Push notification sended to: " + receiverUsername);
                } else {
                    Log.e(TAG, "Error while sending push notification:" + baasResult.error());
                }
            }
        });
    }
}
