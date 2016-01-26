package samurai.geeft.android.geeft.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.adapter.GeeftAdapter;
import samurai.geeft.android.geeft.database.BaaSFeedImageTask;
import samurai.geeft.android.geeft.database.TaskCallbackBoolean;
import samurai.geeft.android.geeft.model.Geeft;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class GeeftListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        TaskCallbackBoolean {
    private List<Geeft> mGeeftList;
    private RecyclerView mRecyclerView;
    private GeeftAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.geeft_item_recyclerview, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);


        mAdapter = new GeeftAdapter(mGeeftList, R.layout.geeft_item);

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
        new BaaSFeedImageTask(getContext(),mGeeftList,this).execute();
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getContext(), "On Refresh",Toast.LENGTH_LONG).show();
        new BaaSFeedImageTask(getContext(),mGeeftList,this).execute();
    }

    public void done(boolean result){
        if(mRefreshLayout.isRefreshing())
            mRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();
    }
}
