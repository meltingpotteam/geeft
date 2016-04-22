package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baasbox.android.BaasException;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;

import java.util.List;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.models.User;

/**
 * Created by ugookeadu on 07/03/16.
 */
public class BaaSFetchUsersFromLink extends AsyncTask<Void,Void,Boolean> {

    private final String TAG = getClass().getSimpleName();
    private final Context mContext;
    private final String mLinkNameQuery;
    private final List<User> mUserList;
    private final TaskCallbackBoolean mCallback;
    private final Geeft mGeeft;
    private User mUser;
    private int count;

    public BaaSFetchUsersFromLink(Context context, Geeft geeft, String linkNameQuery
            , List<User> baasUserList ,TaskCallbackBoolean callback){
        mContext = context;
        mGeeft = geeft;
        mLinkNameQuery = linkNameQuery;
        mUserList = baasUserList;
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (BaasUser.current()!=null){
            BaasQuery.Criteria query = BaasQuery.builder()
                    .where("out.id = '" + mGeeft.getId()+"'").criteria();

            if(mLinkNameQuery != null ) {
                BaasResult<List<BaasLink>> resLinks =
                        BaasLink.fetchAllSync(mLinkNameQuery, query);
                try {
                    List<BaasLink> baasLinkList = resLinks.get();
                    Log.d(TAG, "list size= "+baasLinkList.size()+" geeftId = "+mGeeft.getId());
                    for (BaasLink baasLink : baasLinkList) {
                        BaasResult<BaasUser> baasResult = BaasUser.fetchSync(baasLink.getAuthor());
                        if (baasResult.isSuccess()) {
                            BaasUser baasUser = baasResult.get();
                            mUser = new User(baasUser.getName());
                            fillUserData(baasUser);
                            mUserList.add(mUser);
                        } else if (baasResult.isFailed()) {
                            Log.e(TAG, baasResult.error().getMessage());
                        }
                    }
                    return true;
                } catch (BaasException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private void fillUserData(BaasUser baasUser) {
        JsonObject registeredFields = baasUser.getScope(BaasUser.Scope.REGISTERED);

        String username = registeredFields.getString("username");
        String description = registeredFields.getString("user_description");
        String docId = registeredFields.getString("doc_id");
        double userRank = registeredFields.get("feedback");
        if(registeredFields.getObject("_social").getObject("facebook") == null)
            mUser.setFbID("");
        else
            mUser.setFbID(registeredFields.getObject("_social").getObject("facebook").getString("id"));
        mUser.setProfilePic(baasUser.getScope(BaasUser.Scope.REGISTERED).getString("profilePic"));
        mUser.setUsername(username);
        mUser.setDescription(description);
        mUser.setDocId(docId);
        mUser.setRank(userRank);
    }

    @Override
    protected void onPostExecute(Boolean result) {
       mCallback.done(result);
    }
}
