package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import java.util.List;

import samurai.geeft.android.geeft.adapters.NavigationDrawerItemAdapter;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanIntHolder;
import samurai.geeft.android.geeft.utilities.TagsValue;


/**
 * Created by danybr-dev on 13/04/16.
 */
public class BaaSFillNavigationDrawerCount extends AsyncTask<Void,Void,Boolean> {

    private final String TAG = getClass().getName();

    private Context mContext;
    private String mlinkNameQuery;
    private NavigationDrawerItemAdapter.ViewHolder mHolder;
    private int mPosition;
    private TaskCallbackBooleanIntHolder mCallback;
    private int mCount;
    private int mResultToken;
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    //-------------------

    public BaaSFillNavigationDrawerCount(Context context,String linkNameQuery,
            NavigationDrawerItemAdapter.ViewHolder holder,
            int position, TaskCallbackBooleanIntHolder callback) {

        mContext = context;
        mHolder = holder;
        mPosition = position;
        mCallback = callback;
        mlinkNameQuery = linkNameQuery;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (BaasUser.current() == null)
            return false;
        String docUserId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id");
        //Log.d(TAG, "doc_id are: " + docUserId);
        // PROBLEMA GRAVE: QUANDO FAI LA QUERY,IN È L'UTENTE (DocUserId) E OUT È IL GEEFT
        //                 QUANDO PRENDI I LINK,OUT() È L'UTENTE, IN() È IL GEEFT
        Log.d(TAG, "mNameLinkQuery is: " + mlinkNameQuery);
        if (mlinkNameQuery != null) { // mLinkNameQuery are only
            // "assigned" and "donated"
            BaasQuery.Criteria query;
            if(mlinkNameQuery.equals(TagsValue.LINK_NAME_DONATED)){
                query = BaasQuery.builder().where("in.id like '" + docUserId + "'"
                    + " and out.assigned = false").criteria();
            }
            else if(mlinkNameQuery.equals(TagsValue.LINK_NAME_ASSIGNED)){
                query = BaasQuery.builder().where("in.id like '" + docUserId + "'"
                        + " and out.taken = false").criteria();
            }
            else{
                query = BaasQuery.builder().where("in.id like '" + docUserId + "'").criteria();
            }
            BaasResult<List<BaasLink>> resLinks = BaasLink.fetchAllSync(mlinkNameQuery, query);
            List<BaasLink> links;
            if (resLinks.isSuccess()) {
                mCount = resLinks.value().size();
                Log.d(TAG,"mCount is: " + mCount);
                return true;
            } else {
                if (resLinks.error() instanceof BaasInvalidSessionException) {
                    mResultToken = RESULT_SESSION_EXPIRED;
                    return false;
                } else {
                    Log.e(TAG, "Error when retrieve links: " + resLinks.error());
                    mResultToken = RESULT_FAILED;
                    return false;
                }
            }
        }
        else{
            Log.e(TAG,"Linkname is null!");
            mResultToken = RESULT_OK;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result,mHolder,mCount, mResultToken);
    }
}
