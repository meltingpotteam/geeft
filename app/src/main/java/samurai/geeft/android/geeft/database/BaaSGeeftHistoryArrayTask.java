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

import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by ugookeadu on 02/02/16.
 */
public class BaaSGeeftHistoryArrayTask extends AsyncTask<Void,Void,Boolean> {

    private final String TAG =""+this.getClass().getName();
    Context mContext;
    List<Geeft> mGeeftList;
    TaskCallbackBooleanToken mCallback;
    String mGeeftId;
    boolean result;
    boolean stop = true;
    String mCollection;
    private int mResultToken;
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    //-------------------

    public BaaSGeeftHistoryArrayTask(Context context, List<Geeft> feedItems, String geeftId,
                                     String collection,
                                     TaskCallbackBooleanToken callback) {
        mContext = context;
        mGeeftList = feedItems;
        mCallback = callback;
        mGeeftId = geeftId;
        result = true;
        mCollection = collection;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        BaasQuery.Criteria paginate = BaasQuery.builder().where("id ="+mGeeftId).criteria();
        BaasResult<BaasDocument> baasResult = BaasDocument.fetchSync(mCollection, mGeeftId);
        if (baasResult.isSuccess()) {
            try {
                BaasDocument e = baasResult.get();
                Log.d("DOCU", e.toJson().toString());
                Log.d("HISTORY", e.getString("image"));
                Geeft mGeeft = new Geeft();
                mGeeft.setId(e.getId());
                mGeeft.setUsername(e.getString("name"));
                mGeeft.setGeeftImage(e.getString("image") + BaasUser.current().getToken());
                mGeeft.setGeeftDescription(e.getString("description"));
                mGeeft.setUserProfilePic(e.getString("profilePic"));
                mGeeft.setUserLocation(e.getString("location"));
                mGeeft.setGeeftTitle(e.getString("title"));
                mGeeftList.add(0,mGeeft);
                createGeeftStoryArray(e,mGeeftId+"");
                result = true;
            }catch (BaasInvalidSessionException ise){
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;

            }catch (com.baasbox.android.BaasException ex) {
                Log.e("CLASS", "Deal with error n " + BaaSGeeftHistoryArrayTask.class + " " + ex.getMessage());
                Toast.makeText(mContext, "Exception during loading!", Toast.LENGTH_LONG).show();
                mResultToken = RESULT_FAILED;
                return false;
            }

        } else if (baasResult.isFailed()) {
            if(baasResult.error() instanceof BaasInvalidSessionException){
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;
            }
            else {
                Log.e("CLASS", "Deal with error: " + baasResult.error().getMessage());
                mResultToken = RESULT_FAILED;
                return false;
            }
        }
        return result;
    }

    private void createGeeftStoryArray(BaasDocument e, String mPreviousGeeftId){
        do {
            BaasQuery.Criteria paginate = BaasQuery.builder().
                    where("out.id like '" + mPreviousGeeftId+"'").criteria();
            BaasResult<List<BaasLink>> baasResult = BaasLink.fetchAllSync(TagsValue.LINK_GEEFT_STORY, paginate);
            if (baasResult.isSuccess()) {
                try {
                    List<BaasLink> list = baasResult.get();
                    Log.d(TAG,""+list.size());
                    if (list.size() > 0) {
                        BaasLink link = baasResult.get().get(0);
                        BaasDocument doc = (BaasDocument) link.out();
                        Geeft mGeeft = new Geeft();
                        mGeeft.setId(doc.getId());
                        mGeeft.setUsername(doc.getString("name"));
                        mGeeft.setGeeftImage(doc.getString("image")+BaasUser.current().getToken());
                        mGeeft.setGeeftDescription(doc.getString("description"));
                        mGeeft.setUserProfilePic(doc.getString("profilePic"));
                        mGeeft.setUserLocation(doc.getString("location"));
                        mGeeft.setGeeftTitle(doc.getString("title"));
                        mGeeftList.add(mGeeft);
                        mPreviousGeeftId = doc.getId();
                        stop = false;
                    }
                    else
                        stop=true;
                }catch (BaasInvalidSessionException ise){
                    mResultToken = RESULT_SESSION_EXPIRED;
                }catch (com.baasbox.android.BaasException ex) {
                    Log.e("CLASS2", "Deal with error n " + BaaSGeeftHistoryArrayTask.class + " " + ex.getMessage());
                    Toast.makeText(mContext, "Exception during loading!", Toast.LENGTH_LONG).show();
                    mResultToken = RESULT_FAILED;
                }
            } else if (baasResult.isFailed()) {
                if(baasResult.error() instanceof BaasInvalidSessionException){
                    mResultToken = RESULT_SESSION_EXPIRED;
                }
                else {
                    mResultToken = RESULT_FAILED;
                    Log.e("CLASS2", "Deal with error: " + baasResult.error().getMessage());
                }
            }
        }while (!stop);
    }
    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result,mResultToken);
    }
}
