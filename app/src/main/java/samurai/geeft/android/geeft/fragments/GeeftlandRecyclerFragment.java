package samurai.geeft.android.geeft.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.adapters.GeeftlandAdapter;
import samurai.geeft.android.geeft.database.BaaSGeeftlandGeeftTask;
import samurai.geeft.android.geeft.interfaces.ClickListener;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.RecyclerTouchListener;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by gabriel-dev on 09/02/16.
 */

public class GeeftlandRecyclerFragment extends StatedFragment
        implements  SwipeRefreshLayout.OnRefreshListener, TaskCallbackBoolean{

    private String TAG = "GeeftlandRecyclerFragment";
    private List<Geeft> mGeeftList;
    private RecyclerView mRecyclerView;
    private GeeftlandAdapter mAdapter;
//    private OnGeeftImageSelectedListener mCallback;
    private Geeft mGeeft;
    private Parcelable mGeeftlandState;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mRefreshLayout;
    private View mBallView;

    private static final String GEEFTLAND_STATE_KEY = "samurai.geeft.android.geeft.fragments." +
            "GeeftlandRecyclerFragment_geeftListState";
    private final static String ADD_GEEFT_RECIEVED_LIST_FRAGMENT_SAVED_STATE_KEY = "samurai.geeft.android.geeft.activities."+
            "geeftland_recycler_list_fragment_saved_state";
    private ProgressDialog mProgress;

    public static GeeftlandRecyclerFragment newInstance(Bundle b) {
        GeeftlandRecyclerFragment fragment = new GeeftlandRecyclerFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    protected void onFirstTimeLaunched() {
        super.onFirstTimeLaunched();
        mProgress = new ProgressDialog(getActivity());
        try {
//                    mProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgress.show();
        } catch (WindowManager.BadTokenException e) {
        }
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        mProgress.setMessage("Attendere");
        //TODO: sobstitute with correct baas task, ask to UNO or Danibr
        new BaaSGeeftlandGeeftTask(getContext(), mGeeftList, mAdapter, this).execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeeftList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_geeftland_list, container, false);
//        mToolbar = (Toolbar)rootView.findViewById(R.id.geeftland);
//        Log.d("TOOLBAR", "" + (mToolbar != null));
//        if (mToolbar!=null)
//            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.geeftland_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);
//        mRecyclerView.setHasFixedSize(true);

        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.my_swiperefreshlayout);
        mRefreshLayout.setOnRefreshListener(this);
        mBallView = rootView.findViewById(R.id.loading_balls);
        mAdapter = new GeeftlandAdapter(getActivity(), mGeeftList);
        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity()
//                , mRecyclerView, new ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                //Toast.makeText(getActivity(), "Click element" + position+" "+mGeeftList.get(position).getId(), Toast.LENGTH_LONG).show();
//                //TODO complete the fragment to start
//                mGeeft = mGeeftList.get(position);
//                mCallback.onImageSelected(mGeeft.getId());
//                mCallback.onImageSelected(mGeeft);
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//                //TODO what happens on long press
//                Toast.makeText(getActivity(), "Long press" + position, Toast.LENGTH_SHORT).show();
//                mGeeft = mGeeftList.get(position);
//                mCallback.onImageSelected(mGeeft.getId());
//                mCallback.onImageSelected(mGeeft);
//            }
//        }));
        return rootView;
    }

//    public interface OnGeeftImageSelectedListener {
//        void onImageSelected(String id);
//        void onImageSelected(Geeft geeft);
//    }

    @Override
    public void onRefresh() {
        Log.d(TAG,"onRefresh()");
        new BaaSGeeftlandGeeftTask(getActivity(),mGeeftList,mAdapter,this).execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
//        try {
//            mCallback = (OnGeeftImageSelectedListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString()
//                    + " must implement OnHeadlineSelectedListener");
//        }
    }

    public void done(boolean result){
        Log.d(TAG, "done()");
        mProgress.dismiss();
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

    public GeeftlandRecyclerFragment getInstance(){
        return this;
    }

    /**
     * Save Fragment's State here
     */
    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        outState.putSerializable("mGeeftList2", (Serializable)mGeeftList);
        // Save list state
        mGeeftlandState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(GEEFTLAND_STATE_KEY, mGeeftlandState);
    }

    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        if (savedInstanceState != null) {
            mGeeftList = new ArrayList<>();
            mGeeftlandState = savedInstanceState.getParcelable(GEEFTLAND_STATE_KEY);
            ArrayList<Geeft> array = (ArrayList) savedInstanceState.getSerializable("mGeeftList2");
            if (array != null) {
                mGeeftList.addAll(array);
            }
//            mGeeftListShowDialog();
        }
    }


    /**
     * Resume position of list
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mGeeftlandState != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mGeeftlandState);
        }
    }


//    /**
//     * Show dialog saying no received geeft aviable if necessary
//     */
//    private boolean mGeeftListShowDialog() {
//        if (mGeeftList.size() == 0) {
//            new AlertDialog.Builder(getContext())
//                    .setTitle("Errore")
//                    .setMessage("Nessun oggetto ricevuto disponibile")
//                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            getActivity().getSupportFragmentManager().popBackStack();
//                        }
//                    })
//                    .show();
//            Log.d("DONE", "in done ==0");
//            return true;
//        }
//        return false;
//    }
}
