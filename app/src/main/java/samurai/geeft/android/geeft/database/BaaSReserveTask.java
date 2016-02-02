package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasResult;
import com.baasbox.android.RequestOptions;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
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
    private TaskCallbackBoolean mCallback;
    private BaasDocument mDocUser;

    public BaaSReserveTask(Context context, String docUserId, Geeft item, TaskCallbackBoolean callback) {
        mContext = context;
        mDocUserId = docUserId;
        mItem = item;
        mCallback = callback;
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
        BaasResult<BaasDocument> resDoc = BaasDocument.fetchSync("linkable_users", mDocUserId);
        if (resDoc.isSuccess()) { //fetch DocUser linked to USER (we have not the possibility
            // to directly link to the user)
            Log.d(TAG, "resDoc is success");
            mDocUser = resDoc.value();
        }
        else {
            Log.e(TAG, "Fatal error while retrieve DocUser");
            //Toast.makeText(mContext, "E' accaduto un errore", Toast.LENGTH_LONG).show();
            return false;

        }
        if (mItem.isSelected()) { // Selected button, I reserve (create the link FROM doc of user TO geeft)
             //This id document associated to Current User
                BaasResult<BaasLink> resLink = BaasLink.createSync("reserve", mItem.getId(), mDocUser.getId());
                //TODO : BUG: create(linkname,source-id,dest-id) but source-id is in and dest-id is out.
                if (resLink.isSuccess()) { //Link created
                    BaasLink value = resLink.value();
                    Log.d(TAG, "Link id is :" + value.getId() + " and docUser id is: " + mDocUser.getId());
                    Log.d(TAG,"Link IN is: " + value.in().getId().equals(mItem.getId()) +
                            " OUT is: " + value.out().getId().equals(mDocUser.getId()));
                    mDocUser.put("link_id", value.getId()); //TO CHANGE WITH JSONObject Array to store all of the links
                    BaasResult<BaasDocument> resResultSaved = mDocUser.saveSync();
                    if (resResultSaved.isSuccess()) { //linkId information stored in docUser
                        Log.d(TAG, "Link Id saved in DocUser");
                        //Toast.makeText(mContext, "Ti sei prenotato con successo", Toast.LENGTH_LONG).show();
                        return true;
                    } else {
                        Log.e(TAG, "Error with save Link id in DocUser");
                        //Toast.makeText(mContext, "E' accaduto un errore", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
                else {
                    Log.e(TAG, "Error with creation link");
                    //Toast.makeText(mContext, "E' accaduto un errore", Toast.LENGTH_LONG).show();
                    return false;
                }
        } else { //Deselected button,delete the link reserve ,then clone the link renamed in wasReserved
            BaasResult<Void> resDelLink = BaasLink.withId(mItem.getLinkId()).deleteSync();
            if (resDelLink.isSuccess()) {
                Log.d("TAG", "Link has been deleted");
                BaasResult<BaasLink> resLink = BaasLink.createSync("wasReserved", mDocUser.getId(), mItem.getId());
                if (resLink.isSuccess()) { //Link created
                    Log.d(TAG, "Link cloned");
                    return true;
                }
                else{
                    Log.e(TAG, "Link NOT cloned");
                    return false;
                }
            }
            else{
                Log.e(TAG, "Link NOT deleted");
                return false;
            }
        }

    }



    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result);
    }
}
