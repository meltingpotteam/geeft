package samurai.geeft.android.geeft.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.Rest;
import com.baasbox.android.json.JsonArray;
import com.baasbox.android.json.JsonObject;

import java.util.List;

import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.adapters.StoryItemAdapter;
import samurai.geeft.android.geeft.fragments.TabGeeftFragment;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanStringStringToken;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 14/03/16.
 */
public class BaaSTopListTask extends BaaSCheckTask{
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;

    private final String TAG = getClass().getSimpleName();
    private final String mCollection;
    private StoryItemAdapter mStoryItemAdapter;
    private TaskCallbackBooleanToken mCallback2;
    Context mContext;
    List<Geeft> mGeeftList;
    TaskCallbackBooleanStringStringToken mCallback;
    GeeftItemAdapter mGeeftItemAdapter;
    boolean result;
    boolean mIsButtomRefresh;
    boolean isEmpty;
    String mTopRid;
    String mButtomRid;
    int mListSize;

    public BaaSTopListTask(Context context, List<Geeft> feedItems, GeeftItemAdapter Adapter,
                     String topRid,String buttomRid, boolean isButtomRefresh,String collection,
                     TaskCallbackBooleanStringStringToken callback) {
        mContext = context;
        mGeeftList = feedItems;
        mTopRid = topRid;
        mButtomRid = buttomRid;
        mCallback = callback;
        mIsButtomRefresh = isButtomRefresh;
        mGeeftItemAdapter = Adapter;
        mGeeftList = feedItems;
        mCollection = collection;
    }

    public BaaSTopListTask(Context context, List<Geeft> feedItems, StoryItemAdapter Adapter,
                           String topRid,String buttomRid, boolean isButtomRefresh, String collection,
                           TaskCallbackBooleanStringStringToken callback) {
        mContext = context;
        mGeeftList = feedItems;
        mTopRid = topRid;
        mButtomRid = buttomRid;
        mCallback = callback;
        mIsButtomRefresh = isButtomRefresh;
        mStoryItemAdapter = Adapter;
        mGeeftList = feedItems;
        mCollection = collection;
    }


    @Override
    protected Boolean doInBackground(Void... arg0) {
        Geeft mGeeft;
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
            String collection;
            if(mContext.getClass().equals(TabGeeftFragment.class)){
                collection = "geeft";
            }else{
                collection = "story";
            }
            Log.d(TAG,"collection = "+collection+" getContext ="+ mContext);
            if (resUser.isSuccess()) {
                isEmpty = mGeeftList.size()==0? true:false;
                BaasResult<JsonObject> baasResult;
                if(isEmpty) {
                    baasResult = BaasBox.rest().sync(Rest.Method.GET,
                            "plugin/query.tabs?" +
                                    "firstTimeCalled=true" +
                                    "&collection=" +mCollection+
                                    "&is_minor_than="+mIsButtomRefresh+
                                    "&orderBy=rid%20desc");
                }else{
                    String rid;
                    if(mIsButtomRefresh) {
                        rid = mButtomRid;
                        mListSize = mGeeftList.size();
                        Log.d(TAG, "mIsButtomRefresh rid= "+rid);
                    }else {
                        rid = mTopRid;
                        Log.d(TAG, "NOT mIsButtomRefresh rid= "+rid);
                    }
                    baasResult = BaasBox.rest().sync(Rest.Method.GET,
                            "plugin/query.tabs?" +
                                    "firstTimeCalled=false" +
                                    "&rid="+ rid+
                                    "&collection=" +mCollection+
                                    "&is_minor_than=" +mIsButtomRefresh+
                                    "&orderBy=rid%20desc");
                }

                if (baasResult.isSuccess()) {
                    try {
                        JsonArray docArray= baasResult.get().getObject("data").getArray("result");
                        Log.d(TAG,"Successo docArray size = "+docArray.size());
                        int size = docArray.size();

                        if(size>0) {
                            Log.d(TAG,("@rid"));
                            if (isEmpty) {
                                mTopRid = docArray.getObject(0)
                                        .getString("@rid")
                                        .replace("#", "%23")
                                        .replace(":", "%3A");
                                mButtomRid = docArray.getObject(docArray.size() - 1)
                                        .getString("@rid")
                                        .replace("#", "%23")
                                        .replace(":", "%3A");
                                Log.d(TAG, "mButtomRid= " + docArray.getObject(0)
                                        .getString("@rid"));
                                Log.d(TAG, "mTopRid= " + docArray.getObject(docArray.size() - 1)
                                        .getString("@rid"));
                            } else {
                                if (mIsButtomRefresh) {
                                    mButtomRid = docArray.getObject(0)
                                            .getString("@rid")
                                            .replace("#", "%23")
                                            .replace(":", "%3A");
                                    Log.d(TAG, "mButtomRid= " + docArray.getObject(0)
                                            .getString("@rid"));
                                } else {
                                    mTopRid = docArray.getObject(docArray.size() - 1)
                                            .getString("@rid")
                                            .replace("#", "%23")
                                            .replace(":", "%3A");
                                    Log.d(TAG, "mTopRid= " + docArray.getObject(docArray.size() - 1)
                                            .getString("@rid"));
                                }
                            }
                            if(!mCollection.equals("story")){
                                quickSort(docArray, 0, size - 1);
                            }
                        }

                        for (int i =0;i<docArray.size();i++) {
                            JsonObject e = docArray.getObject(i);
                            Log.d(TAG, e.toString());
                            mGeeft = new Geeft();
                            mGeeft.fillGeeft(e,links);
                            for (BaasLink l : links) {
                                //Log.d(TAG,"out: " + l.out().getId() + " in: " + l.in().getId());
                                Log.d(TAG, "e id: " + e.getString("id") + " inId: " + l.in().getId());
                                //if(l.out().getId().equals(e.getId())){
                                // TODO: LOGIC IS THIS,but BaasLink.create have a bug
                                if (l.in().getId().equals(e.getString("id"))) {
                                    mGeeft.setIsSelected(true);
                                    // set prenoteButton selected (I'm already
                                    // reserved)
                                    mGeeft.setReservedLinkId(l.getId());
                                    Log.d(TAG, "link id is: " + l.getId());
                                }
                            }
                            if(!isEmpty ){
                                if(mIsButtomRefresh) {
                                    mGeeftList.add(mGeeft);
                                }else {
                                    mGeeftList.add(0,mGeeft);
                                }
                            }else{
                                if(mIsButtomRefresh){
                                    mGeeftList.add(0,mGeeft);
                                }else {
                                    mGeeftList.add(mGeeft);
                                }
                            }
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

                }else{
                    Log.d(TAG,"Fallito"+baasResult.error().toString());
                    return false;
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
        return result;
    }

    int partition(JsonArray arr, int left, int right) {

        int i = left, j = right;

        JsonObject tmp;

        Log.d(TAG,"(left + right) / 2 ="+(left + right) / 2);
        Log.d(TAG, "arr.getObject((left + right) / 2) = "+ arr.getObject((left + right) / 2));
        long pivot = arr.getObject((left + right) / 2).getLong("deadline");



        while (i <= j) {
            while (arr.getObject(i).getLong("deadline") < pivot)
                i++;

            while (arr.getObject(j).getLong("deadline") >pivot)
                j--;

            if (i <= j) {
                tmp = arr.getObject(i);
                arr.set(i,arr.getObject(j));
                arr.set(j,tmp);
                i++;
                j--;
            }

        };
        return i;
    }



    void quickSort(JsonArray arr, int left, int right) {
        Log.d(TAG, "IN QUICKSORT");

        int index = partition(arr, left, right);

        if (left < index - 1)

            quickSort(arr, left, index - 1);

        if (index < right)

            quickSort(arr, index, right);

    }

    @Override
    protected void onPostExecute(Boolean result) {
        mCallback.done(result, mTopRid, mButtomRid,mResultToken);
    }

}
