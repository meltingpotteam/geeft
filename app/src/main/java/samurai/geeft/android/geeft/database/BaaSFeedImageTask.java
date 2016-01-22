package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;

import java.util.List;

import samurai.geeft.android.geeft.model.Geeft;

/**
 * Created by ugookeadu on 07/01/16.
 */
public class BaaSFeedImageTask extends AsyncTask<Void,Void,Boolean> {
    Context mContext;
    List<Geeft> mGeeftList;
    TaskCallbackBoolean mCallback;

    public BaaSFeedImageTask(Context context, List<Geeft> feedItems, TaskCallbackBoolean callback) {
        mContext = context;
        mGeeftList = feedItems;
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        Geeft mGeeft;
        BaasQuery.Criteria paginate = BaasQuery.builder()
                .orderBy("_creation_date").criteria();
        BaasResult<List<BaasDocument>> baasResult = BaasDocument.fetchAllSync("geeft", paginate);
        if (baasResult.isSuccess()) {
            Log.d("LOG", "Documents retrieved " + baasResult.toString());
            String name;
            try {
                for (BaasDocument e : baasResult.get()) {
                    name = e.getString("name");
                    Log.d("LOG", "Document retrieved " + name);
                    mGeeft = new Geeft();
                    mGeeft.setId(e.getId());
                    mGeeft.setUsername(e.getString("name"));
                    mGeeft.setGeeftImage(e.getString("image"));
                    mGeeft.setGeeftDescription(e.getString("description"));
                    mGeeft.setUserProfilePic(e.getString("profilePic"));
                    mGeeft.setTimeStamp(e.getString("timeStamp"));
                    mGeeft.setUserLocation(e.getString("location"));
                    mGeeft.setGeeftTitle(e.getString("title"));
                    mGeeftList.add(mGeeft);
                }
                return true;
            } catch (com.baasbox.android.BaasException ex) {
                Log.e("LOG", "Deal with error n " + BaaSFeedImageTask.class + " " + ex.getMessage());
                Toast.makeText(mContext, "Exception during loading!", Toast.LENGTH_LONG).show();
                return false;
            }
        } else if (baasResult.isFailed()) {
            Log.e("LOG", "Deal with error: " + baasResult.error());
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result);
    }
}
