package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import samurai.geeft.android.geeft.interfaces.TaskCallBackBooleanInt;

/**
 * Created by danybr-dev on 16/02/2016.
 */
public class BaaSSignalisationTask extends AsyncTask<Void,Void,Boolean> {

        private final String TAG = getClass().getName();
        Context mContext;
        String mDocId;
        int mAction;
        TaskCallBackBooleanInt mCallback;

        public BaaSSignalisationTask(Context context, String docId,
                               TaskCallBackBooleanInt callback) {
            mContext = context;
            mDocId = docId; //id of doc to be reported
            mCallback = callback;
        }


        @Override
        protected Boolean doInBackground(Void... arg0) {
            if(BaasUser.current() !=null) {
                BaasUser currentUser = BaasUser.current();
                mAction = 0;
                BaasResult<BaasDocument> resDocToBeDelete = BaasDocument.fetchSync("geeft",mDocId);
                if(resDocToBeDelete.isSuccess()){
                    BaasDocument docToBeDelete = resDocToBeDelete.value();
                    if(currentUser.hasRole("registered")){ // if I am a mere mortal,
                                                                  // I can only report by e-mail
                        mAction = 1; //send an email
                        return true;
                    }
                    else if (currentUser.hasRole("moderator")){ //Ok,i can delete directly
                        BaasResult<Void> resDelete = docToBeDelete.deleteSync();
                        if(resDelete.isSuccess()){
                            mAction = 2; //show a toast
                            return true;
                        }
                        else{
                            Log.e(TAG,"Error with deletion of doc:" + resDelete.error());
                            //mAction = 3;
                            return false;
                        }
                    }
                    else {
                        Log.e(TAG,"Error with signalisation");
                        Log.e(TAG,"Are you by chance an administrator?");
                        //TODO: Check this
                        return false;
                    }
                }
                else{
                    Log.e(TAG,"Error when retrieve doc:" + resDocToBeDelete.error());
                    return false;
                }
            }
            else{
                return false;
            }
        }
    protected void onPostExecute(Boolean result) {
        mCallback.done(result,mAction,mDocId);
    }
}
