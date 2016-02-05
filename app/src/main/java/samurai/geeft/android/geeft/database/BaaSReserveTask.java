package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestOptions;
import com.baasbox.android.json.JsonArray;

import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanHolder;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by danybr-dev on 20/01/16.
 *
 */
public class BaaSReserveTask extends AsyncTask<Void,Void,Boolean> {
    private final String TAG ="BaaSReserveTask";
    private Context mContext;
    private String mDocUserId;
    private Geeft mItem;
    private TaskCallbackBooleanHolder mCallback;
    private BaasDocument mDocUser;
    private JsonArray mJSONUserLinks;
    private GeeftItemAdapter.ViewHolder mHolder;
    private long submits_active;

    public BaaSReserveTask(Context context, String docUserId, Geeft item,GeeftItemAdapter.ViewHolder holder,
                           TaskCallbackBooleanHolder callback) {
        mContext = context;
        mDocUserId = docUserId;
        mItem = item;
        mCallback = callback;
        mHolder = holder;
        Log.d(TAG,"Lanciato AsyncTask");
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

        BaasResult<BaasDocument> resDoc = BaasDocument.fetchSync("linkable_users", mDocUserId);
        if (resDoc.isSuccess()) { //fetch DocUser linked to USER (we have not the possibility
            // to directly link to the user)
            Log.d(TAG, "resDoc is success");
            mDocUser = resDoc.value();
            mJSONUserLinks = mDocUser.getArray("prenoteLinks");
            //Log.d(TAG, "JSONDocUser is:" + mJSONUserLinks.toString());
        }
        else {
            Log.e(TAG, "Fatal error while retrieve DocUser");
            //Toast.makeText(mContext, "E' accaduto un errore", Toast.LENGTH_LONG).show();
            return false;

        }
        if(currentUser != null) {
            if (mItem.isSelected()) { // Selected button, I reserve (create the link FROM doc of user TO geeft)
                //This id document associated to Current User
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
                        mItem.setLinkId(value.getId()); //Set new link for referenced item
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
                            Log.e(TAG,"Cannot insert new valure of submits_active");
                            return false;
                        }
                    } else {
                        Log.e(TAG, "Error with save Link id in DocUser");
                        //Toast.makeText(mContext, "E' accaduto un errore", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else {
                    Log.e(TAG, "Error with creation link");
                    //Toast.makeText(mContext, "E' accaduto un errore", Toast.LENGTH_LONG).show();
                    return false;
                }
            } else { //Deselected button,delete the link reserve ,then clone the link renamed in wasReserved
                BaasResult<Void> resDelLink = BaasLink.withId(mItem.getLinkId()).deleteSync();
                if (resDelLink.isSuccess()) {
                    Log.d("TAG", "Link has been deleted");
                    mItem.setLinkId(""); //Link is null for referenced item
                    BaasResult<BaasLink> resLink = BaasLink.createSync("wasReserved", mItem.getId(), mDocUser.getId());
                    if (resLink.isSuccess()) { //Link created
                        Log.d(TAG, "Link cloned");
                        submits_active = currentUser.getScope(BaasUser.Scope.REGISTERED).get("submits_active");
                        submits_active--; //Created link,decrement currentUser's submits_active field
                        currentUser.getScope(BaasUser.Scope.REGISTERED).put("submits_active",submits_active);
                        BaasResult<BaasUser> resUser = currentUser.saveSync();
                        if(resUser.isSuccess()) {
                            return true;
                        }
                        else{
                            Log.e(TAG,"Cannot insert new valure of submits_active");
                            return false;
                        }
                    } else {
                        Log.e(TAG, "Link NOT cloned");
                        return false;
                    }
                } else {
                    Log.e(TAG, "Link NOT deleted,error is:" + resDelLink.error());
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
        mCallback.done(result,mHolder, mItem);
    }
}
