package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import java.util.List;

import samurai.geeft.android.geeft.interfaces.TaskCallbackDeletion;
import samurai.geeft.android.geeft.interfaces.TaskCallbackExchange;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.TagsValue;


/**
 * Created by daniele on 13/03/16.
 */
public class BaaSExchangeCompletedTask extends AsyncTask<Void,Void,Boolean> {
    private final String TAG = getClass().getSimpleName();
    private boolean mIamGeefter;
    private Context mContext;
    private Geeft mGeeft;
    private TaskCallbackExchange mCallback;
    private int mResultToken;
    private BaasDocument mDocumentGeeft;
    private String mDocUserId;

    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;

    //-------------------

    public BaaSExchangeCompletedTask(Context context, Geeft geeft,boolean iAmGeefter,
                                     TaskCallbackExchange callback) {
        mContext = context;
        mGeeft = geeft;
        mIamGeefter = iAmGeefter;
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {

        BaasUser currentUser = BaasUser.current();
        if (currentUser != null) {
            mDocUserId = currentUser.getScope(BaasUser.Scope.PRIVATE).getString("doc_id");
            if (fetchGeeft()) {
                boolean geefterHasExchanged = mDocumentGeeft.getBoolean(TagsValue.FLAG_GEEFTER_HAS_EXCHANGED);
                boolean geeftedHasExchanged = mDocumentGeeft.getBoolean(TagsValue.FLAG_GEEFTED_HAS_EXCHANGED);
                if (geeftedHasExchanged && geeftedHasExchanged) {
                    return true;
                } //Already created,skip all

                if (mIamGeefter) {
                    if (!geefterHasExchanged) {
                        if (updateNumberOfGiven()) {
                            return true;
                        }
                    }
                }
                else{
                    if(!geeftedHasExchanged){
                        if (createLinkReceived()) {
                            if(deleteLinkAssigned()){
                                if(updateNumberOfReceived()){
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.exchangeCompleted(result, mResultToken);
    }

    private boolean fetchGeeft() {
        BaasResult<BaasDocument> resGeeft = BaasDocument.fetchSync(TagsValue.COLLECTION_GEEFT, mGeeft.getId());
        if (resGeeft.isSuccess()) {
            mDocumentGeeft = resGeeft.value();
            return true;
        } else {
            if (resGeeft.error() instanceof BaasInvalidSessionException) {
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            } else {
                Log.e(TAG, "Error while fetching geeft:" + resGeeft.error());
                mResultToken = RESULT_FAILED;
                return false;
            }
        }
    }

    private boolean updateNumberOfGiven(){
        BaasUser currentUser = BaasUser.current();
        float n_given = currentUser.getScope(BaasUser.Scope.REGISTERED).getFloat(TagsValue.SCOPE_VALUE_N_GIVEN);
        currentUser.getScope(BaasUser.Scope.REGISTERED).put(TagsValue.SCOPE_VALUE_N_GIVEN,n_given+1);
        BaasResult<BaasUser> resSaveUser = currentUser.saveSync();
        if(resSaveUser.isSuccess()){
            mDocumentGeeft.put(TagsValue.FLAG_GEEFTER_HAS_EXCHANGED,true);
            BaasResult<BaasDocument> resSaveDocGeeft = mDocumentGeeft.saveSync();
            if(resSaveDocGeeft.isSuccess()){
                return true;
            }else {
                if (resSaveDocGeeft.error() instanceof BaasInvalidSessionException) {
                    mResultToken = RESULT_SESSION_EXPIRED;
                    return false;
                } else {
                    Log.e(TAG, "Error while updating document of geeft:" + resSaveDocGeeft.error());
                    mResultToken = RESULT_FAILED;
                    return false;
                }
            }
        }
        else {
            if (resSaveUser.error() instanceof BaasInvalidSessionException) {
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            } else {
                Log.e(TAG, "Error while updating document of geeft:" + resSaveUser.error());
                mResultToken = RESULT_FAILED;
                return false;
            }
        }
    }

    private boolean createLinkReceived(){
        BaasResult<BaasLink> resCreateLink = BaasLink.createSync(TagsValue.LINK_NAME_RECEIVED, mGeeft.getId(), mDocUserId);
        if(resCreateLink.isSuccess()){
            return true;
        }
        else {
            if (resCreateLink.error() instanceof BaasInvalidSessionException) {
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            } else {
                Log.e(TAG, "Error while creating link received:" + resCreateLink.error());
                mResultToken = RESULT_FAILED;
                return false;
            }
        }
    }

    private boolean deleteLinkAssigned(){
        BaasQuery.Criteria query =BaasQuery.builder().where("out.id like '" + mGeeft.getId() + "'" ).criteria();
        BaasResult<List<BaasLink>> resLinks = BaasLink.fetchAllSync(TagsValue.LINK_NAME_ASSIGNED, query);
        if(resLinks.isSuccess()){
            List<BaasLink> links = resLinks.value();
            Log.d(TAG,"Expected size of links is one: " + links.size());
            for (BaasLink link : links) {
                BaasResult resDelLink = link.deleteSync();
                if(resDelLink.isSuccess()){
                    return true;
                }
                else {
                    if (resLinks.error() instanceof BaasInvalidSessionException) {
                        mResultToken = RESULT_SESSION_EXPIRED;
                        return false;
                    } else {
                        Log.e(TAG, "Error while deleting links 'assigned':" + resLinks.error());
                        mResultToken = RESULT_FAILED;
                        return false;
                    }
                }
            }
        }
        else {
            if (resLinks.error() instanceof BaasInvalidSessionException) {
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            } else {
                Log.e(TAG, "Error while fetching links 'assigned':" + resLinks.error());
                mResultToken = RESULT_FAILED;
                return false;
            }
        }
        return false;
    }

    private boolean updateNumberOfReceived(){
        BaasUser currentUser = BaasUser.current();
        float n_received = currentUser.getScope(BaasUser.Scope.REGISTERED).getFloat(TagsValue.SCOPE_VALUE_N_RECEIVED);
        currentUser.getScope(BaasUser.Scope.REGISTERED).put(TagsValue.SCOPE_VALUE_N_GIVEN,n_received+1);
        BaasResult<BaasUser> resSaveUser = currentUser.saveSync();
        if(resSaveUser.isSuccess()){
            mDocumentGeeft.put(TagsValue.FLAG_GEEFTED_HAS_EXCHANGED,true);
            BaasResult<BaasDocument> resSaveDocGeeft = mDocumentGeeft.saveSync();
            if(resSaveDocGeeft.isSuccess()){
                return true;
            }else {
                if (resSaveDocGeeft.error() instanceof BaasInvalidSessionException) {
                    mResultToken = RESULT_SESSION_EXPIRED;
                    return false;
                } else {
                    Log.e(TAG, "Error while updating document of geeft:" + resSaveDocGeeft.error());
                    mResultToken = RESULT_FAILED;
                    return false;
                }
            }
        }
        else {
            if (resSaveUser.error() instanceof BaasInvalidSessionException) {
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            } else {
                Log.e(TAG, "Error while updating document of geeft:" + resSaveUser.error());
                mResultToken = RESULT_FAILED;
                return false;
            }
        }
    }
}

