package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;

import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;

import java.util.List;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanStringArrayToken;

/**
 * Created by danybr-dev on 18/02/16.
 */
public class BaaSGetGeefterFullInformation extends AsyncTask<Void,Void,Boolean> {

        private final String TAG = getClass().getName();
        private Context mContext;
        private String mFullUserInformation[];
        TaskCallbackBooleanStringArrayToken mCallback;
        private int mResultToken;
        //-------------------Macros
        private final int RESULT_OK = 1;
        private final int RESULT_FAILED = 0;
        private final int RESULT_SESSION_EXPIRED = -1;
        //-------------------
        public BaaSGetGeefterFullInformation(Context context, TaskCallbackBooleanStringArrayToken callback) {
            mFullUserInformation = new String[8];
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
                long submits_without = currentUser.getScope(BaasUser.Scope.REGISTERED).get("submits_without");
                long submits_active = currentUser.getScope(BaasUser.Scope.REGISTERED).get("submits_active");
                String fbName = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("name");
                JsonObject field = BaasUser.current().getScope(BaasUser.Scope.REGISTERED);
                String fbId = field.getObject("_social").getObject("facebook").getString("id");
                String profilePicUri = "https://graph.facebook.com/" + fbId + "/picture?type=large";

                mFullUserInformation[0] = ""+feedback;
                mFullUserInformation[1] = ""+given;
                mFullUserInformation[2] = ""+received;
                mFullUserInformation[3] = ""+submits_without;
                mFullUserInformation[4] = ""+submits_active;
                mFullUserInformation[5] = fbName;
                mFullUserInformation[6] = profilePicUri;
                mFullUserInformation[7] = fbId;

                return true;
            }
            else{
                mResultToken = RESULT_FAILED;
                return false;
            }

        }



        @Override
        protected void onPostExecute(Boolean result) {
            mCallback.done(result,mFullUserInformation,mResultToken);
        }

    }
