package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanStringArray;

/**
 * Created by danybr-dev on 18/02/16.
 */
public class BaaSGetGeefterFullInformation extends AsyncTask<Void,Void,Boolean> {

        private final String TAG = getClass().getName();
        private Context mContext;
        private String mFullUserInformation[];
        TaskCallbackBooleanStringArray mCallback;

        public BaaSGetGeefterFullInformation(Context context, TaskCallbackBooleanStringArray callback) {
            mFullUserInformation = new String[6];
            mContext = context;
            mCallback = callback;

        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            BaasUser currentUser = BaasUser.current();
            if(currentUser !=null) {
                long fb = currentUser.getScope(BaasUser.Scope.REGISTERED).get("feedback");
                double feedback = (double) fb; //to avoid error of casting
                long given = currentUser.getScope(BaasUser.Scope.REGISTERED).get("n_given");
                long received = currentUser.getScope(BaasUser.Scope.REGISTERED).get("n_received");
                String fbName = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("name");

                JsonObject field = BaasUser.current().getScope(BaasUser.Scope.REGISTERED);
                String fbId = field.getObject("_social").getObject("facebook").getString("id");
                String profilePicUri = "https://graph.facebook.com/" + fbId + "/picture?type=large";

                mFullUserInformation[0] = ""+feedback;
                mFullUserInformation[1] = ""+given;
                mFullUserInformation[2] = ""+received;
                mFullUserInformation[3] = fbName;
                mFullUserInformation[4] = profilePicUri;
                mFullUserInformation[5] = fbId;
                        /*
                private TextView mFullProfileUserRank;
                private TextView mFullProfileUserGiven;
                private TextView mFullProfileUserReceived;
                private TextView mFullProfileUsername;
                private ImageView mFullProfilegUserImage;

                private ImageButton mFullProfileFbButton;
                private ParallaxImageView mFullProfileBackground;*/
                return true;
            }
            else{
                return false;
            }

        }



        @Override
        protected void onPostExecute(Boolean result) {
            mCallback.done(result,mFullUserInformation);
        }

    }
