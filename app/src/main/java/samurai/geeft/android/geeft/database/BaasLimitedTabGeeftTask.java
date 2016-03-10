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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Category;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by joseph on 08/03/16.
 */
public class BaasLimitedTabGeeftTask extends BaaSCheckTask{

    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;

    private static final String TAG ="BaaSGeeftItemTask";
    private static final String TAG2 ="BaaSGeeftItemTask2";
    Context mContext;
    List<Geeft> mGeeftList;
    TaskCallbackBooleanToken mCallback;
    GeeftItemAdapter mGeeftItemAdapter;
    boolean result;
    boolean mIsCategoryTask;
    Category mCategory;
    private int mGeeftQuantityShow = 3;
    private String mFirstID;
    private boolean mIsFirstLoop;
    private int k, mInitVal; //Counter
    private long mFirstTimeStamp;
    private long mActualTimeStamp;
    private long mFirstActualTimeStamp;


    public BaasLimitedTabGeeftTask(Context context, List<Geeft> feedItems, GeeftItemAdapter Adapter,
                                   String firstID,  long firstTimeStamp, TaskCallbackBooleanToken callback) {
        mContext = context;
        mGeeftList = feedItems;
        mCallback = callback;
        mGeeftItemAdapter = Adapter;
        mGeeftList = feedItems;
        mFirstID = firstID;
        mFirstTimeStamp = firstTimeStamp;
    }

    public BaasLimitedTabGeeftTask(Context context, List<Geeft> feedItems, GeeftItemAdapter Adapter,
                            boolean isCategoryTask, Category category,
                            String firstID, long firstTimeStamp, TaskCallbackBooleanToken callback) {
        mContext = context;
        mGeeftList = feedItems;
        mCallback = callback;
        mGeeftItemAdapter = Adapter;
        mGeeftList = feedItems;
        mIsCategoryTask = isCategoryTask;
        mCategory = category;
        mFirstID = firstID;
        mFirstTimeStamp = firstTimeStamp;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        Geeft mGeeft;
        mIsFirstLoop = true;
        Log.d(TAG2, "The current id: " + mFirstID);
        Log.d(TAG, BaasUser.current().toString());
        String docId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id"); //retrieve doc_is attached at user
        //find all links with the doc_id (User id <--> doc id )
        Log.d(TAG, "Doc_id is: " + docId);
        //TODO: change when baasbox fix issue with BaasLink.create
        BaasQuery.Criteria query = BaasQuery.builder().where("in.id like '" + docId + "'").criteria();
        BaasResult<List<BaasLink>> resLinks = BaasLink.fetchAllSync("reserve", query);
        List<BaasLink> links;
        /*if (resLinks.isSuccess()) {
            links = resLinks.value();
            Log.d(TAG, "Your links are here: " + links.size());
        } else {
            if (resLinks.error() instanceof BaasInvalidSessionException) {
                mResultToken = RESULT_SESSION_EXPIRED;
                return false;

            } else {
                Log.e(TAG, "Error when retrieve links:" + resLinks.error());
                mResultToken = RESULT_FAILED;
                return false; // Don't continue if we are in this case
            }
        }*/

        if (checkError(resLinks)) {
            links = resLinks.value();
            Log.d(TAG, "Your links are here: " + links.size());
            BaasUser currentUser = BaasUser.current();
            BaasUser.current().getScope(BaasUser.Scope.REGISTERED).put("submits_active", links.size());
            BaasResult<BaasUser> resUser = currentUser.saveSync();
            if (resUser.isSuccess()) {
                BaasQuery.Criteria paginate;

                if (mIsCategoryTask) {
                    paginate = BaasQuery.builder()
                            .where("closed = false and deleted = false and category = '"
                                    +mCategory.getCategoryName().toLowerCase()+"'")
                            .orderBy("_creation_date desc").criteria();
                }else{
                    paginate = BaasQuery.builder()
                            .where("closed = false and deleted = false")
                            .orderBy("_creation_date desc").criteria();
                }
                mInitVal =Integer.valueOf(mFirstID);
                Log.d(TAG2, "The ENTERING id: " + mFirstID);
                BaasResult<List<BaasDocument>> baasResult = BaasDocument.fetchAllSync("geeft", paginate);
                if (baasResult.isSuccess()) {
                    try {
                        k=0;
                        for (BaasDocument e : baasResult.get()) {
                            k++;
                            mActualTimeStamp = getCreationTimestamp(e);
                            if (mIsFirstLoop) {
                                mIsFirstLoop = false;
                                mFirstActualTimeStamp = mActualTimeStamp;
                                Log.d(TAG2, "Before SAVED timestamp: " + mFirstTimeStamp);
                                Log.d(TAG2, "Before ACTUAL timestamp: " + mActualTimeStamp);
                            }
                            if (((k>= mInitVal)&&(k< mInitVal +mGeeftQuantityShow))||(mActualTimeStamp>mFirstTimeStamp)) {
                                Log.d(TAG2, "The ACTUAL timestamp (writing on the list) : " + mActualTimeStamp);
                                mGeeft = new Geeft();
                                mGeeft.setId(e.getId());
                                mGeeft.setUsername(e.getString("name"));
                                mGeeft.setBaasboxUsername(e.getString("baasboxUsername"));
                                mGeeft.setGeeftImage(e.getString("image") + BaasUser.current().getToken());
                                //Append ad image url your session token!
                                mGeeft.setGeeftDescription(e.getString("description"));
                                mGeeft.setUserProfilePic(e.getString("profilePic"));
                                mGeeft.setCreationTime(getCreationTimestamp(e));
                                mGeeft.setDeadLine(e.getLong("deadline"));
                                mGeeft.setUserFbId(e.getString("userFbId"));
                                mGeeft.setAutomaticSelection(e.getBoolean("automaticSelection"));
                                mGeeft.setAllowCommunication(e.getBoolean("allowCommunication"));
                                mGeeft.setUserLocation(e.getString("location"));
                                mGeeft.setUserCap(e.getString("cap"));
                                mGeeft.setGeeftTitle(e.getString("title"));
                                mGeeft.setDimensionRead(e.getBoolean("allowDimension"));
                                mGeeft.setGeeftHeight(e.getInt("height"));
                                mGeeft.setGeeftWidth(e.getInt("width"));
                                mGeeft.setGeeftDepth(e.getInt("depth"));
                                mGeeft.setDonatedLinkId(e.getString("donatedLinkId"));
                                mGeeft.setAssigned(e.getBoolean("assigned"));
                                mGeeft.setTaken(e.getBoolean("taken"));
                                mGeeft.setGiven(e.getBoolean("given"));

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
                                if (mActualTimeStamp>mFirstTimeStamp) {
                                    mGeeftList.add(0, mGeeft);
                                    mInitVal++;
                                } else {
                                    mGeeftList.add(mGeeftList.size(), mGeeft);
                                }
                                mResultToken = RESULT_OK;
                                result = true;
                            }
                        }
                        mResultToken = RESULT_OK;
                        result = true;
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
            } else {
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
        mInitVal +=mGeeftQuantityShow;
        mFirstID=Integer.toString(mInitVal);
        Log.d(TAG2, "The SAVED id before the end: " + mFirstID);
        mFirstTimeStamp=mFirstActualTimeStamp;
        Log.d(TAG2, "The SAVED timestamp before the end: " + mFirstTimeStamp);
        return result;
    }

    private static long getCreationTimestamp(BaasDocument d){ //return timestamp of _creation_date of document
        String date = d.getCreationDate();
        //Log.d(TAG,"_creation_date is:" + date);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date creation_date = dateFormat.parse(date);

            return creation_date.getTime();
        }catch (java.text.ParseException e){
            Log.e(TAG,"ERRORE FATALE : " + e.toString());
        }
        return -1;

    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result,mFirstID,mFirstTimeStamp,mResultToken);
    }
}
