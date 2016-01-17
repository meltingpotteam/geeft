package samurai.geeft.android.geeft.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;

import java.util.List;

import samurai.geeft.android.geeft.data.FeedItem;

/**
 * Created by ugookeadu on 07/01/16.
 */
public class BaaSFeedImageTask extends AsyncTask<Void,Void,Boolean> {
    Context mContext;
    List<FeedItem> mFeedItems;
    TaskCallbackBoolean mCallback;

    public BaaSFeedImageTask(Context context, List<FeedItem> feedItems, TaskCallbackBoolean callback) {
        mContext = context;
        mFeedItems = feedItems;
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        FeedItem item;
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
                    item = new FeedItem();
                    item.setId(e.getId());
                    item.setName(e.getString("name"));
                    item.setImge(e.getString("image"));
                    item.setStatus(e.getString("description"));
                    item.setProfilePic(e.getString("profilePic"));
                    item.setTimeStamp(e.getString("timeStamp"));
                    item.setUrl(e.getString("url"));
                    item.setLocation(e.getString("location"));
                    item.setTitle(e.getString("title"));
                    mFeedItems.add(item);
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
