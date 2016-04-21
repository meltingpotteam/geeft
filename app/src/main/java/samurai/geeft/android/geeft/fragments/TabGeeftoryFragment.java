package samurai.geeft.android.geeft.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baasbox.android.BaasQuery;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.LoginActivity;
import samurai.geeft.android.geeft.adapters.StoryItemAdapter;
import samurai.geeft.android.geeft.database.BaaSSearchTask;
import samurai.geeft.android.geeft.database.BaaSTopListTask;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanStringStringToken;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Category;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by ugookeadu on 17/02/16.
 */

public class TabGeeftoryFragment extends StatedFragment implements TaskCallbackBooleanToken
        ,TaskCallbackBooleanStringStringToken {

    private final String TAG = getClass().getSimpleName();
    private final String PREF_FILE_NAME = "2pref_file";
    private static final String GEEFT_LIST_STATE_KEY = "geeft_list_state";
    private static final String GEFFTORY_LIST_KEY = "geeftory_list_key";
    private static final String KEY_IS_SEARCH_CALL = "key_is_a_search_call";
    private static final String KEY_SEARCH = "key_search" ;
    private static final String KEY_CATEGORY = "key_category" ;
    private static final String KEY_IS_CATEGORY_CALL = "key_is_category_call";

    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    private List<Geeft> mGeeftoryList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private StoryItemAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private Parcelable mGeeftListState;
    private String mFirstRId;
    private String mLastRId;
    private int mListOldSize;
    private boolean mIsSearchCall;
    private String mSearchQuery;
    private boolean mIsCategoryCall;
    private Toolbar mToolbar;
    private Category mCategory;

    public static TabGeeftoryFragment newInstance(Bundle b) {
        TabGeeftoryFragment fragment = new TabGeeftoryFragment();
        fragment.setArguments(b);
        return fragment;
    }

    // instance for the search
    public static TabGeeftoryFragment newInstance(boolean isSearchCall,String query) {
        TabGeeftoryFragment fragment = new TabGeeftoryFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_SEARCH_CALL, isSearchCall);
        bundle.putSerializable(KEY_SEARCH, query.toLowerCase());
        fragment.setArguments(bundle);
        return fragment;
    }

    public static TabGeeftoryFragment newInstance(boolean isCategoryCall,Category category) {
        TabGeeftoryFragment fragment = new TabGeeftoryFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_CATEGORY_CALL, isCategoryCall);
        bundle.putSerializable(KEY_CATEGORY, category);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()-> savedInstanceState is null? " + (savedInstanceState == null));
        initVariables();
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

        Log.d(TAG, " onCreateView()-> savedInstanceState is null? " + (savedInstanceState == null));

        //inflates the view
        View rootView;


        if(mIsCategoryCall){
            rootView = inflater.inflate(R.layout.fragment_recycler_and_swipe_with_toolbar
                    , container, false);
        }else {
            rootView = inflater.inflate(R.layout.fragment_recycler_and_swipe_no_toolbar
                    , container, false);
        }

        //initialize UI
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
        Log.d(TAG, "onResume()");
        if (mGeeftListState != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mGeeftListState);
        }
    }


    public void done(boolean result, int token) {
        Log.d(TAG, "done()");

        if (mRefreshLayout.isRefreshing()) {
            stopRefreshOperations(result, token);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initUI(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new StoryItemAdapter(getActivity(), mGeeftoryList,mRecyclerView);
        if(!mIsCategoryCall&&!mIsSearchCall) {
            mAdapter.setOnLoadMoreListener(new StoryItemAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    //add progress item
                    if (mGeeftoryList.get(mGeeftoryList.size() - 1) == null)
                        return;
                    mGeeftoryList.add(null);
                    mAdapter.notifyItemInserted(mGeeftoryList.size() - 1);
                    getData(true);
                    System.out.println("load");
                }
            });
        }
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.my_swiperefreshlayout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
    }

    private void initSupportActionBar(View rootView) {
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity())
                .getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mCategory.getCategoryName());
    }

    private void saveState(Bundle outState) {
        outState.putParcelableArrayList(GEFFTORY_LIST_KEY, (ArrayList) mGeeftoryList);
        // Save list state
        mGeeftListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(GEEFT_LIST_STATE_KEY, mGeeftListState);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mGeeftListState = savedInstanceState.getParcelable(GEEFT_LIST_STATE_KEY);
            ArrayList<Geeft> arrayList =
                    (ArrayList) savedInstanceState.getParcelableArrayList(GEFFTORY_LIST_KEY);
            mGeeftoryList.addAll(arrayList);
        }

        Log.d(TAG, "onRestoreState()-> mGeeftoryList==null || mGeeftoryList.size()==0? "
                + (mGeeftoryList == null || mGeeftoryList.size() == 0));
        if (mGeeftoryList != null)
            Log.d(TAG, "onRestoreState()-> mGeeftoryList.size= "
                    + (mGeeftoryList.size()));

        if (mGeeftoryList == null || mGeeftoryList.size() == 0) {
           getData();
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void getData(){
        if(!isNetworkConnected()) {
            mRefreshLayout.setRefreshing(false);
            showSnackbar();
        }else {
            getData(false);
        }
    }

    private void getData(boolean isButtomRefresh) {
        mListOldSize = mGeeftoryList.size();
        BaasQuery.Criteria paginate;
        if(mIsSearchCall){
            //for the search activity
            Log.d(TAG, "SEARCH CALLED RIGHT ");
            new BaaSSearchTask(getActivity(),mGeeftoryList,mAdapter,mSearchQuery, "story",this).execute();

        }else if(mIsCategoryCall){
            new BaaSTopListTask(getActivity(), mGeeftoryList, mAdapter
                    , mFirstRId, mLastRId, isButtomRefresh,"story",
                    mCategory.getCategoryName().toLowerCase() ,this).execute();
        }else {
            new BaaSTopListTask(getActivity(), mGeeftoryList, mAdapter
                    , mFirstRId, mLastRId, isButtomRefresh,"story",null, this).execute();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void showSnackbar() {
        final Snackbar snackbar = Snackbar
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


    private void showToast(String message) {
        Toast toast;
        if(getActivity()!=null){
            toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    @Override
    public void done(boolean result, String firstId, String lastId, int resultToken) {
        Log.d(TAG, "done()");
        mFirstRId = firstId;
        mLastRId = lastId;
        if(mRefreshLayout.isRefreshing()) {
            stopRefreshOperations(result, resultToken);
        }else{
            boolean trovato = false;
            for (int i = 0; i < mGeeftoryList.size() && !trovato; i++) {
                if (mGeeftoryList.get(i) == null) {
                    Log.d(TAG, "elemento " + mGeeftoryList.get(i));
                    mGeeftoryList.remove(i);
                    trovato = true;
                    mAdapter.notifyDataSetChanged();
                }
            }
            mAdapter.setLoaded();
        }
        mAdapter.notifyDataSetChanged();
    }

    private void stopRefreshOperations(boolean result, int resultToken) {
        String message = null;
        mRefreshLayout.setRefreshing(false);
        Toast toast;
        if (result) {
            int newSize = mGeeftoryList.size();
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
                startLoginActivity();
            }else if(mGeeftoryList.size()==0){
                message="Nessun risultato";
            }else if(resultToken == RESULT_FAILED){
                message="Operazione fallita, riprovare.";
            }
        }
        if(message!=null) {
            showToast(message);
        }
    }

    private void startLoginActivity(){
        Intent intent = new Intent(getContext(),LoginActivity.class);
        startActivity(intent);
    }

}