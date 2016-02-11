package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import java.util.List;

import samurai.geeft.android.geeft.adapters.GeeftStoryListAdapter;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 09/02/16.
 */
public class BaasRecievedGeeftTask extends AsyncTask<Void,Void,Boolean> {
    private final String TAG = getClass().getName();
    Context mContext;
    List<Geeft> mGeeftList;
    TaskCallbackBoolean mCallback;
    GeeftStoryListAdapter mGeeftStoryListAdapter;

    public BaasRecievedGeeftTask(Context context, List<Geeft> feedItems,
                                 GeeftStoryListAdapter Adapter, TaskCallbackBoolean callback) {
        mContext = context;
        mGeeftList = feedItems;
        mCallback = callback;
        mGeeftStoryListAdapter = Adapter;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if(BaasUser.current()==null)
            return false;
        String docId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id");
        BaasQuery.Criteria query =BaasQuery.builder().where("in.id like '" + docId + "'" ).criteria();

        BaasResult<List<BaasLink>> resLinks = BaasLink.fetchAllSync("recieved", query);
        List<BaasLink> links;
        if(resLinks.isSuccess()){
            links = resLinks.value();
            Log.d("Recieved",getClass().getName());
            for (BaasLink link : links){
                BaasResult<BaasDocument> result= BaasDocument.fetchSync("geeft", link.in().getId());
                if(result.isSuccess()){
                    Log.d(TAG, "Your links are here: " + links.size());
                    try {
                        BaasDocument document = result.get().asDocument();
                        Geeft geeft = new Geeft();
                        geeft.setGeeftImage(document.getString("image")+BaasUser.current().getToken());
                        geeft.setId(document.getId());
                        mGeeftList.add(geeft);
                    }catch (com.baasbox.android.BaasException ex){
                        Toast.makeText(mContext, "Exception during loading!",
                                Toast.LENGTH_LONG).show();
                        return false;
                    }
                }else if (result.isFailed()){
                    Log.e(TAG, "Error when retrieve links");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result);
    }
}
