package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.Grant;
import com.baasbox.android.Role;
import com.baasbox.android.json.JsonArray;
import com.baasbox.android.json.JsonObject;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;

/**
 * This task manages the social sign-in server-side using BaasBox.
 * Update by danybr-dev on 2/02/16
 */
public class BaaSLoginTask extends AsyncTask<Void,Integer,Boolean> {
    private static final String TAG = "BaaSLoginTask";
    /**
     * Strings used for confrontation and for deciding if signing in with
     * Facebook or Google+.
     */
    private static final String FACEBOOK="FACEBOOK";
    private static final String GOOGLE = "GOOGLE";

    /**
     * Key secret used for obtaining user private information by Facebook or Google+.
     */
    private static final String FACEBOOK_SECRET = "52001af9217446b6408ef593f56e3f1a";
    private static final String GOOGLE_SECRET = "RIlloFFnJRSMnKN48ASkc8Gu";
    private  Bundle mUserData;

    /**
     * mContext is the context calling the task,
     * mProvider indicates which provider user is using for sign-in (Facebook, Google+).
     * mToken is the session token needed for server function.
     * mSecret is the provider secret key needed for server function.
     * mCallback is the callback handler.
     * mBaasProvider is the provider default value used by the server.
     */

    private Context mContext;
    private String mProvider,mToken,mSecret;
    private TaskCallbackBoolean mCallback;
    private String mBaasProvider;
    private BaasUser user;

    /**
     * BaaSLoginTask default constructor,.
     * @param context is the context calling the task
     * @param provider indicates which provider user is using for sign-in (Facebook, Google+)
     * @param token is the session token needed for server function
     * @param callback is the callback handler
     */
    public BaaSLoginTask(Context context, String provider,
                         String token, Bundle userData,TaskCallbackBoolean callback) {
        mContext = context;
        mProvider = provider;
        mToken = token;
        mCallback = callback;
        mUserData = userData;

        /**
         * Assigning server default value based with the provider
         */
        if(mProvider.equals(FACEBOOK)) {
            mBaasProvider = BaasUser.Social.FACEBOOK;
            mSecret = FACEBOOK_SECRET;
        }
        else if(mProvider.equals(GOOGLE)) {
            mBaasProvider = BaasUser.Social.GOOGLE;
            mSecret = GOOGLE_SECRET;
        }
        Log.d("LOG", "The provider is " + mProvider
                + ", the BaaS default value is " + mBaasProvider);
    }
    public BaaSLoginTask(Context context, String provider,
                         String token, BaasUser user,TaskCallbackBoolean callback) {
        mContext = context;
        mProvider = provider;
        mToken = token;
        mCallback = callback;
        this.user = user;

        /**
         * Assigning server default value based with the provider
         */
        if(mProvider.equals(FACEBOOK)) {
            mBaasProvider = BaasUser.Social.FACEBOOK;
            mSecret = FACEBOOK_SECRET;
        }
        else if(mProvider.equals(GOOGLE)) {
            mBaasProvider = BaasUser.Social.GOOGLE;
            mSecret = GOOGLE_SECRET;
        }
        Log.d("LOG", "The provider is " + mProvider
                + ", the BaaS default value is " + mBaasProvider);
    }

    /**
     * @param arg0 no value
     * @return true-> success sign_in, false-> error or cancelled sign-in
     */
    @Override
    protected Boolean doInBackground(Void... arg0) {
        /**
         * Variable storing the result of the sync BaaS social login
         */
        BaasResult<BaasUser> baasResult = null;

        if(mBaasProvider.equals(BaasUser.Social.FACEBOOK)){
            baasResult = BaasUser
                    .signupWithProviderSync(mBaasProvider, mToken, mSecret);
            if (BaasUser.current() != null) {
                Log.d(TAG, BaasUser.current().toString());
                Log.d(TAG, baasResult.value().toString());
            }
        }


        /**
         * In case of success it returns true if not it shows Toast text
         * and returns false
         */
        if (mBaasProvider.equals(BaasUser.Social.GOOGLE)||baasResult.isSuccess()) {
            //Log.d(TAG,"ID IS: " + BaasUser.current().getScope(BaasUser.Scope.PRIVATE)..getString("id"));
            user = BaasUser.current();
            if (user != null) {
                BaasResult<BaasUser> userBaasResult = BaasUser.current().refreshSync();
                if (userBaasResult.isSuccess()){
                    Log.d(TAG,"Successo");
                }else{
                    Log.d(TAG,"Fallito");
                    return false;
                }
                Log.d(TAG, user.toString());
                String userDocId =
                        BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id");
                String name =
                        BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("name");
                boolean isNull;
                if (userDocId == null) {
                    isNull = true;
                    BaasDocument doc = new BaasDocument("linkable_users");
                    JsonArray JSONUserLinks = new JsonArray();
                    doc.put("prenoteLinks", JSONUserLinks);
                    BaasResult<BaasDocument> resDoc = doc.saveSync();
                    if (resDoc.isSuccess()) {
                        BaasResult<Void> resGrantDoc = doc.grantAllSync(Grant.READ, Role.REGISTERED);
                        //TODO: Check this,if doesn't works,replace with grantAllSync
                        if(resGrantDoc.isSuccess()){
                            Log.d(TAG, "Doc ID is: " + doc.getId());
                            if(mBaasProvider.equals(BaasUser.Social.FACEBOOK)) {
                                user.getScope(BaasUser.Scope.REGISTERED)
                                        .put("profilePic", getProfilePicFacebook());
                            }
                            //Insert in doc_id the id of docUser,linked with geefts
                            user.getScope(BaasUser.Scope.PRIVATE).put("doc_id", doc.getId());
                            //Insert Feedback,first registration is 5
                            double initFeedback = 4.99;
                            user.getScope(BaasUser.Scope.REGISTERED).put("feedback", initFeedback);
                            //Insert n_feedback,first registration is 0
                            user.getScope(BaasUser.Scope.REGISTERED).put("n_feedback", 1);
                            //Insert n_feedback,first registration is 0
                            user.getScope(BaasUser.Scope.REGISTERED).put("feedback_sum", initFeedback);
                            //Insert n_given,first registration is 0
                            user.getScope(BaasUser.Scope.REGISTERED).put("n_given", 0);
                            //Insert n_received,first registration is 0
                            user.getScope(BaasUser.Scope.REGISTERED).put("n_received", 0);
                            //Insert submits_without,first registration is 0
                            user.getScope(BaasUser.Scope.REGISTERED).put("submits_without", 0);
                            //Insert submits_active,first registration is 0
                            user.getScope(BaasUser.Scope.REGISTERED).put("submits_active", 0);
                            //Insert in doc_id the id of docUser,linked with geefts
                            user.getScope(BaasUser.Scope.REGISTERED).put("doc_id", doc.getId());
                            //Insert username
                            //user.getScope(BaasUser.Scope.REGISTERED).put("username", name);
                            if(mUserData!=null){
                                //Insert location
                                user.getScope(BaasUser.Scope.REGISTERED)
                                        .put("location", mUserData.getString("location"));
                                //Insert email
                                /*user.getScope(BaasUser.Scope.REGISTERED)
                                        .put("email", mUserData.getString("email"));*/
                                //Insert gender
                                user.getScope(BaasUser.Scope.REGISTERED)
                                        .put("gender", mUserData.getString("gender"));
                                //Insert birthday
                                user.getScope(BaasUser.Scope.REGISTERED)
                                        .put("birthday", mUserData.getString("birthday"));
                            }

                            BaasResult<BaasUser> resUser = user.saveSync();
                            if (resUser.isSuccess()) {
                                Log.d(TAG, "New user, document created");
                                return true;
                            } else {
                                Log.e(TAG, "FATAL ERROR userScope not update");
                                return false;
                            }
                        }
                        else{
                            Log.e(TAG, "FATAL ERROR document not GRANTED");
                            return false;
                        }
                    } else {
                        Log.e(TAG, "FATAL ERROR document not created");
                        return false;
                    }
                } else {
                    Log.d(TAG, "User already registered");
                    if(user.getScope(BaasUser.Scope.REGISTERED)
                            .get("profilePic")==null){
                        if (mBaasProvider.equals(BaasUser.Social.FACEBOOK) &&
                                user.getScope(BaasUser.Scope.REGISTERED)
                                        .get("profilePic")==null) {
                            user.getScope(BaasUser.Scope.REGISTERED)
                                    .put("profilePic", getProfilePicFacebook());
                        }
                    }
                    if(mUserData!=null){
                        //Insert location
                        user.getScope(BaasUser.Scope.REGISTERED)
                                .put("location", mUserData.getString("location"));
                        //Insert email
                        /*user.getScope(BaasUser.Scope.REGISTERED)
                                .put("email", mUserData.getString("email"));*/
                        //Insert gender
                        user.getScope(BaasUser.Scope.REGISTERED)
                                .put("gender", mUserData.getString("gender"));
                        //Insert birthday
                        user.getScope(BaasUser.Scope.REGISTERED)
                                .put("birthday", mUserData.getString("birthday"));
                    }

                    BaasResult<BaasUser> resUser = user.saveSync();
                    if (resUser.isSuccess()) {
                        Log.d(TAG, "New user, document created");
                        return true;
                    } else {
                        Log.e(TAG, "FATAL ERROR userScope not update");
                        return false;
                    }
                }
            }else{ // if user is null
                Log.e(TAG,"Error,user is NULL!");
                return false;
            }
        }

        else if (baasResult.isCanceled()) {
            Log.d(TAG, "ID IS: " + BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("id"));
            if (mBaasProvider.equals(FACEBOOK))
                this.publishProgress(R.string.toast_fb_login_canc);
            else{
                Log.d(TAG,"GOOGLE SIGN IN CANCELED");
                this.publishProgress(R.string.toast_google_login_canc);
            }
        } else if (baasResult.isFailed()) {
            if (mProvider.equals(FACEBOOK))
                this.publishProgress(R.string.toast_fb_login_err);
            else if (mProvider.equals(GOOGLE)){
                Log.d(TAG,"GOOGLE SIGN IN FAILED: "+baasResult.error().toString());
                this.publishProgress(R.string.toast_google_login_err);
            }
        }
        return false;
    }

    private String getProfilePicFacebook(){ // return link of user's profile picture
        JsonObject field = BaasUser.current().getScope(BaasUser.Scope.REGISTERED);
        String id = field.getObject("_social").getObject("facebook").getString("id");
        Log.d(TAG, "FB_id is: " + id);
        return "https://graph.facebook.com/" + id + "/picture?type=large";
    }

    /**
     * Displays String for toast sent by doInBackground.
     * @param values  R.string message Integer ID value
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Toast.makeText(mContext,values[0], Toast.LENGTH_LONG).show();
    }



    /**
     * Handles the callback
     * @param result doInBackground result:
     *               true-> success sign_in, false-> error or cancelled sign-in
     */
    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result);
    }

}
