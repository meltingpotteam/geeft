package samurai.geeft.android.geeft.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import samurai.geeft.android.geeft.activities.LoginActivity;
import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.database.BaaSTabGeeftTask;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class TabGeeftFragment extends StatedFragment implements TaskCallbackBooleanToken {

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
    //-------------------

    public static TabGeeftFragment newInstance(Bundle b) {
        TabGeeftFragment fragment = new TabGeeftFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_tab_geeftory, container, false);

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

        Log.d(TAG, "onRestoreState()-> savedInstanceState is null? "+(savedInstanceState==null));

        restoreState(savedInstanceState);
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


    public void done(boolean result, int resultToken){
        Log.d(TAG, "done()");

        if(mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
            Toast toast;
            if (result) {
                if(!(mGeeftList.size() == 0)) {
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

    private  void initUI(View rootView){
        Log.d(TAG, "INITUI GEEFT");
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);

        mAdapter = new GeeftItemAdapter(getActivity(), mGeeftList);
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

    private void saveState(Bundle outState){
        outState.putParcelableArrayList(GEFFT_LIST_KEY, (ArrayList) mGeeftList);
        // Save list state
        mGeeftListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(GEEFT_LIST_STATE_KEY, mGeeftListState);
    }

    private void restoreState(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            mGeeftListState = savedInstanceState.getParcelable(GEEFT_LIST_STATE_KEY);
            ArrayList<Geeft> arrayList=
                    (ArrayList)savedInstanceState.getParcelableArrayList(GEFFT_LIST_KEY);
            mGeeftList.addAll(arrayList);
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

    private void getData(){

        if(!isNetworkConnected()) {
            mRefreshLayout.setRefreshing(false);
            showSnackbar();
        }else {
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
