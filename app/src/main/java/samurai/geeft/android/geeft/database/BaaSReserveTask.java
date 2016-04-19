package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonArray;

import java.util.List;

import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanHolderToken;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by danybr-dev on 20/01/16.
 *
 */
public class BaaSReserveTask extends AsyncTask<Void,Void,Boolean> {
    private final String TAG ="BaaSReserveTask";
    private Context mContext;
    private String mDocUserId;
    private Geeft mItem;
    private TaskCallbackBooleanHolderToken mCallback;
    private BaasDocument mDocUser;
    private JsonArray mJSONUserLinks;
    private GeeftItemAdapter.GeeftViewHolder mHolder;
    private long submits_active;
    private int mResultToken;
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    //-------------------

    public BaaSReserveTask(Context context, String docUserId, Geeft item,GeeftItemAdapter.GeeftViewHolder holder,
                           TaskCallbackBooleanHolderToken callback) {
        mContext = context;
        mDocUserId = docUserId;
        mItem = item;
        mCallback = callback;
        mHolder = holder;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        //BaasLink.create("reserve", BaasUser.current().getName(),item.getId(),RequestOptions.DEFAULT,new BaasHandler<BaasLink>() {
               /* BaasResult linkResult = BaasLink.createSync("reserve", docId,item.getId());
                if(linkResult.isSuccess()){
                    Log.d(TAG,"Link created with id: ");
                }else{
                    Log.d(TAG,"error while linking");
                }*/
        //-----------------------
        BaasUser currentUser = BaasUser.current();
        Log.d(TAG,"CURRENT USER= "+currentUser.toString());
        if (mDocUserId==null){
            return false;
        }

        BaasResult<BaasDocument> resDoc = BaasDocument.fetchSync("linkable_users", mDocUserId);
        if (resDoc.isSuccess()) { //fetch DocUser linked to USER (we have not the possibility
            // to directly link to the user)
            Log.d(TAG, "resDoc is success");
            mDocUser = resDoc.value();
            mJSONUserLinks = mDocUser.getArray("prenoteLinks");
            //Log.d(TAG, "JSONDocUser is:" + mJSONUserLinks.toString());
        }
        else {
            if(resDoc.error() instanceof BaasInvalidSessionException){
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            }
            else {
                Log.e(TAG, "Fatal error while retrieve DocUser ="+resDoc.error());
                mResultToken = RESULT_FAILED;
                return false;
            }

        }
        if(currentUser != null) {
            if (mItem.isSelected()) { // Selected button, I reserve (create the link FROM doc of user TO geeft)
                //This id document associated to Current User
                BaasQuery.Criteria query = BaasQuery.builder().where("out.id like '" + mItem.getId()+ "'").
                        criteria();
                BaasResult<List<BaasLink>> assignedLink = BaasLink.fetchAllSync(
                        TagsValue.LINK_NAME_ASSIGNED,query);
                try {
                    if (assignedLink.get().size()!=0){
                        Log.d(TAG,"oggetto gia assegnato");
                        return true;
                    }
                } catch (BaasException e) {
                    e.printStackTrace();
                }

                BaasResult<BaasLink> resLink = BaasLink.createSync("reserve", mItem.getId(), mDocUser.getId());
                //TODO : BUG: create(linkname,source-id,dest-id) but source-id is in and dest-id is out.
                if (resLink.isSuccess()) { //Link created
                    BaasLink value = resLink.value();
                    Log.d(TAG, "Link id is :" + value.getId() + " and docUser id is: " + mDocUser.getId());
                    Log.d(TAG, "Link IN is: " + value.in().getId().equals(mItem.getId()) +
                            " OUT is: " + value.out().getId().equals(mDocUser.getId()));
                    //mDocUser.put("link_id", value.getId()); //TO CHANGE WITH JSONObject Array to store all of the links

                    mJSONUserLinks.add(value.getId());
                    //Log.d(TAG,mJSONUserLinks.toString());
                    mDocUser.put("prenoteLinks", mJSONUserLinks);
                    BaasResult<BaasDocument> resResultSaved = mDocUser.saveSync();
                    if (resResultSaved.isSuccess()) { //linkId information stored in docUser
                        Log.d(TAG, "Links Id saved in DocUser");
                        mItem.setReservedLinkId(value.getId()); //Set new link for referenced item
                        // assumed is null
                        //Toast.makeText(mContext, "Ti sei prenotato con successo", Toast.LENGTH_LONG).show();
                        submits_active = currentUser.getScope(BaasUser.Scope.REGISTERED).get("submits_active");
                        submits_active++; //Created link,increment currentUser's submits_active field
                        currentUser.getScope(BaasUser.Scope.REGISTERED).put("submits_active",submits_active);
                        BaasResult<BaasUser> resUser = currentUser.saveSync();
                        if(resUser.isSuccess()) {
                            return true;
                        }
                        else{
                            if(resUser.error() instanceof BaasInvalidSessionException){
                                mResultToken = RESULT_SESSION_EXPIRED;
                                return false;
                            }
                            else {
                                Log.e(TAG, "Cannot insert new valure of submits_active");
                                mResultToken = RESULT_FAILED;
                                return false;
                            }
                        }
                    } else {
                        if(resResultSaved.error() instanceof BaasInvalidSessionException) {
                            mResultToken = RESULT_SESSION_EXPIRED;
                            return false;
                        }else {
                            Log.e(TAG, "Error with save Link id in DocUser");
                            mResultToken = RESULT_FAILED;
                            return false;
                        }
                    }
                } else {
                    if(resLink.error() instanceof BaasInvalidSessionException) {
                        mResultToken = RESULT_SESSION_EXPIRED;
                        return false;
                    }else {
                        Log.e(TAG, "Error with creation link");
                        mResultToken = RESULT_FAILED;
                        return false;
                    }
                }
            } else { //Deselected button,delete the link reserve ,then clone the link renamed in wasReserved
                BaasResult<Void> resDelLink = BaasLink.withId(mItem.getReservedLinkId()).deleteSync();
                if (resDelLink.isSuccess()) {
                    Log.d("TAG", "Link has been deleted");
                    mItem.setReservedLinkId(""); //Link is null for referenced item
                    BaasResult<BaasLink> resLink = BaasLink.createSync("wasReserved", mItem.getId(), mDocUser.getId());
                    if (resLink.isSuccess()) { //Link created
                        Log.d(TAG, "Link cloned");
                        submits_active = currentUser.getScope(BaasUser.Scope.REGISTERED).get("submits_active");
                        submits_active = submits_active - 1; //Created link,decrement currentUser's submits_active field
                        currentUser.getScope(BaasUser.Scope.REGISTERED).put("submits_active",submits_active);
                        BaasResult<BaasUser> resUser = currentUser.saveSync();
                        if(resUser.isSuccess()) {
                            return true;
                        }
                        else{
                            if(resUser.error() instanceof BaasInvalidSessionException) {
                                mResultToken = RESULT_SESSION_EXPIRED;
                                return false;
                            }
                            else {
                                Log.e(TAG, "Cannot insert new value of submits_active");
                                mResultToken = RESULT_FAILED;
                                return false;
                            }
                        }
                    } else {
                        if(resLink.error() instanceof BaasInvalidSessionException) {
                            mResultToken = RESULT_SESSION_EXPIRED;
                            return false;
                        }
                        else {
                            Log.e(TAG, "Link NOT cloned");
                            mResultToken = RESULT_FAILED;
                            return false;
                        }
                    }
                } else {
                    if(resDelLink.error() instanceof BaasInvalidSessionException) {
                        mResultToken = RESULT_SESSION_EXPIRED;
                        return false;
                    }
                    else {
                        Log.e(TAG, "Link NOT deleted,error is:" + resDelLink.error());
                        mResultToken = RESULT_FAILED;
                        return false;
                    }
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
        mCallback.done(result,mHolder, mItem,mResultToken);
    }
}
