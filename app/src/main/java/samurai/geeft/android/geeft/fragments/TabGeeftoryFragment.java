package samurai.geeft.android.geeft.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.adapters.StoryItemAdapter;
import samurai.geeft.android.geeft.database.BaaSGeeftoryRecycleTask;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by ugookeadu on 17/02/16.
 */
public class GeeftoryRecycleFragment extends StatedFragment
        implements SwipeRefreshLayout.OnRefreshListener, TaskCallbackBoolean {

    private final String TAG = getClass().getSimpleName().toUpperCase();
    private static final String GEEFT_LIST_STATE_KEY = "samurai.geeft.android.geeft.fragments." +
            "GeeftoryRecycleFragment_geeftListState";

    private List<Geeft> mGeeftList;
    private RecyclerView mRecyclerView;
    private StoryItemAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private View mBallView;

    private Parcelable mGeeftListState;

    public static GeeftoryRecycleFragment newInstance(Bundle b) {
        GeeftoryRecycleFragment fragment = new GeeftoryRecycleFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, " onCreateView()-> savedInstanceState is null? " + (savedInstanceState == null));
        View rootView = inflater.inflate(R.layout.fragment_geeft_list, container, false);
        mBallView = rootView.findViewById(R.id.loading_balls);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new StoryItemAdapter(getActivity(), mGeeftList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.my_swiperefreshlayout);
        mRefreshLayout.setOnRefreshListener(this);
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
        Log.d(TAG,"onRefresh()");
        new BaaSGeeftoryRecycleTask(getActivity(),mGeeftList,mAdapter,this).execute();
    }

    public void done(boolean result){
        Log.d(TAG,"done()");
        mBallView.setVisibility(View.GONE);
        if(mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
            Toast toast;
            if (result) {

                toast = Toast.makeText(getContext(), "Nuove storie, scorri", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
            } else {
                toast = Toast.makeText(getContext(), "Nessuna nuova storia", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public GeeftoryRecycleFragment getInstance(){
        return this;
    }

    @Override
    protected void onFirstTimeLaunched() {
        super.onFirstTimeLaunched();
        Log.d(TAG, "onFirstTimeLaunched()");
        new BaaSGeeftoryRecycleTask(getActivity(),mGeeftList,mAdapter,this).execute();
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

        Log.d(TAG, "onRestoreState()-> savedInstanceState is null? "+(savedInstanceState==null));

        if (savedInstanceState != null) {
            mGeeftListState = savedInstanceState.getParcelable(GEEFT_LIST_STATE_KEY);
            ArrayList<Geeft> arrayList=
                    (ArrayList)savedInstanceState.getParcelableArrayList("mGeeftList");
            mGeeftList.addAll(arrayList);
        }

        Log.d(TAG, "onRestoreState()-> mGeeftList==null || mGeeftList.size()==0? "
                +(mGeeftList==null || mGeeftList.size()==0));
        if (mGeeftList!=null)
            Log.d(TAG, "onRestoreState()-> mGeeftList.size= "
                    +(mGeeftList.size()));

        if (mGeeftList==null || mGeeftList.size()==0){
            new BaaSGeeftoryRecycleTask(getActivity(),mGeeftList,mAdapter,this).execute();
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
}