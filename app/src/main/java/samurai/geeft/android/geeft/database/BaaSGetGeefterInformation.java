package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;

import com.baasbox.android.BaasUser;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanArray;

/**
 * Created by danybr-dev on 08/02/16.
 */
public class BaaSGetGeefterInformation extends AsyncTask<Void,Void,Boolean> {

    private final String TAG = getClass().getName();
    private Context mContext;
    private double mUserInformation[];
    TaskCallbackBooleanArray mCallback;

    public BaaSGetGeefterInformation(Context context, TaskCallbackBooleanArray callback) {
        mUserInformation = new double[3];
        mContext = context;
        mCallback = callback;

    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        BaasUser currentUser = BaasUser.current();
        if(currentUser !=null) {
            double feedback = currentUser.getScope(BaasUser.Scope.REGISTERED).get("feedback");
            long given = currentUser.getScope(BaasUser.Scope.REGISTERED).get("n_given");
            long received = currentUser.getScope(BaasUser.Scope.REGISTERED).get("n_received");

            mUserInformation[0] = feedback;
            mUserInformation[1] = given;
            mUserInformation[2] = received;
            return true;
        }
        else{
            return false;
        }

    }



    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result,mUserInformation);
    }

}
