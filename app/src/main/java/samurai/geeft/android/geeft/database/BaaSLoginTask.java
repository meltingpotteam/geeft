package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonArray;

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
    private static final String GOOGLE_SECRET = "-ZaokFIw9scM3ZSVwp0kJHU6";

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

    /**
     * BaaSLoginTask default constructor,.
     * @param context is the context calling the task
     * @param provider indicates which provider user is using for sign-in (Facebook, Google+)
     * @param token is the session token needed for server function
     * @param callback is the callback handler
     */
    public BaaSLoginTask(Context context, String provider,
                         String token, TaskCallbackBoolean callback) {
        mContext = context;
        mProvider = provider;
        mToken = token;
        mCallback = callback;

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
        if(mBaasProvider.equals("facebook")) {
            //Bypassed google
            BaasResult<BaasUser> baasResult = BaasUser
                    .signupWithProviderSync(mBaasProvider, mToken, mSecret);
            Log.d(TAG, BaasUser.current().toString());
            Log.d(TAG, baasResult.value().toString());


            /**
             * In case of success it returns true if not it shows Toast text
             * and returns false
             */
            if (baasResult.isSuccess()) {
                //Log.d(TAG,"ID IS: " + BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("id"));
                BaasUser user = BaasUser.current();
                if (user != null) {
                    Log.d(TAG, user.toString());
                    BaasResult<BaasUser> userBaasResult = BaasUser.current().refreshSync();
                    if (userBaasResult.isSuccess()){
                        Log.d(TAG,"Successo");
                    }else{
                        Log.d(TAG,"Fallito");
                        return false;
                    }
                    String UserDocId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id");
                    if (UserDocId == null) {
                        BaasDocument doc = new BaasDocument("linkable_users");
                        JsonArray JSONUserLinks = new JsonArray();
                        doc.put("prenoteLinks", JSONUserLinks);
                        BaasResult<BaasDocument> resDoc = doc.saveSync();
                        if (resDoc.isSuccess()) {
                            Log.d(TAG, "Doc ID is: " + doc.getId());
                            //Insert in doc_id the id of docUser,linked with geefts
                            user.getScope(BaasUser.Scope.PRIVATE).put("doc_id", doc.getId());
                            //Insert Feedback,first registration is 5
                            double initFeedback = 4.3;
                            user.getScope(BaasUser.Scope.REGISTERED).put("feedback",initFeedback);
                            //Insert n_given,first registration is 0
                            user.getScope(BaasUser.Scope.REGISTERED).put("n_given",0);
                            //Insert n_received,first registration is 0
                            user.getScope(BaasUser.Scope.REGISTERED).put("n_received",0);
                            //Insert submits_without,first registration is 0
                            user.getScope(BaasUser.Scope.REGISTERED).put("submits_without",0);
                            //Insert submits_active,first registration is 0
                            user.getScope(BaasUser.Scope.REGISTERED).put("submits_active",0);
                            BaasResult<BaasUser> resUser = user.saveSync();
                            if (resUser.isSuccess()) {
                                Log.d(TAG, "New user, document created");
                                return  true;
                            } else {
                                Log.e(TAG, "FATAL ERROR userScope not update");
                                return false;
                            }
                        } else {
                            Log.e(TAG, "FATAL ERROR document not created");
                            return false;
                        }
                    } else {
                        Log.d(TAG, "User already registered");
                        return true;
                    }
                }else{ // if user is null
                    Log.e(TAG,"Error,user is NULL!");
                    return false;
                }
            }

            else if (baasResult.isCanceled()) {
                if (mBaasProvider.equals(FACEBOOK))
                    this.publishProgress(R.string.toast_fb_login_canc);
                else
                    this.publishProgress(R.string.toast_google_login_canc);
            } else if (baasResult.isFailed()) {
                if (mProvider.equals(FACEBOOK))
                    this.publishProgress(R.string.toast_fb_login_err);
                else if (mProvider.equals(GOOGLE))
                    this.publishProgress(R.string.toast_google_login_err);
            }
            return false;
        }
        else if (mBaasProvider.equals("google")){
            //TO COMPLETED
            //BYPASS G+ Login Problem
            //G+ User is logged,but not logged with BaasUser
            //@Ugo: I know,now it isn't modular.. Now it is need,but temporary :)
            //MainActivity.setIsGoogleUser(true);
            return true;
        }
        return false;
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
