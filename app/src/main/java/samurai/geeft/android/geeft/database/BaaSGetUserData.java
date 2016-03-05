package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.User;

/**
 * Created by ugookeadu on 05/03/16.
 */
public class BaaSGetUserData  extends AsyncTask<Void,Void,Boolean> {
    private final String TAG = getClass().getSimpleName() ;
    private Context mContext;
    private User mUser;
    private TaskCallbackBoolean mCallback;

    public BaaSGetUserData(Context context, @Nullable User user
            ,TaskCallbackBoolean callback){
        mContext = context;
        mUser = user;
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        BaasResult<BaasUser> baasResult = BaasUser.fetchSync(mUser.getID());
        try {
            if (baasResult.isSuccess()) {
                fillUserData(baasResult.get());
                return true;
            } else if (baasResult.isFailed()) {
                Log.d(TAG, baasResult.error().getMessage());
            }
        }catch (Exception ex){
            Log.d(TAG, ex.getMessage());
        }
        return false;
    }

    private void fillUserData(BaasUser baasUser) {
        JsonObject registeredFields = baasUser.getScope(BaasUser.Scope.REGISTERED);
        JsonObject privateFields = baasUser.getScope(BaasUser.Scope.PRIVATE);

        String fbID = registeredFields.getObject("_social").getObject("facebook").getString("id");
        String username = privateFields.get("name").toString();
        String docId = privateFields.getString("doc_id");
        double userRank = registeredFields.get("feedback");

        mUser.setFbID(fbID);
        mUser.setUsername(username);
        mUser.setDocId(docId);
        mUser.setRank(userRank);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result);
    }
}
