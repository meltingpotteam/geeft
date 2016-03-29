package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.Rest;
import com.baasbox.android.json.JsonObject;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by joseph on 27/03/16.
 */
public class BaaSUpdateUsernameMail extends AsyncTask<Void,Void,Boolean> {

    private final String TAG = getClass().getName();
    private String mFeedbackComment;
    private Context mContext;
    private String mHisBaasboxUsername;
    private String mNickname;
    private String mEmail;
    private TaskCallbackBooleanToken mCallback;
    private int mResultToken;
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    //-------------------
    private BaasUser mBaasUser;

    /**
     *
     **/
    public BaaSUpdateUsernameMail(Context context,String hisBaasboxUsername, String nickname,
                                  String email, TaskCallbackBooleanToken callback) {
        mContext = context;
        mHisBaasboxUsername = hisBaasboxUsername;
        mNickname = nickname;
        mEmail  = email;
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        boolean result;
        if(BaasUser.current() !=null) {
            Log.d(TAG, "UsernameMail username:" + mHisBaasboxUsername);
            BaasResult<BaasUser> resUser = BaasUser.fetchSync(mHisBaasboxUsername);
            if(resUser.isSuccess()){
                BaasUser user = resUser.value();
                if(getBaasUser(mHisBaasboxUsername)) {
                    if (putNewUserNicknameEmailInBaasbox(mNickname, mEmail)) {
                        return true;
                    }
                }
            }
            else{
                if(resUser.error() instanceof BaasInvalidSessionException){
                    Log.e(TAG,"Invalid Session Token");
                    mResultToken = RESULT_SESSION_EXPIRED;
                    return false;
                }
                else{
                    Log.e(TAG,"Error while fetching user");
                    mResultToken = RESULT_FAILED;
                    return false;
                }
            }
        }
        else{
            mResultToken = RESULT_SESSION_EXPIRED;
            return false;
        }
        return false;

    }


    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result, mResultToken);
    }


    private boolean getBaasUser(String mUsername) {
        BaasResult<BaasUser> resUser = BaasUser.fetchSync(mUsername);
        if(resUser.isSuccess()){
            mBaasUser = resUser.value();
            return true;
        }else{
            if(resUser.error() instanceof BaasInvalidSessionException){
                Log.e(TAG,"Invalid Session Token");
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            }
            else{
                Log.e(TAG,"Error while fetching user");
                Log.e(TAG,resUser.error().toString());
                mResultToken = RESULT_FAILED;
                return false;
            }
        }

    }


    private boolean putNewUserNicknameEmailInBaasbox(String nickname, String email) { //this is for the new feed
        BaasResult<JsonObject> resFeedback = BaasBox.rest().sync(Rest.Method.GET, "plugin/update.NicknameEmail?" +
                "baasboxUsername=" + mHisBaasboxUsername + "&nickname=" + nickname + "&email=" + email);

        if(resFeedback.isSuccess()){
            Log.d(TAG,"Successo");
            Log.d(TAG,resFeedback.toString());
            return true;
        }
        else{
            Log.d(TAG,"Errore");
            Log.d(TAG,resFeedback.toString());
            return false;
        }

    }

}




