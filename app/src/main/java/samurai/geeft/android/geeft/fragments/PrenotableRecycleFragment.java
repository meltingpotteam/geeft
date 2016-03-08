package samurai.geeft.android.geeft.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.activities.LoginActivity;
import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.database.BaaSTabGeeftTask;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class PrenotableRecycleFragment extends StatedFragment
        implements SwipeRefreshLayout.OnRefreshListener, TaskCallbackBooleanToken {
    private final String TAG = getClass().getSimpleName().toUpperCase();
    private static final String GEEFT_LIST_STATE_KEY = "samurai.geeft.android.geeft.fragments." +
            "AddGeeftRecievedListFragment_geeftListState";

    private List<Geeft> mGeeftList;
    private RecyclerView mRecyclerView;
    private GeeftItemAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private View mBallView;

    private Parcelable mGeeftListState;

    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    private Toolbar mToolbar;
    //-------------------


    public static PrenotableRecycleFragment newInstance(Bundle b) {
        PrenotableRecycleFragment fragment = new PrenotableRecycleFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView()-> savedInstanceState is null? " + (savedInstanceState == null));
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        initUI(rootView);
        if (savedInstanceState==null)
            initSupportActionBar(rootView);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()-> savedInstanceState is null? " + (savedInstanceState == null));
        mGeeftList = new ArrayList<>();
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh()");
        new BaaSTabGeeftTask(getActivity(),mGeeftList,mAdapter,this).execute();
    }

    public void done(boolean result,String firstID, int resultToken){
        Log.d(TAG,"done()");
        mBallView.setVisibility(View.GONE);
        if(mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
            Toast toast;
            if (result) {
                if(mGeeftList.size() == 0) {
                    toast = Toast.makeText(getContext(), "Nuovi annunci, scorri", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();
                }
                else{
                    toast = Toast.makeText(getContext(), "Nessun nuovo annuncio", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();
                }
            } else {
                if(resultToken == RESULT_OK) {
                    toast = Toast.makeText(getContext(), "Nessun nuovo annuncio", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 0);
                    toast.show();
                }
                else if(resultToken == RESULT_SESSION_EXPIRED){
                    toast = Toast.makeText(getContext(), "Sessione scaduta,è necessario effettuare di nuovo" +
                            " il login", Toast.LENGTH_LONG);
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    toast.show();
                }
                else{
                    toast = Toast.makeText(getContext(), "E' accaduto un errore", Toast.LENGTH_LONG);
                      toast.show();
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public PrenotableRecycleFragment getInstance(){
       return this;
    }

    @Override
    protected void onFirstTimeLaunched() {
        super.onFirstTimeLaunched();
        Log.d(TAG, "onFirstTimeLaunched()");
        new BaaSTabGeeftTask(getActivity(),mGeeftList,mAdapter,this).execute();
    }

    /**
     * Save Fragment's State here
     */

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        Log.d(TAG, "onSaveState()");
        outState.putParcelableArrayList("mGeeftList", (ArrayList) mGeeftList);
        // Save list state
        mGeeftListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(GEEFT_LIST_STATE_KEY, mGeeftListState);
    }

    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);

        Log.d(TAG, "onRestoreState()-> savedInstanceState is null? " + (savedInstanceState == null));

        if (savedInstanceState != null) {
            mGeeftListState = savedInstanceState.getParcelable(GEEFT_LIST_STATE_KEY);
            ArrayList<Geeft> arrayList=
                    (ArrayList)savedInstanceState.getParcelableArrayList("mGeeftList");
            mGeeftList.addAll(arrayList);
            View rootView = getView();
            if (rootView!=null){
                initUI(rootView);
                initSupportActionBar(rootView);
            }
        }

        Log.d(TAG, "onRestoreState()-> mGeeftList==null || mGeeftList.size()==0? "
                + (mGeeftList == null || mGeeftList.size() == 0));
        if (mGeeftList!=null)
            Log.d(TAG, "onRestoreState()-> mGeeftList.size= "
                    +(mGeeftList.size()));

        if (mGeeftList==null || mGeeftList.size()==0){
            new BaaSTabGeeftTask(getActivity(),mGeeftList,mAdapter,this).execute();
        }
        else {
            mBallView.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
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

    private void initUI(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new GeeftItemAdapter(getActivity(), mGeeftList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.my_swiperefreshlayout);
        mRefreshLayout.setOnRefreshListener(this);
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
