package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasFile;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.Grant;
import com.baasbox.android.Role;
import com.baasbox.android.json.JsonObject;

import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanArray;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by danybr-dev on 08/02/16.
 */
public class BaaSGetGeefterInformation extends AsyncTask<Void,Void,Boolean> {

    private final String TAG = getClass().getName();
    private Context mContext;
    private long mUserInformation[];
    private GeeftItemAdapter.ViewHolder mHolder;
    TaskCallbackBooleanArray mCallback;

    public BaaSGetGeefterInformation(Context context,GeeftItemAdapter.ViewHolder holder, TaskCallbackBooleanArray callback) {
        mUserInformation = new long[3];
        mContext = context;
        mHolder = holder;
        mCallback = callback;

    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        BaasUser currentUser = BaasUser.current();
        if(currentUser !=null) {
            long feedback = currentUser.getScope(BaasUser.Scope.REGISTERED).get("feedback");
            long given = currentUser.getScope(BaasUser.Scope.REGISTERED).get("n_given");
            long received =currentUser.getScope(BaasUser.Scope.REGISTERED).get("n_received");
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
        mCallback.done(result,mHolder,mUserInformation);
    }

}
