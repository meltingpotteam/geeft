package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;


import java.util.List;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanArrayToken;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by danybr-dev on 22/04/16.
 */
public class BaaSGetStatistics extends AsyncTask<Void,Void,Boolean> {

    private final String TAG = getClass().getName();

    Context mContext;
    TaskCallbackBooleanArrayToken mCallback;
    private double mInfo[];
    private int mResultToken;
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    //-------------------

    public BaaSGetStatistics(Context context,
                             TaskCallbackBooleanArrayToken callback) {
        mContext = context;
        mInfo = new double[12];
        mInfo[1] = 0;
        mInfo[2] = 0;
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if(BaasUser.current()==null)
            return false;

        BaasQuery.Criteria query = BaasQuery.builder()
                .orderBy("user.name")
                .criteria();
        BaasResult<List<BaasUser>> resListUsers = BaasUser.fetchAllSync();
        if(resListUsers.isSuccess()){
            List<BaasUser> listUsers = resListUsers.value();
            mInfo[0] = listUsers.size();
            Log.d(TAG,"Users without profilePic are: ");
            for(BaasUser user : listUsers){
                JsonObject scopeRegistered = user.getScope(BaasUser.Scope.REGISTERED);
                if(scopeRegistered.getString("profilePic") == null){
                    Log.d(TAG,user.getName() + "   -----   "
                            + scopeRegistered.getString("username"));
                }
                if(scopeRegistered.getDouble("feedback") == null){
                    Log.d(TAG," and he has no feedback!");
                }
                else{
                    if(scopeRegistered.getDouble("feedback") == 5)
                        Log.d(TAG,user.getName() + "   -----   "
                                + scopeRegistered.getString("username") + " and he has 5 for feedback!");
                }
                if(scopeRegistered.getObject("_social").getObject("facebook") != null){
                    mInfo[1] += 1;
                }
                else{
                    mInfo[2] += 1;
                }
                if(scopeRegistered.getString("gender") == null)
                    mInfo[5] += 1;

                else if(scopeRegistered.getString("gender").equals("female")){
                    mInfo[3] += 1;
                }
                else if(scopeRegistered.getString("gender").equals("male")){
                    mInfo[4] += 1;
                }
            }

            int n_closed = 0;
            BaasQuery.Criteria query1 = BaasQuery.builder().where("assigned = false").criteria();
            BaasResult<List<BaasDocument>> resListGeeftsNotAssigned = BaasDocument.fetchAllSync(TagsValue.COLLECTION_GEEFT,query1);
            if(resListGeeftsNotAssigned.isSuccess()){
                mInfo[6] = resListGeeftsNotAssigned.value().size();
                for(BaasDocument geeft : resListGeeftsNotAssigned.value()){
                    if(!geeft.getBoolean("closed"))
                        n_closed += 1;
                }
                mInfo[10] = n_closed;
            }

            BaasResult<List<BaasDocument>> resListGeefts = BaasDocument.fetchAllSync(TagsValue.COLLECTION_GEEFT);
            if(resListGeefts.isSuccess()){
                mInfo[7] = resListGeefts.value().size();
                List<BaasDocument> listGeefts = resListGeefts.value();
                int countLocationRome = 0;
                int countAssigned = 0;
                for(BaasDocument geeft : listGeefts){
                    //Log.d(TAG,"Location: " + geeft.getString("location"));
                    if(geeft.getString("location").equals("Roma"))
                        countLocationRome += 1;
                    if(!geeft.getBoolean("deleted") && geeft.getBoolean("assigned")
                            && geeft.getBoolean("taken") && geeft.getBoolean("given"))
                        countAssigned++;
                }
                Log.d(TAG,"Values..count:" + countLocationRome + " size:" + resListGeefts.value().size());
                double percentual = ((double)countLocationRome/(double)resListGeefts.value().size());
                mInfo[9] = Math.floor(percentual * 10000) / 100; //It serves to round to two digits
                Log.d(TAG,"mInfo:" + mInfo[9] + " percentual: " + percentual);
                mInfo[11] = countAssigned;
            }

            BaasResult<List<BaasDocument>> resListGeeftorys = BaasDocument.fetchAllSync("story");
            if(resListGeeftorys.isSuccess()){
                mInfo[8] = resListGeeftorys.value().size();
            }
            return true;

        }
        else{
            if (resListUsers.error() instanceof BaasInvalidSessionException) {
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            } else {
                Log.e(TAG, "Error when retrieve links: " + resListUsers.error());
                mResultToken = RESULT_FAILED;
                return false;
            }
        }

    }



    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result, mInfo,mResultToken);
    }

}