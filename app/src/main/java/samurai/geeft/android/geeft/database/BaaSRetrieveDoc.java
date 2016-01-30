package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasResult;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by daniele on 20/01/16.
 */
public class BaaSRetrieveDoc extends AsyncTask<Void,Void,Boolean> {
    private final String TAG ="BaaSRetrieveDoc";
    Context mContext;
    String mDocId;
    Geeft mItem;
    TaskCallbackBoolean mCallback;
    private BaasDocument mDocUser;

    public BaaSRetrieveDoc(Context context, String docId, Geeft item, TaskCallbackBoolean callback) {
        mContext = context;
        mDocId = docId;
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
        Log.d(TAG,"è selezionato: " + mItem.isSelected());
        if(!mItem.isSelected()) {// if not selected,add a link FROM doc of user TO geeft

            Log.d(TAG,"Entrato in notSelectedItem");
            BaasResult<BaasDocument> resDoc = BaasDocument.fetchSync("linkable_users",mDocId);
            if(resDoc.isSuccess()) {
                Log.d(TAG,"resDoc is success");
                mDocUser = resDoc.value(); //Document associated to Current User
            }
            else{

                Log.e(TAG, "resDoc fail");
            }
            BaasResult<BaasLink> resLink = BaasLink.createSync("reserve", mDocUser.getId(), mItem.getId());
            if(resLink.isSuccess()){
                BaasLink value = resLink.value();
                Log.d(TAG, "Link id is :" + value.getId() + " and docUser id is: " + mDocUser.getId());
                mDocUser.put("link_id", value.getId()); //TO CHANGE WITH JSONObject Array to store all of the links
                mDocUser.saveSync();
                return true;
            }
            else{
                Log.e(TAG, "Fatal error while creating link");
                return false;
            }

        }else{// if selected,remove the link FROM doc of user TO this geeft (item)
            //Delete link
            Log.d(TAG,"Now button is not selected");
            return true;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result);
    }
}
