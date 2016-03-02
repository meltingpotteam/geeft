package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import samurai.geeft.android.geeft.interfaces.TaskCallbackDeletion;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by Daniele on 01/03/2016.
 */
public class BaaSDeleteGeeftTask extends AsyncTask<Void,Void,Boolean> {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private Geeft mItem;
    private TaskCallbackDeletion mCallback;
    private int mResultToken;
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;

    //-------------------

    public BaaSDeleteGeeftTask(Context context, Geeft item,
                               TaskCallbackDeletion callback) {
        mContext = context;
        mItem = item;
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {

        BaasUser currentUser = BaasUser.current();
        if(currentUser != null) {
            final String docUserId = currentUser.getScope(BaasUser.Scope.PRIVATE).getString("doc_id");
            Log.d(TAG,"Link id is:" + mItem.getDonatedLinkId());
            Log.d(TAG,"Geeft id is:" + mItem.getId());
            Log.d(TAG,"Geeft title is:" + mItem.getGeeftTitle());

            BaasResult<Void> resDelLink = BaasLink.withId(mItem.getDonatedLinkId()).deleteSync();
            if (resDelLink.isSuccess()) {
                Log.d(TAG, "Link has been deleted");
                mItem.setDonatedLinkId(""); //Link is null for referenced item
                BaasResult<BaasLink> resLink = BaasLink.createSync(TagsValue.LINK_NAME_WAS_DONATED, mItem.getId(), docUserId);
                if (resLink.isSuccess()) { //Link created
                    Log.d(TAG, "Link cloned");
                    BaasResult<BaasDocument> resDocToBeClosed = BaasDocument.fetchSync("geeft",mItem.getId());
                    if(resDocToBeClosed.isSuccess()){
                        BaasDocument docToBeClosed = resDocToBeClosed.value();
                        docToBeClosed.put("closed",true);
                        docToBeClosed.put("deleted",true);
                        BaasResult<BaasDocument> resClosed = docToBeClosed.saveSync();
                        if(resClosed.isSuccess()){
                            return true;
                        }
                        else{
                            Log.e(TAG,"Error when put close flag in docToBeClosed");
                            mResultToken = RESULT_FAILED;
                            return false;
                        }
                    }
                    else{
                        Log.e(TAG,"Error when retrieve docToBeClosed");
                        mResultToken = RESULT_FAILED;
                        return false;
                    }
                } else {
                    if (resLink.error() instanceof BaasInvalidSessionException) {
                        mResultToken = RESULT_SESSION_EXPIRED;
                        return false;
                    } else {
                        Log.e(TAG, "Link NOT cloned");
                        mResultToken = RESULT_FAILED;
                        return false;
                    }
                }
            } else {
                if (resDelLink.error() instanceof BaasInvalidSessionException) {
                    mResultToken = RESULT_SESSION_EXPIRED;
                    return false;
                } else {
                    Log.e(TAG, "Link NOT deleted,error is:" + resDelLink.error());
                    mResultToken = RESULT_FAILED;
                    return false;
                }
            }

        }
        else{ //currentUser is null
            Log.e(TAG,"User is NULL");
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.doneDeletion(result, mResultToken);
    }
}

