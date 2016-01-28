package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;

import java.util.List;

import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 07/01/16.
 * Task for populating GeeftItem cards
 */
public class BaaSFeedImageTask extends AsyncTask<Void,Void,Boolean> {
    Context mContext;
    List<Geeft> mGeeftList;
    TaskCallbackBoolean mCallback;
    GeeftItemAdapter mGeeftItemAdapter;
    boolean result;

    public BaaSFeedImageTask(Context context, List<Geeft> feedItems, GeeftItemAdapter Adapter, TaskCallbackBoolean callback) {
        mContext = context;
        mGeeftList = feedItems;
        mCallback = callback;
        mGeeftItemAdapter = Adapter;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        Geeft mGeeft;
        BaasQuery.Criteria paginate = BaasQuery.builder()
                .orderBy("_creation_date desc").criteria();
        BaasResult<List<BaasDocument>> baasResult = BaasDocument.fetchAllSync("geeft", paginate);
        if (baasResult.isSuccess()) {
            try {
                for (BaasDocument e : baasResult.get()) {
                    mGeeft = new Geeft();
                    mGeeft.setId(e.getId());
                    mGeeft.setUsername(""+mGeeftList.size());
                    mGeeft.setGeeftImage(e.getString("image"));
                    mGeeft.setGeeftDescription(e.getString("description"));
                    mGeeft.setUserProfilePic(e.getString("profilePic"));
                    mGeeft.setTimeStamp(e.getString("timeStamp"));
                    mGeeft.setUserLocation(e.getString("location"));
                    mGeeft.setGeeftTitle(e.getString("title"));

                    mGeeftList.add(0,mGeeft);
                    mGeeftItemAdapter.notifyItemInserted(0);
                    result = true;
                }
            } catch (com.baasbox.android.BaasException ex) {
                Log.e("LOG", "Deal with error n " + BaaSFeedImageTask.class + " " + ex.getMessage());
                Toast.makeText(mContext, "Exception during loading!", Toast.LENGTH_LONG).show();
            }
        } else if (baasResult.isFailed()) {
            Log.e("LOG", "Deal with error: " + baasResult.error().getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result);
    }
}
