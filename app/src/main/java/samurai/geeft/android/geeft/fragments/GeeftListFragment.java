package samurai.geeft.android.geeft.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private List<Geeft> mGeeftList;
    private RecyclerView mRecyclerView;
    private GeeftItemAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_geeft_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);


        mAdapter = new GeeftItemAdapter(getActivity(), mGeeftList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.my_swiperefreshlayout);
        mRefreshLayout.setOnRefreshListener(this);
        new BaaSFeedImageTask(getContext(),mGeeftList,mAdapter,this).execute();

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
}
