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

import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanGeeft;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by danybr-dev on 18/02/16.
 */
public class BaaSBackgroundNewAssignment extends AsyncTask<Void,Void,Boolean> {

    private final String TAG = getClass().getName();
    private Context mContext;
    private TaskCallbackBooleanGeeft mCallback;
    private String mlinkNameQuery;
    private Geeft mGeeft;

    /**
     * NOTA: TODO: USIAMO SOLO PER LA SECONDA MILESTONE!!!
     */


    /**
     * Constructor to create an object Geeft to send to Baasbox TODO: add the field 'expiration time'
     **/
    public BaaSBackgroundNewAssignment(Context context, TaskCallbackBooleanGeeft callback) {
        mContext = context;
        mlinkNameQuery = "assigned";
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        if (BaasUser.current() != null) {
            String docUserId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id");
            BaasQuery.Criteria query = BaasQuery.builder().where("out.id like '" + docUserId + "'").criteria();
            Log.d(TAG, "doc_id are: " + docUserId);
            // PROBLEMA GRAVE: QUANDO FAI LA QUERY,OUT È L'UTENTE (DocUserId) E IN È IL GEEFT
            //                 QUANDO PRENDI I LINK,OUT() È IL GEEFT,IN() È L'UTENTE
            if (mlinkNameQuery != null || !mlinkNameQuery.equals("")) {
                BaasResult<List<BaasLink>> resLinks = BaasLink.fetchAllSync(mlinkNameQuery, query);
                List<BaasLink> links;
                if (resLinks.isSuccess()) {
                    links = resLinks.value();
                    if(links.size() == 0) {
                        mGeeft = null;
                        return false;
                    }
                    else {
                        for (BaasLink link : links) {
                            BaasResult<BaasDocument> result = BaasDocument.fetchSync("geeft", link.out().getId());
                            Log.d(TAG, "geeftID: " + link.in().getId());
                            if (result.isSuccess()) {
                                Log.d(TAG, "Your links are here: " + links.size());
                                try {
                                    BaasDocument document = result.get().asDocument();

                                    mGeeft.setGeeftImage(document.getString("image") + BaasUser.current().getToken());
                                    mGeeft.setId(document.getId());
                                    mGeeft.setUsername(document.getString("name"));
                                    //Append ad image url your session token!

                                    mGeeft.setUserLocation(document.getString("location"));
                                    mGeeft.setGeeftDescription(document.getString("description"));
                                    mGeeft.setUserProfilePic(document.getString("profilePic"));
                                    mGeeft.setUserCap(document.getString("cap"));
                                    mGeeft.setGeeftTitle(document.getString("title"));
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
                }
            } else {
                Log.e(TAG, "Error when retrieve links");
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result,mGeeft);
    }
}