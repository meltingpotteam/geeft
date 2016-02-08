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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 07/01/16.
 * Task for populating GeeftItem cards
 * Update by danybr-dev on 17/01/16
 */
public class BaaSFeedImageTask extends AsyncTask<Void,Void,Boolean> {

    private static final String TAG ="BaaSFeedImageTask";
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
        Log.d(TAG,"Lanciato");
        Log.d(TAG,BaasUser.current().toString());
        String docId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id"); //retrieve doc_is attached at user
        //find all links with the doc_id (User id <--> doc id )
        Log.d(TAG,"Doc_id is: " + docId);
        //TODO: change when baasbox fix issue with BaasLink.create
        BaasQuery.Criteria query =BaasQuery.builder().where("in.id like '" + docId + "'" ).criteria();
        BaasResult<List<BaasLink>> resLinks = BaasLink.fetchAllSync("reserve", query);
        List<BaasLink> links;
        if(resLinks.isSuccess()){
            links = resLinks.value();
            Log.d(TAG, "Your links are here: " + links.size());
        }
        else{
            Log.e(TAG, "Error when retrieve links");
            return false; // Don't continue if we are in this case
        }
        BaasQuery.Criteria paginate = BaasQuery.builder()
                .orderBy("_creation_date asc").criteria();
        BaasResult<List<BaasDocument>> baasResult = BaasDocument.fetchAllSync("geeft", paginate);
        if (baasResult.isSuccess()) {
            try {
                for (BaasDocument e : baasResult.get()) {
                    mGeeft = new Geeft();
                    mGeeft.setId(e.getId());
                    mGeeft.setUsername(e.getString("name"));
                    mGeeft.setGeeftImage(e.getString("image") + BaasUser.current().getToken());
                    //Append ad image url your session token!

                    mGeeft.setGeeftDescription(e.getString("description"));
                    mGeeft.setUserProfilePic(e.getString("profilePic"));
                    mGeeft.setCreationTimeStamp(getCreationTimestamp(e));
                    mGeeft.setDeadLine(e.getString("deadline"));

//                    TODO verify the error; probably we need to erase and recharg all the object since i send another one field to baas
//                    mGeeft.setAutomaticSelection(e.getBoolean("automaticSelection"));
//                    mGeeft.setAllowCommunication(e.getBoolean("allowCommunication"));

                    mGeeft.setUserLocation(e.getString("location"));
                    mGeeft.setUserCap(e.getString("cap"));
                    mGeeft.setGeeftTitle(e.getString("title"));
                    for (BaasLink l : links) {
                        //Log.d(TAG,"out: " + l.out().getId() + " in: " + l.in().getId());
                        Log.d(TAG, "e id: " + e.getId() + " inId: " + l.in().getId());
                        //if(l.out().getId().equals(e.getId())){ //TODO: LOGIC IS THIS,but BaasLink.create have a bug
                        if (l.in().getId().equals(e.getId())) {
                            mGeeft.setIsSelected(true);// set prenoteButton selected (I'm already
                            // reserved)
                            mGeeft.setLinkId(l.getId());
                            Log.d(TAG, "link id is: " + l.getId());
                        }
                    }
                    mGeeftList.add(0, mGeeft);
                    result = true;
                }
                } catch (com.baasbox.android.BaasException ex) {
                    Log.e("LOG", "Deal with error n " + BaaSFeedImageTask.class + " " + ex.getMessage());
                    Toast.makeText(mContext, "Exception during loading!", Toast.LENGTH_LONG).show();
                    return false;
                }
            } else if (baasResult.isFailed()) {
                Log.e("LOG", "Deal with error: " + baasResult.error().getMessage());
            }
        return result;
    }

    private static String getCreationTimestamp(BaasDocument d){ //return timestamp of _creation_date of document
        //TODO: change data type from String to Double (also change it in geeftItemAdapter)
        String date = d.getCreationDate();
        //Log.d(TAG,"_creation_date is:" + date);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date creation_date = dateFormat.parse(date);
            return ""+creation_date.getTime(); //Convert timestamp in string
        }catch (java.text.ParseException e){
           Log.e(TAG,"ERRORE FATALE : " + e.toString());
        }
        return "";

    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result);
    }
}
