package samurai.geeft.android.geeft.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
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
import samurai.geeft.android.geeft.database.BaaSTabGeeftoryTask;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by ugookeadu on 17/02/16.
 */
public class TabGeeftoryFragment extends StatedFragment implements TaskCallbackBooleanToken {

    private final String TAG = getClass().getSimpleName();
    private final String PREF_FILE_NAME = "2pref_file";
    private static final String GEEFT_LIST_STATE_KEY = "geeft_list_state";
    private static final String GEFFTORY_LIST_KEY = "geeftory_list_key";
    private static final String GEFFTORY_LIST_PREF = "geeftory_list_pref";

    private List<Geeft> mGeeftoryList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private StoryItemAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private Parcelable mGeeftListState;

    public static TabGeeftoryFragment newInstance(Bundle b) {
        TabGeeftoryFragment fragment = new TabGeeftoryFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()-> savedInstanceState is null? " + (savedInstanceState == null));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, " onCreateView()-> savedInstanceState is null? " + (savedInstanceState == null));

        //inflates the view
        View rootView = inflater.inflate(R.layout.fragment_recycler_and_swipe_no_toolbar, container, false);

        //initialize UI
        initUI(rootView);

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


    public void done(boolean result,String firstID, int token) {
        Log.d(TAG, "done()");

        if (mRefreshLayout.isRefreshing()) {
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

    private void initUI(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);

        mAdapter = new StoryItemAdapter(getActivity(), mGeeftoryList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.my_swiperefreshlayout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
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


    private void getData() {

        if (!isNetworkConnected()) {
            mRefreshLayout.setRefreshing(false);
            showSnackbar();
        } else {
            new BaaSTabGeeftoryTask(getActivity(), mGeeftoryList, mAdapter, this).execute();
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