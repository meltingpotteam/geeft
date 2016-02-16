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
import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.database.BaaSFeedImageTask;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class GeeftMainRecycleFragment extends StatedFragment
        implements SwipeRefreshLayout.OnRefreshListener, TaskCallbackBoolean {

    private static final String GEEFT_LIST_STATE_KEY = "samurai.geeft.android.geeft.fragments." +
            "AddGeeftRecievedListFragment_geeftListState";

    private List<Geeft> mGeeftList;
    private RecyclerView mRecyclerView;
    private GeeftItemAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private View mBallView;

    private Parcelable mGeeftListState;

    public static GeeftMainRecycleFragment newInstance(Bundle b) {
        GeeftMainRecycleFragment fragment = new GeeftMainRecycleFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_geeft_list, container, false);
        mBallView = rootView.findViewById(R.id.loading_balls);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new GeeftItemAdapter(getActivity(), mGeeftList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.my_swiperefreshlayout);
        mRefreshLayout.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeeftList = new ArrayList<>();

    }

    @Override
    public void onRefresh() {
        new BaaSFeedImageTask(getContext(),mGeeftList,mAdapter,this).execute();
    }

    public void done(boolean result){
        mBallView.setVisibility(View.GONE);
        if(mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
            Toast toast;
            if (result) {

                toast = Toast.makeText(getContext(), "Nuovi annunci, scorri", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
            } else {
                toast = Toast.makeText(getContext(), "Nessun nuovo annuncio", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public GeeftMainRecycleFragment getInstance(){
       return this;
    }

    @Override
    protected void onFirstTimeLaunched() {
        super.onFirstTimeLaunched();
        new BaaSFeedImageTask(getContext(),mGeeftList,mAdapter,this).execute();
    }

    /**
     * Save Fragment's State here
     */

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
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
        if (savedInstanceState != null) {
            mBallView.setVisibility(View.GONE);
            mGeeftListState = savedInstanceState.getParcelable(GEEFT_LIST_STATE_KEY);
        }
        else{
            Log.d("LOADBALL", "LOAD");
            new BaaSFeedImageTask(getContext(),mGeeftList,mAdapter,this).execute();
        }
    }

    /**
     * Resume position of list
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mGeeftListState != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mGeeftListState);
        }
    }
}
