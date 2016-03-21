package samurai.geeft.android.geeft.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.baasbox.android.BaasQuery;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.MainActivity;
import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.database.BaaSSearchTask;
import samurai.geeft.android.geeft.database.BaaSTabGeeftTask;
import samurai.geeft.android.geeft.database.BaaSTopListTask;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanStringStringToken;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Category;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class TabGeeftFragment extends StatedFragment
        implements TaskCallbackBooleanToken, TaskCallbackBooleanStringStringToken {

    private static final String KEY_IS_CATEGORY_CALL = "key_is_category_call";
    private static final String KEY_CATEGORY = "key_category" ;
    private static final String FIRST_ID_KEY = "key_firstID";
    private static final String FIRST_TIME_STAMP = "key_firstTimeStamp";
    private static final String KEY_IS_SEARCH_CALL = "key_is_a_search_call";
    private static final String KEY_SEARCH = "key_search" ;
    private static final String KEY_FIRST_RID = "key_first_rid";
    private static final String KEY_LAST_RID = "key_last_rid";
    private final String TAG = getClass().getSimpleName();
    private final String PREF_FILE_NAME = "1pref_file";
    private static final String GEEFT_LIST_STATE_KEY = "geeft_list_state";
    private static final String GEFFT_LIST_KEY = "geeft_list_key";
    private static final String GEFFT_LIST_PREF = "geeft_list_pref";

    private List<Geeft> mGeeftList= new ArrayList<>();
    private RecyclerView mRecyclerView;
    private GeeftItemAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private Parcelable mGeeftListState;
    //private ProgressDialog mProgressDialog;
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    private boolean mIsCategoryCall;
    private Category mCategory;
    private Toolbar mToolbar;
    private String mFirstID;
    private int mListOldSize;
    //-------------------


    //
//      TODO:  QUERY TEST !!REMOVE!!
//
    private Button mButtonQuery;
    private long mFirstTimeStamp;

    //---for the search activity
    private boolean mIsSearchCall;
    private String mSearchQuery;
    private String mLastRId;
    private String mFirstRId;

    public static TabGeeftFragment newInstance(boolean isCategoryCall,Category category) {
        TabGeeftFragment fragment = new TabGeeftFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_CATEGORY_CALL, isCategoryCall);
        bundle.putSerializable(KEY_CATEGORY, category);
        fragment.setArguments(bundle);
        return fragment;
    }

    // instance for the search
    public static TabGeeftFragment newInstance(boolean isSearchCall,String query) {
        TabGeeftFragment fragment = new TabGeeftFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_SEARCH_CALL, isSearchCall);
        bundle.putSerializable(KEY_SEARCH, query.toLowerCase());
        fragment.setArguments(bundle);
        return fragment;
    }

    public static TabGeeftFragment newInstance(boolean isCategoryCall) {
        TabGeeftFragment fragment = new TabGeeftFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_CATEGORY_CALL, isCategoryCall);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showProgressDialog();
        initVariables();
        mFirstID = "1";
        mFirstTimeStamp = Long.MAX_VALUE;
        Log.d(TAG, "onCreate()-> savedInstanceState is null? " + (savedInstanceState == null));
    }

    private void initVariables() {
        mIsCategoryCall = getArguments().getBoolean(KEY_IS_CATEGORY_CALL,false);
        mIsSearchCall = getArguments().getBoolean(KEY_IS_SEARCH_CALL,false);
        if (mIsCategoryCall){
            mCategory = (Category)getArguments().getSerializable(KEY_CATEGORY);
        } else if (mIsSearchCall){
            mSearchQuery = (String)getArguments().getSerializable(KEY_SEARCH);
            Log.d(TAG, "QUERY_SETTED"+mSearchQuery);
        }
        else {
            mCategory = new Category("","");
            mSearchQuery = "";
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, " onCreateView() -> savedInstanceState is null? " + (savedInstanceState == null));

        //inflates the view
        View rootView;

        if(mIsCategoryCall){
            rootView = inflater.inflate(R.layout.fragment_recycler_and_swipe_with_toolbar
                    , container, false);
        }else {
            rootView = inflater.inflate(R.layout.fragment_recycler_and_swipe_no_toolbar
                    , container, false);
        }

        initUI(rootView);
        if (savedInstanceState==null && mIsCategoryCall)
            initSupportActionBar(rootView);

        return rootView;
    }

    @Override
    protected void onFirstTimeLaunched() {
        super.onFirstTimeLaunched();

        Log.d(TAG, "onFirstTimeLaunched()");
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                getData();
            }
        });
    }

    /**
     * Save Fragment's State here
     */

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);

        Log.d(TAG, "onSaveState()");
        saveState(outState);
    }

    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);

        Log.d(TAG, "onRestoreState()-> savedInstanceState is null? " + (savedInstanceState == null));
        restoreState(savedInstanceState);
        View rootView = getView();
        if (rootView!=null){
            initUI(rootView);
            if(mIsCategoryCall){
                initSupportActionBar(rootView);
            }
        }
    }

    /**
     * Resume position of list
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mGeeftListState != null) {
            Log.d(TAG, "onResume()");
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mGeeftListState);
        }
    }

    private void showToast(String message) {
        Toast toast;
        if(getActivity()!=null){
            toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }
    }

    private  void initUI(View rootView){
        Log.d(TAG, "INITUI GEEFT");
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new GeeftItemAdapter(getContext(),mGeeftList, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        final Handler handler = new Handler() ;

        if(!mIsCategoryCall&&!mIsSearchCall) {
            mAdapter.setOnLoadMoreListener(new GeeftItemAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    //add progress item
                    if (mGeeftList.get(mGeeftList.size() - 1) == null)
                        return;
                    mGeeftList.add(null);
                    mAdapter.notifyItemInserted(mGeeftList.size() - 1);
                    getData(true);
                    System.out.println("load");
                }
            });
        }
//
//      TODO:  QUERY TEST !!REMOVE!!
//
//
//        mButtonQuery = (Button) rootView.findViewById(R.id.buttonquery);
//        mButtonQuery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getData();
//            }
//        });
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.my_swiperefreshlayout);
            mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getData();
                }
            });
    }


    private void saveState(Bundle outState){
        outState.putParcelableArrayList(GEFFT_LIST_KEY, (ArrayList) mGeeftList);
        outState.putString(FIRST_ID_KEY, mFirstID);
        outState.putLong(FIRST_TIME_STAMP, mFirstTimeStamp);
        // Save list state
        mGeeftListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(GEEFT_LIST_STATE_KEY, mGeeftListState);
        outState.putBoolean(KEY_IS_CATEGORY_CALL, mIsCategoryCall);

        outState.putBoolean(KEY_IS_SEARCH_CALL, mIsSearchCall);
        outState.putString(KEY_FIRST_RID, mFirstID);
        outState.putString(KEY_LAST_RID, mLastRId);
    }

    private void restoreState(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            mGeeftListState = savedInstanceState.getParcelable(GEEFT_LIST_STATE_KEY);
            ArrayList<Geeft> arrayList=
                    (ArrayList)savedInstanceState.getParcelableArrayList(GEFFT_LIST_KEY);
            mGeeftList.addAll(arrayList);
            mIsCategoryCall = savedInstanceState.getBoolean(KEY_IS_CATEGORY_CALL);
            mIsSearchCall = savedInstanceState.getBoolean(KEY_IS_SEARCH_CALL);
            mFirstID = savedInstanceState.getString(KEY_FIRST_RID);
            mLastRId = savedInstanceState.getString(KEY_LAST_RID);
        }

        Log.d(TAG, "onRestoreState()-> mGeeftList==null || mGeeftList.size()==0? "
                + (mGeeftList == null || mGeeftList.size() == 0));
        if (mGeeftList!=null)
            Log.d(TAG, "onRestoreState()-> mGeeftList.size= "
                    +(mGeeftList.size()));

        if (mGeeftList==null || mGeeftList.size()==0){
           getData();
        }
        else {
            mAdapter.notifyDataSetChanged();
        }
    }
// CHANGED TO PUBLIC FOR TESTING, NOT REALLY SURE IF IT CAN REMAIN LIKE THIS
    public void getData(){
        if(!isNetworkConnected()) {
            mRefreshLayout.setRefreshing(false);
            showSnackbar();
        }else {
            getData(false);
        }
    }

    public void getData(boolean isButtomRefresh){
        mListOldSize = mGeeftList.size();
        Log.d("BaaSGeeftItemTask2", "ID from TabGeeftFragment: " + mFirstID);
        /*else if(!mIsCategoryCall){
            new BaasLimitedTabGeeftTask(getActivity(),mGeeftList,mAdapter,mFirstID,mFirstTimeStamp,this).execute();
        }
        else {

                    */
        BaasQuery.Criteria paginate;
        if(mIsSearchCall){
            //for the search activity
            Log.d(TAG, "SEARCH CALLED RIGHT ");
            new BaaSSearchTask(getActivity(),mGeeftList,mAdapter,mSearchQuery,"geeft",this).execute();

        } else if(mIsCategoryCall){
            paginate = BaasQuery.builder()
                    .where("closed = false and deleted = false and category = '"
                            +mCategory.getCategoryName().toLowerCase()+"'")
                    .orderBy("_creation_date asc").criteria();

            new BaaSTabGeeftTask(getActivity(),mGeeftList,mAdapter
                    , paginate,this).execute();
        } else {
            new BaaSTopListTask(getActivity(), mGeeftList, mAdapter
                    , mFirstRId, mLastRId, isButtomRefresh,"geeft", this).execute();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    private void initSupportActionBar(View rootView) {
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity())
                .getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void done(boolean result, int resultToken){
        Log.d(TAG, "done()");
        String message = new String();
        if(mRefreshLayout.isRefreshing()) {
           stopRefreshOperations(result,resultToken);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void done(boolean result, String firstRId, String lastRId, int resultToken) {
        Log.d(TAG, "done()");
        mFirstRId = firstRId;
        mLastRId = lastRId;
        if(mRefreshLayout.isRefreshing()) {
            stopRefreshOperations(result, resultToken);
        }else{
            boolean trovato = false;
            for (int i = 0; i < mGeeftList.size() && !trovato; i++) {
                if (mGeeftList.get(i) == null) {
                    Log.d(TAG, "elemento " + mGeeftList.get(i));
                    mGeeftList.remove(i);
                    trovato = true;
                    mAdapter.notifyDataSetChanged();
                }
            }
            mAdapter.setLoaded();
        }
        mAdapter.notifyDataSetChanged();
    }

    private void stopRefreshOperations(boolean result, int resultToken) {
        String message = new String();
        mRefreshLayout.setRefreshing(false);
        Toast toast;
        if (result) {
            int newSize = mGeeftList.size();
            if(newSize==0 || newSize==mListOldSize) {
                if(getContext()!=null) {
                    message = "Nessun nuovo annuncio";
                }
            }
            else {
                message = "Nuovi annunci scorri";
            }
        } else {
            if(resultToken == RESULT_OK) {
                message= "Nessun nuovo annuncio";
            }else if (resultToken == RESULT_SESSION_EXPIRED) {
                message= "Sessione scaduta,è necessario effettuare di nuovo" +
                        " il login";
            }else if(mGeeftList.size()==0){
                message="Nessun risultato";
            }else {
                message="Operazione non possibile. Riprovare.";
            }
        }
        showToast(message);
    }
    private void showSnackbar(){
        if(getActivity().getClass().equals(MainActivity.class)) {
            Snackbar snackbar = Snackbar
                    .make(getActivity().findViewById(R.id.main_coordinator_layout),
                            "No Internet Connection!", Snackbar.LENGTH_SHORT)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getData();
                        }
                    });
            snackbar.show();
        }
    }

    /*private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(getContext());
        try {
//                    mProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Attendere");
            mProgressDialog.show();
        } catch (WindowManager.BadTokenException e) {
            Log.e(TAG,"error: " + e.toString());
        }
    }*/
}
