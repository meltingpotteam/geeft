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
 * Created by joseph on 07/03/16.
 */
public class BaaSTabLimitedGeeftTask  extends BaaSCheckTask {
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    private static final String TAG ="BaaSGeeftItemTask";
    Context mContext;
    List<Geeft> mGeeftList;
    TaskCallbackBooleanToken mCallback;
    GeeftItemAdapter mGeeftItemAdapter;
    boolean result;
    boolean mIsCategoryTask;
    private String mFirstID;
    Category mCategory;
    private String mMyID;
    private int mSkipNumber;
    private int mGeeftQuantity = 1;

    public BaaSTabLimitedGeeftTask(Context context, List<Geeft> feedItems, GeeftItemAdapter Adapter,
                                   String firstID, TaskCallbackBooleanToken callback) {
        mContext = context;
        mGeeftList = feedItems;
        mCallback = callback;
        mGeeftItemAdapter = Adapter;
        mGeeftList = feedItems;
        mFirstID = firstID;
    }
    public BaaSTabLimitedGeeftTask(Context context, List<Geeft> feedItems, GeeftItemAdapter Adapter,
                                   boolean isCategoryTask, Category category, String firstID,
                                   TaskCallbackBooleanToken callback) {
        mContext = context;
        mGeeftList = feedItems;
        mCallback = callback;
        mGeeftItemAdapter = Adapter;
        mGeeftList = feedItems;
        mIsCategoryTask = isCategoryTask;
        mCategory = category;
        mFirstID = firstID;
    }
    @Override
    protected Boolean doInBackground(Void... arg0) {
        Geeft mGeeft;
        boolean mIsFirstLoop = true;
        Log.d(TAG, BaasUser.current().toString());
        String docId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id"); //retrieve doc_id attached at user
        //find all links with the doc_id (User id <--> doc id )
        Log.d(TAG, "Doc_id is: " + docId);
        //TODO: change when baasbox fix issue with BaasLink.create
        BaasQuery.Criteria query = BaasQuery.builder().where("in.id like '" + docId + "'").criteria();
        BaasResult<List<BaasLink>> resLinks = BaasLink.fetchAllSync("reserve", query);
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
        List<BaasLink> links;
        if (checkError(resLinks)) {
            links = resLinks.value();
            Log.d(TAG, "Your links are here: " + links.size());
            BaasUser currentUser = BaasUser.current();
            BaasUser.current().getScope(BaasUser.Scope.REGISTERED).put("submits_active", links.size());
            BaasResult<BaasUser> resUser = currentUser.saveSync();
            if (resUser.isSuccess()) {
                BaasQuery.Criteria paginate;
//                TODO change pagination to 10
                Log.d(TAG, "BEFORE choosing the criteria the id is: " + mFirstID);
                if (mFirstID=="") {
                    mSkipNumber = 0;
                    if (mIsCategoryTask) {
                        Log.d(TAG, "I should NOT be here with this id: " + mFirstID);
                        paginate = BaasQuery.builder().pagination(0, mGeeftQuantity)
                                .where("closed = false and deleted = false and category = '"
                                        + mCategory.getCategoryName().toLowerCase() + "'")
                                .orderBy("_creation_date asc").criteria();
                    } else {
                        Log.d(TAG, "I should be here with this id: " + mFirstID);
                        paginate = BaasQuery.builder().pagination(0, mGeeftQuantity)
                                .where("closed = false and deleted = false")
                                .orderBy("_creation_date asc").criteria();
                    }
                } else {
                    mSkipNumber += mGeeftQuantity;
                    Log.d(TAG, "I should NOT be here with this id: " + mFirstID);
                    paginate = BaasQuery.builder().pagination(0, 1)
                            .where("id = '" + mMyID + "'").criteria();
                    BaasResult<List<BaasDocument>> baasResult2 = BaasDocument.fetchAllSync("geeft", paginate);
                    if (baasResult2.isSuccess()) {
                        try {
                            for (BaasDocument y : baasResult2.get()) {
                                mMyID = y.getId();
                                Log.d(TAG, "I should NOT REPEAT THIS " + mFirstID);
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
                    } else {
                        Log.d(TAG, "Haven't found the 1st geeft, possibily deleted, such bad luck: " + mFirstID);
                        Toast.makeText(this.mContext, "Troppi utenti, scusi.",Toast.LENGTH_SHORT);
                    }
                    if (mFirstID==mMyID) {
                        if (mIsCategoryTask) {
                            Log.d(TAG, "I should NOT be here LoadMore with this id: " + mFirstID);
                            paginate = BaasQuery.builder().pagination(0, mGeeftQuantity).skip(mSkipNumber)
                                    .where("closed = false and deleted = false and category = '"
                                            + mCategory.getCategoryName().toLowerCase() + "'")
                                    .orderBy("_creation_date asc").criteria();
                        } else {
                            Log.d(TAG, "I should NOT be here LoadMore with this id: " + mFirstID);
                            paginate = BaasQuery.builder().pagination(0, mGeeftQuantity).skip(mSkipNumber)
                                    .where("closed = false and deleted = false")
                                    .orderBy("_creation_date asc").criteria();
                        }
                    } else {
                        Toast.makeText(this.mContext, "Ci sono nuovi oggetti!",Toast.LENGTH_SHORT);
                        if (mIsCategoryTask) {
                            Log.d(TAG, "Refresh id: " + mFirstID);
                            paginate = BaasQuery.builder().pagination(0, mGeeftQuantity)
                                    .where("closed = false and deleted = false and category = '"
                                            + mCategory.getCategoryName().toLowerCase() + "'")
                                    .orderBy("_creation_date asc").criteria();
                        } else {
                            Log.d(TAG, "Refresh id: " + mFirstID);
                            paginate = BaasQuery.builder().pagination(0, mGeeftQuantity)
                                    .where("closed = false and deleted = false")
                                    .orderBy("_creation_date asc").criteria();
                        }
                    }
                }
                BaasResult<List<BaasDocument>> baasResult = BaasDocument.fetchAllSync("geeft", paginate);
                if (baasResult.isSuccess()) {
                    try {
                        for (BaasDocument e : baasResult.get()) {
                            mGeeft = new Geeft();
                            mGeeft.setId(e.getId());
                            if (mIsFirstLoop){
                                mFirstID=e.getId();
                                Log.d(TAG, "The SAVED id: " + mFirstID);
                                mIsFirstLoop=false;
                            }
                            mGeeft.setUsername(e.getString("name"));
                            mGeeft.setBaasboxUsername(e.getString("baasboxUsername"));
                            mGeeft.setGeeftImage(e.getString("image") + BaasUser.current().getToken());
                            //Append ad image url your session token!
                            mGeeft.setGeeftDescription(e.getString("description"));
                            mGeeft.setUserProfilePic(e.getString("profilePic"));
                            mGeeft.setCreationTime(getCreationTimestamp(e));
                            mGeeft.setDeadLine(e.getLong("deadline"));
                            mGeeft.setUserFbId(e.getString("userFbId"));
//
                            mGeeft.setAutomaticSelection(e.getBoolean("automaticSelection"));
                            mGeeft.setAllowCommunication(e.getBoolean("allowCommunication"));
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
                                    mGeeft.setReservedLinkId(l.getId());
                                    Log.d(TAG, "link id is: " + l.getId());
                                }
                            }
                            mGeeftList.add(0, mGeeft);
                            mResultToken = RESULT_OK;
                            result = true;
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
                    Log.e(TAG, "Cannot insert new value of submits_active");
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
        mCallback.done(result,mFirstID,mResultToken);
    }
}
