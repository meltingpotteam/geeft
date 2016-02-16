package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasFile;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.Grant;
import com.baasbox.android.Role;
import com.baasbox.android.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by danybr-dev on 16/02/16.
 */
public class BaaSSendSuggestions extends AsyncTask<Void,Void,Boolean>{

    private final String TAG = getClass().getName();
    Context mContext;
    String mTitle;
    String mDescription;
    String mUserDisplayName;
    TaskCallbackBoolean mCallback;

    /**
     * Constructor to create an object Geeft to send to Baasbox TODO: add the field 'expiration time'
     **/
    public BaaSSendSuggestions(Context context,String userDisplayName, String title,String description,
                           TaskCallbackBoolean callback) {
        mContext = context;
        mTitle = title;
        mDescription = description;
        mUserDisplayName = userDisplayName;
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        if(BaasUser.current() !=null) {
            BaasDocument doc = new BaasDocument("suggestions");
            doc.put("userDisplayName", mUserDisplayName);
            doc.put("title", mTitle);
            doc.put("description", mDescription);
            BaasResult<BaasDocument> resDoc = doc.saveSync();
            if (resDoc.isSuccess()) {
                Log.d(TAG, "Doc saved with success");
                BaasResult<Void> resDocGrant = doc.grantAllSync(Grant.READ, Role.REGISTERED);
                if (resDocGrant.isSuccess()) {
                    Log.d(TAG, "Doc granted with success");
                    return true;
                } else {
                    Log.e(TAG, "Error with grant of doc");
                    return false;
                }
            } else {
                Log.e(TAG, "Error with doc");
                return false;
            }
        }
        else{
            Log.e(TAG,"Error user not logged in:");
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result);
    }

}


