package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;

import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import java.util.List;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanArrayToken;

/**
 * Created by danybr-dev on 08/02/16.
 */
public class BaaSGetGeefterInformation extends AsyncTask<Void,Void,Boolean> {

    private final String TAG = getClass().getName();
    private Context mContext;
    private double mUserInformation[];
    TaskCallbackBooleanArrayToken mCallback;
    private int mResultToken;
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    //-------------------

    public BaaSGetGeefterInformation(Context context, TaskCallbackBooleanArrayToken callback) {
        mUserInformation = new double[3];
        mContext = context;
        mCallback = callback;

    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        BaasUser currentUser = BaasUser.current();
        //--- Try to get followers to check if the current session is valid or not
        BaasResult<List<BaasUser>> resultSession = currentUser.followersSync();
        if(resultSession.isFailed()){
            if(resultSession.error() instanceof BaasInvalidSessionException){
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            }
            else{
                mResultToken = RESULT_FAILED;
                return false;
            }
        }
        //---------------------------------------
        if(currentUser !=null) {
            double feedback = currentUser.getScope(BaasUser.Scope.REGISTERED).get("feedback");
            long given = currentUser.getScope(BaasUser.Scope.REGISTERED).get("n_given");
            long received = currentUser.getScope(BaasUser.Scope.REGISTERED).get("n_received");

            mUserInformation[0] = feedback;
            mUserInformation[1] = (double)given;
            mUserInformation[2] = (double)received;
            return true;
        }
        else{
            mResultToken = RESULT_FAILED;
            return false;
        }

    }



    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result,mUserInformation,mResultToken);
    }

}
