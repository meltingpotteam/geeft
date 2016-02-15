package samurai.geeft.android.geeft.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
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

/**
 * Created by ugookeadu on 20/01/16.
 */
public class GeeftListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        TaskCallbackBoolean {
    private static final String ARG_GEEFT_LIST_STATE= "samurai.geeft.android.geeft.fragments." +
            "GeeftListFragment_geeftListState";
    private static final String GEEFT_LIST_KEY = "samurai.geeft.android.geeft.fragments." +
            "GeeftListFragment_geeftList";
    private List<Geeft> mGeeftList;
    private RecyclerView mRecyclerView;
    private GeeftItemAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private View mBallView;
    private Parcelable mGeeftListState;

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
    public void onRefresh() {
        // Add new items to mGeeftList
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

    public GeeftListFragment getInstance(){
       return this;
    }

    /**
     * Savind list state and items
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save list items
        outState.putParcelableArrayList(GEEFT_LIST_KEY, (ArrayList) mGeeftList);
        // Save list state
        mGeeftListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(ARG_GEEFT_LIST_STATE, mGeeftListState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //retrievs mGeeftList position and state else gets items from database
        if (savedInstanceState != null) {
            mBallView.setVisibility(View.GONE);
            mGeeftListState = savedInstanceState.getParcelable(ARG_GEEFT_LIST_STATE);
        }
        else{
            new BaaSFeedImageTask(getContext(),mGeeftList,mAdapter,this).execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //resume mGeeftList position and state
        if (mGeeftListState != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mGeeftListState);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeeftList = new ArrayList<>();

        //resume the mGeeftList items if it exist
        if (savedInstanceState != null) {
            Log.d("GeeftListFragment", "onCreate->onSaveInstanceState");
            ArrayList<Geeft> array = (ArrayList)
                    savedInstanceState.getParcelableArrayList(GEEFT_LIST_KEY);
            mGeeftList.addAll(array);
            Log.d("GeeftListFragment", "onCreate->" + mGeeftList.size());
        }

    }
    /**
     * END Saving list state and items
     */
}
