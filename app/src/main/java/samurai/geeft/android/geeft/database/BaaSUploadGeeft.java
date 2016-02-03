package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;

import com.baasbox.android.BaasACL;
import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasFile;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.Grant;
import com.baasbox.android.Role;
import com.baasbox.android.json.JsonObject;

import java.io.File;
import java.sql.Timestamp;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
/**
 * Created by danybr-dev on 31/01/16.
 */
public class BaaSUploadGeeft extends AsyncTask<Void,Void,Boolean> {

    private final String TAG = "BaaSUploadGeeft";
    Context mContext;
    String mTitle;
    String mDescription;
    String mLocation;
    String mExpTime;
    String mCategory;
    byte[] mImage;
    TaskCallbackBoolean mCallback;
    private BaasDocument mDocUser;

    /**
     * Constructor to create an object Geeft to send to Baasbox TODO: add the field 'expiration time'
     **/
    public BaaSUploadGeeft(Context context, String title, String description,String location, byte[] image, String expTime, String category, TaskCallbackBoolean callback) {
        mContext = context;
        mTitle = title;
        mDescription = description;
        mLocation = location;
        mImage = image;
        mExpTime = expTime;
        mCategory = category;

        mCallback = callback;
        Log.d(TAG, "Lanciato AsyncTask");
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        if(BaasUser.current() !=null) {
            BaasDocument doc = new BaasDocument("geeft");
            doc.put("title", mTitle);
            doc.put("description", mDescription);
            doc.put("location", mLocation);
            doc.put("close", "false");
            doc.put("name", getFacebookName());
            doc.put("profilePic", getProfilePicFacebook());
            String timestamp = "1455115679"; //timestamp fittizio che punta al 10 Febbraio,serve per la scadenza
            Log.d(TAG, "Timestamp is: " + timestamp);
            doc.put("deadline", timestamp);
            BaasFile image = new BaasFile();
            BaasResult<BaasFile> resImage = image.uploadSync(mImage);
            if (resImage.isSuccess()) {
                Log.d(TAG, "Image uploaded");
                BaasResult<Void> resGrant = image.grantAllSync(Grant.READ, Role.REGISTERED);
                if (resGrant.isSuccess()) {
                    Log.d(TAG, "Granted");
                    doc.put("image", getImageUrl(image));//TODO Now imageUrl is cutted,append in
                    // another class your session token
                    //TODO Retrieve the link at image and put in doc,then save the doc and return true
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
                } else {
                    Log.e(TAG, "Fatal error grant");
                    return false;
                }
            } else {
                Log.e(TAG, "Fatal error upload");
                return false;
            }
        }
        else{
            return false;
        }



    }

    public String getFacebookName(){// return display name of user's profile
        String FbName = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("name");
        Log.d(TAG, "FB_Name is: " + FbName);
        return FbName;
    }
    public String getProfilePicFacebook(){ // return link of user's profile picture
        JsonObject field = BaasUser.current().getScope(BaasUser.Scope.REGISTERED);
        String id = field.getObject("_social").getObject("facebook").getString("id");
        Log.d(TAG, "FB_id is: " + id);
        return "http://graph.facebook.com/" + id + "/picture";
    }

    public String getImageUrl(BaasFile image){
        String streamUri = image.getStreamUri().toString();
        String cropUri = streamUri.substring(0,112);
        return cropUri;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result);
    }
}


