package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasFile;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.Grant;
import com.baasbox.android.Role;
import com.baasbox.android.json.JsonArray;
import com.baasbox.android.json.JsonObject;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by danybr-dev on 31/01/16.
 */

public class BaaSUploadGeeft extends AsyncTask<Void,Void,Boolean> {

    private final String TAG = "BaaSUploadGeeft";
    Context mContext;
    Geeft mGeeft;
    String mOldGeeftId;
    boolean mModify;
    TaskCallbackBoolean mCallback;

    /**
     * Constructor to create an object Geeft to send to Baasbox
     **/
    public BaaSUploadGeeft(Context context, Geeft geeft,boolean modify,
                           TaskCallbackBoolean callback) {
        mContext = context;
        mGeeft = geeft;
        mModify = modify;
        mCallback = callback;
    }

    public BaaSUploadGeeft(Context context, Geeft geeft, String id,
                           TaskCallbackBoolean callback) {
        mContext = context;
        mGeeft = geeft;
        mCallback = callback;
        mOldGeeftId = id;
        mModify = false;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        if(BaasUser.current() !=null) {
            String userFbId = BaasUser.current().getScope(BaasUser.Scope.REGISTERED).getObject("_social")
                    .getObject("facebook")
                    .get("id").toString();
            String docUserId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id");

            BaasDocument doc;

            if(mModify){ //If we are in editing mode of geeft
                doc = getExistedGeeft();
                if(doc == null) //It doesn't happen, but check
                    return false;
            }
            else{ // new Geeft
                doc = new BaasDocument("geeft");
            }

            doc.put("title", mGeeft.getGeeftTitle());
            doc.put("description", mGeeft.getGeeftDescription());
            doc.put("location", mGeeft.getUserLocation());
            doc.put("closed", false);
            doc.put("cap", mGeeft.getUserCap());
            doc.put("name", getFacebookName());
            doc.put("baasboxUsername",BaasUser.current().getName());
            doc.put("userFbId",userFbId);
            doc.put("profilePic", getProfilePicFacebook());
            doc.put("deadline", mGeeft.getDeadLine()); // is timestamp in long
            doc.put("category", mGeeft.getCategory().toLowerCase());
            // send the field fo allow communication and automatic selection; remember to manage them
            // in the BassReserveTask
            doc.put("automaticSelection", mGeeft.isAutomaticSelection());
            doc.put("allowCommunication", mGeeft.isAllowCommunication());
            doc.put("height",mGeeft.getGeeftHeight());
            doc.put("width",mGeeft.getGeeftWidth());
            doc.put("depth",mGeeft.getGeeftDepth());
            doc.put("allowDimension",mGeeft.isDimensionRead());
            doc.put("deleted",false);
            doc.put("assigned",false);
            doc.put("taken", false);

            // send labels in an array built splitting the label string
            doc.put("labels", mGeeft.getGeeftArrayLabels());

            BaasFile image = new BaasFile();
            BaasResult<BaasFile> resImage = image.uploadSync(mGeeft.getStreamImage());
            if (resImage.isSuccess()) {
                Log.d(TAG, "Image uploaded");
                BaasResult<Void> resGrant = image.grantAllSync(Grant.READ, Role.ANONYMOUS);
                if (resGrant.isSuccess()) {
                    Log.d(TAG, "Granted");
                    doc.put("image", getImageUrl(image));// Now imageUrl is cutted,append in
                    // another class your session token
                    //Retrieve the link at image and put in doc,then save the doc and return true
                    BaasResult<BaasDocument> resDoc = doc.saveSync();
                    if (resDoc.isSuccess()) {
                        mGeeft.setId(doc.getId());
                        Log.d(TAG, "Doc saved with success");
                        BaasResult<Void> resDocGrantRead = doc.grantAllSync(Grant.READ, Role.REGISTERED);
                        if (resDocGrantRead.isSuccess()) {
                            Log.d(TAG, "Doc granted with success");
                            BaasResult<Void> resDocGrantDel = doc.grantAllSync(Grant.DELETE, TagsValue.ROLE_MODERATOR);
                            if(resDocGrantDel.isSuccess()) {
                                if (mOldGeeftId != null) {
                                    Log.d(TAG, "OLD GEEFT ID: " + mOldGeeftId);
                                    BaasResult<BaasLink> resLink = BaasLink.createSync("geeft_story", mGeeft.getId()
                                            , mOldGeeftId);
                                }
                                return createDonatedLink(doc,docUserId,mModify);
                            }
                            else{
                                Log.e(TAG,"Error with grant DELETE to MODERATOR");
                                return false;
                            }
                        } else {
                            Log.e(TAG, "Error with grant READ to REGISTERED");
                            return false;
                        }
                    } else {
                        Log.e(TAG, "Error with doc");
                        return false;
                    }
                } else {
                    Log.e(TAG, "Error with grant ANONYMOUS to images");
                    return false;
                }
            } else {
                Log.e(TAG, "Fatal error upload:" + resImage.error());
                return false;
            }
        }
        else{
            return false;
        }
    }

    private BaasDocument getExistedGeeft(){ //Fetch existing geeft
        BaasResult<BaasDocument> resDocFetched = BaasDocument.fetchSync("geeft",mGeeft.getId());
        if(resDocFetched.isSuccess()){
            return resDocFetched.value();
        }
        else{
            if(resDocFetched.error() instanceof BaasInvalidSessionException){
                Log.e(TAG,"Session Expired");
                return null;
            }
            else{
                Log.e(TAG,"Fatal error while fetching doc");
                return null;
            }
        }
    }

    private boolean createDonatedLink(BaasDocument doc,String docUserId,boolean modify){
        if(!modify) { // If is a new Geeft,create link in donate
            BaasResult<BaasLink> resLink = BaasLink.createSync("donated", mGeeft.getId(), docUserId);
            //TODO : swap and Manage resLink
            if (resLink.isSuccess()) { //Link created
                BaasLink value = resLink.value();
                Log.d(TAG, "Link id is :" + value.getId() + " and docUser id is: " + docUserId);
                Log.d(TAG, "Link IN is: " + value.in().getId().equals(mGeeft.getId()) +
                        " OUT is: " + value.out().getId().equals(docUserId));
                mGeeft.setDonatedLinkId(value.getId()); //is for BaaSDeleteGeeftTask
                Log.d(TAG,"link is:" + value.getId());
                Log.d(TAG, "are same:" + value.getId().equals(mGeeft.getDonatedLinkId()));
                doc.put("donatedLinkId", mGeeft.getDonatedLinkId());
                BaasResult<BaasDocument> resDoc = doc.saveSync();
                if(resDoc.isSuccess()){
                    return true;
                }
                else{
                    Log.e(TAG,"error when put link in doc");
                    return false;
                }
            } else {
                return false;
            }
        }
        else // existing Geeft --> Existing link
            return true;
    }

    private String getFacebookName(){// return display name of user's profile
        String FbName = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("name");
        Log.d(TAG, "FB_Name is: " + FbName);
        return FbName;
    }

    private String getProfilePicFacebook(){ // return link of user's profile picture
        JsonObject field = BaasUser.current().getScope(BaasUser.Scope.REGISTERED);
        String id = field.getObject("_social").getObject("facebook").getString("id");
        Log.d(TAG, "FB_id is: " + id);
        return "https://graph.facebook.com/" + id + "/picture?type=large";
    }

    private String getImageUrl(BaasFile image){
        String streamUri = image.getStreamUri().toString();
        String temp[] = streamUri.split("\\?");
        StringBuilder stbuild = new StringBuilder("");
        stbuild.append(temp[0]).append("?X-BB-SESSION=");
        return stbuild.toString();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result);
    }
    public String getDeadlineTimestamp(int expTime){ // I know,there is a delay between creation and upload time of document,
        //so we have a not matching timestamp (deadline and REAL deadline
        // calculated like creation data + exptime in days)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE, expTime); // Adding "expTime" days
        //String deadline = sdf.format(c.getTime()); //return Date,not timestamp.
        String deadline = ""+ c.getTimeInMillis()/1000; //get timestamp
        Log.d(TAG, "deadline is:" + deadline); //DELETE THIS AFTER DEBUG
        return deadline;
    }
}


