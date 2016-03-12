package samurai.geeft.android.geeft.fragments;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.LoginActivity;
import samurai.geeft.android.geeft.activities.MainActivity;
import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.database.BaaSTabGeeftTask;
import samurai.geeft.android.geeft.database.BaaSTabLimitedGeeftTask;
import samurai.geeft.android.geeft.database.BaasLimitedTabGeeftTask;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Category;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class TabGeeftFragment extends StatedFragment implements TaskCallbackBooleanToken {

    private static final String KEY_IS_CATEGORY_CALL = "key_is_category_call";
    private static final String KEY_CATEGORY = "key_category" ;
    private static final String FIRST_ID_KEY = "key_firstID";
    private static final String FIRST_TIME_STAMP = "key_firstTimeStamp";
    private static final String KEY_IS_SEARCH_CALL = "ke_is_a_search_call";
    private static final String KEY_SEARCH = "key_search" ;
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
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    private boolean mIsCategoryCall;
    private Category mCategory;
    private Toolbar mToolbar;
    private String mFirstID;
    //-------------------


    //
//      TODO:  QUERY TEST !!REMOVE!!
//
    private Button mButtonQuery;
    private long mFirstTimeStamp;

    //---for the search activity
    private boolean mIsSearchCall;
    private String mSearchQuery;

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
        initVariables();
        Log.d("BaaSGeeftItemTask2", "ESTO PASA SIEMPRE? ");
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
        getData();
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


    public void done(boolean result, String firstID, long firstTimeStamp, int resultToken){
        Log.d(TAG, "done()");
        mFirstID=firstID;
        mFirstTimeStamp=firstTimeStamp;
        if(mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
            Toast toast;
            if (result) {
                if(!(mGeeftList.size() == 0)) {
                    toast = Toast.makeText(getContext(), "Nuovi annunci, scorri", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else{
                    toast = Toast.makeText(getContext(), "Nessun nuovo annuncio", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            } else {
                if(resultToken == RESULT_OK) {
                    toast = Toast.makeText(getContext(), "Nessun nuovo annuncio", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else if (resultToken == RESULT_SESSION_EXPIRED) {
                    toast = Toast.makeText(getContext(), "Sessione scaduta,è necessario effettuare di nuovo" +
                            " il login", Toast.LENGTH_LONG);
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    toast.show();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Errore")
                            .setMessage("Operazione non possibile. Riprovare più tardi.").show();
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    public void clearData() {
        int size = this.mGeeftList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.mGeeftList.remove(0);
            }
            mAdapter.notifyItemRangeRemoved(0, size);
        }
    }
    private  void initUI(View rootView){
        Log.d(TAG, "INITUI GEEFT");
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);

        mAdapter = new GeeftItemAdapter(getActivity(), mGeeftList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
//
//      TODO:  QUERY TEST !!REMOVE!!
//
//
        mButtonQuery = (Button) rootView.findViewById(R.id.buttonquery);
        mButtonQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
//
//
//
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.my_swiperefreshlayout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearData();
                mFirstID="1";
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
    }

    private void restoreState(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            mGeeftListState = savedInstanceState.getParcelable(GEEFT_LIST_STATE_KEY);
            ArrayList<Geeft> arrayList=
                    (ArrayList)savedInstanceState.getParcelableArrayList(GEFFT_LIST_KEY);
            mGeeftList.addAll(arrayList);
            mIsCategoryCall = savedInstanceState.getBoolean(KEY_IS_CATEGORY_CALL);
            mIsSearchCall = savedInstanceState.getBoolean(KEY_IS_SEARCH_CALL);
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
        Log.d("BaaSGeeftItemTask2", "ID from TabGeeftFragment: " + mFirstID);
        if(!isNetworkConnected()) {
            mRefreshLayout.setRefreshing(false);
            showSnackbar();
        }/*else if(!mIsCategoryCall){
            new BaasLimitedTabGeeftTask(getActivity(),mGeeftList,mAdapter,mFirstID,mFirstTimeStamp,this).execute();
        }
        else {
            new BaasLimitedTabGeeftTask(getActivity(),mGeeftList,mAdapter,
                    mIsCategoryCall,mCategory,mFirstID,mFirstTimeStamp,this).execute();
        }*/else if(!mIsCategoryCall && mIsSearchCall){
            //for the search activity
            Log.d(TAG, "SEARCH CALLED RIGHT ");
            new BaaSTabGeeftTask(getActivity(),mGeeftList,mAdapter,
                    mIsCategoryCall, mIsSearchCall, mSearchQuery,this).execute();
        }
        else if (mIsCategoryCall && !mIsSearchCall){
            new BaaSTabGeeftTask(getActivity(),mGeeftList,mAdapter,
                    mIsCategoryCall, mIsSearchCall, mCategory,this).execute();
        } else{
            new BaaSTabGeeftTask(getActivity(),mGeeftList,mAdapter,this).execute();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void showSnackbar(){
        if(getActivity().getClass().equals(MainActivity.class)) {
            Snackbar snackbar = Snackbar
                    .make(getActivity().findViewById(R.id.main_coordinator_layout),
                            "No Internet Connection!", Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getData();
                        }
                    });
            snackbar.show();
        }
    }

    private void initSupportActionBar(View rootView) {
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity())
                .getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
