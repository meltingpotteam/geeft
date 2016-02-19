package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import java.util.List;

import samurai.geeft.android.geeft.adapters.GeeftStoryListAdapter;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 09/02/16.
 */
public class BaaSReceivedDonatedGeeftTask extends AsyncTask<Void,Void,Boolean> {
    private final String TAG = getClass().getName();
    Context mContext;
    List<Geeft> mGeeftList;
    String mlinkNameQuery;
    TaskCallbackBoolean mCallback;
    GeeftStoryListAdapter mGeeftStoryListAdapter;

    public BaaSReceivedDonatedGeeftTask(Context context, String linkNameQuery, List<Geeft> feedItems,
                                        GeeftStoryListAdapter Adapter,
                                        TaskCallbackBoolean callback) {
        mContext = context;
        mGeeftList = feedItems;
        mCallback = callback;
        mlinkNameQuery = linkNameQuery;
        mGeeftStoryListAdapter = Adapter;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if(BaasUser.current()==null)
            return false;
        String docUserId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id");
        BaasQuery.Criteria query =BaasQuery.builder().where("out.id like '" + docUserId + "'" ).criteria();
        Log.d(TAG,"doc_id are: " + docUserId);
        // PROBLEMA GRAVE: QUANDO FAI LA QUERY,OUT È L'UTENTE (DocUserId) E IN È IL GEEFT
        //                 QUANDO PRENDI I LINK,OUT() È IL GEEFT,IN() È L'UTENTE
        Log.d(TAG,"mNameLinkQuery is: " + mlinkNameQuery);
        if(mlinkNameQuery != null ) { // mLinkNameQuery are only
                                    // "received" and "geefted"
            BaasResult<List<BaasLink>> resLinks = BaasLink.fetchAllSync(mlinkNameQuery, query);
            List<BaasLink> links;
            if (resLinks.isSuccess()) {
                links = resLinks.value();
                //Log.d(mlinkNameQuery, getClass().getName());
                for (BaasLink link : links) {
                    BaasResult<BaasDocument> result = BaasDocument.fetchSync("geeft", link.out().getId());
                    Log.d(TAG,"geeftID: " + link.in().getId());
                    if (result.isSuccess()) {
                        Log.d(TAG, "Your links are here: " + links.size());
                        try {
                            BaasDocument document = result.get().asDocument();
                            Geeft geeft = new Geeft();
                            geeft.setGeeftImage(document.getString("image") + BaasUser.current().getToken());
                            geeft.setId(document.getId());
                            geeft.setUsername(document.getString("name"));
                            //Append ad image url your session token!

                            geeft.setUserLocation(document.getString("location"));
                            geeft.setGeeftDescription(document.getString("description"));
                            geeft.setUserProfilePic(document.getString("profilePic"));
                            geeft.setUserCap(document.getString("cap"));
                            geeft.setGeeftTitle(document.getString("title"));
                            mGeeftList.add(geeft);
                        } catch (com.baasbox.android.BaasException ex) {
                            Toast.makeText(mContext, "Exception during loading!",
                                    Toast.LENGTH_LONG).show();
                            return false;
                        }
                    } else if (result.isFailed()) {
                        Log.e(TAG, "Error when retrieve links: " + result.error());
                        return false;
                    }
                }
            }
            return true;
        }
        else{
            Log.e(TAG, "Error when retrieve links");
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result);
    }
}
