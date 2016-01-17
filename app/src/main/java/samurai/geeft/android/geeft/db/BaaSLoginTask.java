package samurai.geeft.android.geeft.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activity.MainActivity;

/**
 * This task manages the social sign-in server-side using BaasBox.
 */
public class BaaSLoginTask extends AsyncTask<Void,Integer,Boolean> {

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
        if(mBaasProvider == "facebook") {
            //Bypassed google
            BaasResult<BaasUser> baasResult = BaasUser
                    .signupWithProviderSync(mBaasProvider, mToken, mSecret);
            //Log.d("LOG", BaasUser.current().toString());

            /**
             * In case of success it returns true if not it shows Toast text
             * and returns false
             */
            if (baasResult.isSuccess())
                return true;
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
        else if (mBaasProvider == "google"){
            //TO COMPLETED
            //BYPASS G+ Login Problem
            //G+ User is logged,but not logged with BaasUser
            //@Ugo: I know,now it isn't modular.. Now it is need,but temporary :)
            MainActivity.setIsGoogleUser(true);
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
