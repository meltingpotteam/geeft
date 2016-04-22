package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.adapters.StoryItemAdapter;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 14/03/16.
 */
public class BaaSSearchTask extends BaaSCheckTask{

    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;

    private static final String TAG ="BaaSGeeftItemTask";
    private final String mCollection;
    Context mContext;
    List<Geeft> mGeeftList;
    TaskCallbackBooleanToken mCallback;
    GeeftItemAdapter mGeeftItemAdapter;
    StoryItemAdapter mStoryItemAdapter;
    boolean result;
    String mSearchQuery;

    public BaaSSearchTask(Context context, List<Geeft> feedItems, GeeftItemAdapter Adapter
            , String query, String collection, TaskCallbackBooleanToken callback) {
        mContext = context;
        mGeeftList = feedItems;
        mCallback = callback;
        mGeeftItemAdapter = Adapter;
        mGeeftList = feedItems;
        mSearchQuery = query;
        mCollection = collection;
    }

    public BaaSSearchTask(Context context, List<Geeft> feedItems, StoryItemAdapter Adapter
            , String query, String collection, TaskCallbackBooleanToken callback) {
        mContext = context;
        mGeeftList = feedItems;
        mCallback = callback;
        mStoryItemAdapter = Adapter;
        mGeeftList = feedItems;
        mSearchQuery = query;
        mCollection = collection;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Geeft mGeeft;
        Log.d(TAG, BaasUser.current().toString());
        //retrieve doc_is attached at user
        String docId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id");
        //find all links with the doc_id (User id <--> doc id )
        Log.d(TAG, "Doc_id is: " + docId);
        //TODO: change when baasbox fix issue with BaasLink.create
        BaasQuery.Criteria query = BaasQuery.builder().where("in.id like '" + docId + "'").
                criteria();
        BaasResult<List<BaasLink>> resLinks = BaasLink.fetchAllSync("reserve", query);
        List<BaasLink> links;
        if (checkError(resLinks)) {
            links = resLinks.value();
            Log.d(TAG, "Your links are here: " + links.size());
            BaasUser currentUser = BaasUser.current();
            BaasUser.current().getScope(BaasUser.Scope.REGISTERED)
                    .put("submits_active", links.size());
            BaasResult<BaasUser> resUser = currentUser.saveSync();
            if (resUser.isSuccess()) {
                BaasQuery.Criteria paginate;
                if(mGeeftList.isEmpty()) {
                    if(mCollection.equals("story")){
                        paginate = BaasQuery.builder()
                                .orderBy("_creation_date asc").criteria();
                    } else {
                        paginate = BaasQuery.builder()
                                .where("closed = false and deleted = false")
                                .orderBy("_creation_date asc").criteria();
                    }
                }else {
                    Timestamp stamp = new Timestamp(mGeeftList.get(0).getCreationTime());
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    if (mCollection.equals("story")){
                        paginate = BaasQuery.builder()
                                .orderBy("_creation_date asc").criteria();
                    } else {
                        paginate = BaasQuery.builder()
                                .where("closed = false and deleted = false and  timestamp > "
                                        + dateFormat.format(stamp))
                                .orderBy("_creation_date asc").criteria();
                    }
                }
                BaasResult<List<BaasDocument>> baasResult = BaasDocument.fetchAllSync(mCollection, paginate);
                if (baasResult.isSuccess()) {
                    try {
                        for (BaasDocument e : baasResult.get()) {

                            mGeeft = new Geeft();
                            mGeeft.setId(e.getId());
                            mGeeft.setUsername(e.getString("username"));
                            mGeeft.setBaasboxUsername(e.getString("baasboxUsername"));
                            mGeeft.setGeeftImage(e.getString("image") + BaasUser.current().getToken());
                            //Append ad image url your session token!
                            mGeeft.setGeeftDescription(e.getString("description"));
                            mGeeft.setUserProfilePic(e.getString("profilePic"));
                            mGeeft.setCreationTime(getCreationTimestamp(e));
                            mGeeft.setUserLocation(e.getString("location"));
                            mGeeft.setUserCap(e.getString("cap"));
                            mGeeft.setGeeftTitle(e.getString("title"));
                            mGeeft.setDonatedLinkId(e.getString("donatedLinkId"));
                            mGeeft.setUserFbId(e.getString("userFbId"));

                            if(e.getBoolean("allowCommunication")!=null){
                                mGeeft.setAutomaticSelection(e.getBoolean("automaticSelection"));
                                mGeeft.setAllowCommunication(e.getBoolean("allowCommunication"));
                                mGeeft.setDimensionRead(e.getBoolean("allowDimension"));
                                mGeeft.setGeeftHeight(e.getInt("height"));
                                mGeeft.setGeeftWidth(e.getInt("width"));
                                mGeeft.setGeeftDepth(e.getInt("depth"));
                                mGeeft.setAssigned(e.getBoolean("assigned"));
                                mGeeft.setTaken(e.getBoolean("taken"));
                                mGeeft.setGiven(e.getBoolean("given"));
                                mGeeft.setDeadLine(e.getLong("deadline"));
                            }


                            // i build the string to compare with the query
                            StringBuilder strToCompareBuilder = new StringBuilder();
                            strToCompareBuilder.append(mGeeft.getGeeftTitle()).append(mGeeft.getGeeftDescription());
                            String strToCompare = strToCompareBuilder.toString().toLowerCase().replace(" ", "");

                            for (BaasLink l : links) {
                                //Log.d(TAG,"out: " + l.out().getId() + " in: " + l.in().getId());
                                Log.d(TAG, "e id: " + e.getId() + " inId: " + l.in().getId());
                                //if(l.out().getId().equals(e.getId())){ //TODO: LOGIC IS THIS,but BaasLink.create have a bug
                                if (l.in().getId().equals(e.getId())) {
                                    mGeeft.setIsSelected(true);// set prenoteButton selected (I'm already
                                    // reserved)
                                    mGeeft.setReservedLinkId(l.getId());
                                    Log.d(TAG, "link id is: " + l.getId());
                                }
                            }
                            if (strToCompare.contains(mSearchQuery)) {
                                mGeeftList.add(0, mGeeft);
                                mResultToken = RESULT_OK;
                                result = true;
                            }
                        }
                    } catch (BaasInvalidSessionException ise) {
                        mResultToken = RESULT_SESSION_EXPIRED;
                        return false;
                    } catch (com.baasbox.android.BaasException ex) {
                        Log.e("LOG", "Deal with error n " + BaaSTabGeeftTask.class + " " + ex.getMessage());
                        Toast.makeText(mContext, "Exception during loading!", Toast.LENGTH_LONG).show();
                        mResultToken = RESULT_FAILED;
                        return false;
                    }
                }
            }else {
                if (resUser.error() instanceof BaasInvalidSessionException) {
                    mResultToken = RESULT_SESSION_EXPIRED;
                    return false;
                } else {
                    Log.e(TAG, "Cannot insert new valure of submits_active");
                    mResultToken = RESULT_FAILED;
                    return false;
                }
            }
        }
        return result;
    }

    private static long getCreationTimestamp(BaasDocument d){ //return timestamp of _creation_date of document
        String date = d.getCreationDate();
        //Log.d(TAG,"_creation_date is:" + date);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date creation_date = dateFormat.parse(date);
            return creation_date.getTime(); //Convert timestamp in string
        }catch (java.text.ParseException e){
            Log.e(TAG,"ERRORE FATALE : " + e.toString());
        }
        return -1;

    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result,mResultToken);
    }
}
