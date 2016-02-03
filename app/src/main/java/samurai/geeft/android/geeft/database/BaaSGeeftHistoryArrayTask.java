package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;

import java.util.List;

import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 02/02/16.
 */
public class BaaSGeeftHistoryArrayTask extends AsyncTask<Void,Void,Boolean> {

    private final String TAG =""+this.getClass().getName();
    Context mContext;
    List<Geeft> mGeeftList;
    TaskCallbackBoolean mCallback;
    String mGeeftId;
    boolean result;
    boolean stop = true;

    public BaaSGeeftHistoryArrayTask(Context context, List<Geeft> feedItems, String geeftId,
                                     TaskCallbackBoolean callback) {
        mContext = context;
        mGeeftList = feedItems;
        mCallback = callback;
        mGeeftId = geeftId;
        result = true;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        BaasQuery.Criteria paginate = BaasQuery.builder().where("id ="+mGeeftId).criteria();

        BaasResult<BaasDocument> baasResult = BaasDocument.fetchSync("geeft", mGeeftId);
        if (baasResult.isSuccess()) {
            try {
                    BaasDocument e = baasResult.get();
                    Log.d("DOCU", e.toJson().toString());
                    Geeft mGeeft = new Geeft();
                    mGeeft.setId(e.getId());
                    mGeeft.setUsername(e.getString("name"));
                    mGeeft.setGeeftImage(e.getString("image"));
                    mGeeft.setGeeftDescription(e.getString("description"));
                    mGeeft.setUserProfilePic(e.getString("profilePic"));
                    mGeeft.setUserLocation(e.getString("location"));
                    mGeeft.setGeeftTitle(e.getString("title"));
                    mGeeftList.add(0,mGeeft);
                    createGeeftStoryArray(e,mGeeftId+"");
                    result = true;
            } catch (com.baasbox.android.BaasException ex) {
                Log.e("CLASS", "Deal with error n " + BaaSFeedImageTask.class + " " + ex.getMessage());
                Toast.makeText(mContext, "Exception during loading!", Toast.LENGTH_LONG).show();
                return false;
            }
        } else if (baasResult.isFailed()) {
            Log.e("CLASS", "Deal with error: " + baasResult.error().getMessage());
        }
        return result;
    }

    private void createGeeftStoryArray(BaasDocument e, String mPreviousGeeftId){
        do {
            BaasQuery.Criteria paginate = BaasQuery.builder().
                    where("out.id like '" + mPreviousGeeftId+"'").criteria();
            BaasResult<List<BaasLink>> baasResult = BaasLink.fetchAllSync("geeft_story", paginate);
            if (baasResult.isSuccess()) {
                try {
                    List<BaasLink> list = baasResult.get();
                    if (list.size() > 0) {
                        BaasLink link = baasResult.get().get(0);
                        BaasDocument doc = (BaasDocument) link.out();
                        Geeft mGeeft = new Geeft();
                        mGeeft.setId(e.getId());
                        mGeeft.setUsername(e.getString("name"));
                        mGeeft.setGeeftImage(e.getString("image"));
                        mGeeft.setGeeftDescription(e.getString("description"));
                        mGeeft.setUserProfilePic(e.getString("profilePic"));
                        mGeeft.setUserLocation(e.getString("location"));
                        mGeeft.setGeeftTitle(e.getString("title"));
                        mGeeftList.add(mGeeft);
                        mPreviousGeeftId = e.getId();
                        stop = false;
                    }
                } catch (com.baasbox.android.BaasException ex) {
                    Log.e("CLASS2", "Deal with error n " + BaaSFeedImageTask.class + " " + ex.getMessage());
                    Toast.makeText(mContext, "Exception during loading!", Toast.LENGTH_LONG).show();
                }
            } else if (baasResult.isFailed()) {
                Log.e("CLASS2", "Deal with error: " + baasResult.error().getMessage());
            }
        }while (!stop);
    }
    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result);
    }
}
