package samurai.geeft.android.geeft.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.DonatedActivity;
import samurai.geeft.android.geeft.activities.ReceivedActivity;
import samurai.geeft.android.geeft.adapters.GeeftStoryListAdapter;
import samurai.geeft.android.geeft.database.BaaSReceivedDonatedGeeftTask;
import samurai.geeft.android.geeft.interfaces.ClickListener;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.RecyclerTouchListener;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by ugookeadu on 09/02/16.
 */
public class GeeftReceivedListFragment extends StatedFragment implements TaskCallbackBoolean{
    private List<Geeft> mGeeftList;
    private RecyclerView mRecyclerView;
    private GeeftStoryListAdapter mAdapter;
    private OnGeeftImageSelectedListener mCallback;
    private Geeft mGeeft;
    private Parcelable mGeeftListState;
    private Toolbar mToolbar;

    private static final String GEEFT_LIST_STATE_KEY = "samurai.geeft.android.geeft.fragments." +
            "AddGeeftRecievedListFragment_geeftListState";
    private final static String ADD_GEEFT_RECIEVED_LIST_FRAGMENT_SAVED_STATE_KEY = "samurai.geeft.android.geeft.activities."+
            "add_geeft_recieved_list_fragment_saved_state";
    private ProgressDialog mProgress;

    public static GeeftReceivedListFragment newInstance(Bundle b) {
        GeeftReceivedListFragment fragment = new GeeftReceivedListFragment();
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
        new BaaSReceivedDonatedGeeftTask(getContext(), getArguments().getString("link_name"), mGeeftList, mAdapter, this).execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeeftList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_received_list, container, false);
        mToolbar = (Toolbar)rootView.findViewById(R.id.fragment_add_geeft_toolbar);
        Log.d("TOOLBAR", "" + (mToolbar != null));
        if (mToolbar!=null)
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);
//        mRecyclerView.setHasFixedSize(true);


        mAdapter = new GeeftStoryListAdapter(getActivity(), mGeeftList);
        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity()
                , mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(getActivity(), "Click element" + position+" "+mGeeftList.get(position).getId(), Toast.LENGTH_LONG).show();
                //TODO complete the fragment to start
                mGeeft = mGeeftList.get(position);
                mCallback.onImageSelected(mGeeft.getId());
                mCallback.onImageSelected(mGeeft);
            }

            @Override
            public void onLongClick(View view, int position) {
                //TODO what happens on long press
                Toast.makeText(getActivity(), "Long press" + position, Toast.LENGTH_SHORT).show();
                mGeeft = mGeeftList.get(position);
                mCallback.onImageSelected(mGeeft.getId());
                mCallback.onImageSelected(mGeeft);
            }
        }));
        return rootView;
    }

    public interface OnGeeftImageSelectedListener {
        void onImageSelected(String id);
        void onImageSelected(Geeft geeft);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnGeeftImageSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void done(boolean result){
        Toast toast;
        Log.d("DONE", "in done");
        mProgress.dismiss();
        if (result) {
            if (mGeeftList==null || mGeeftList.size()==0) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Oops")
                        .setMessage("Nessun oggetto ricevuto da mostrare!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().getSupportFragmentManager().popBackStack();
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
            else {
                mAdapter.notifyDataSetChanged();
            }
        }
        else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Errore")
                    .setMessage("Riprovare più tardi")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    })
                    .show();
        }
    }

    public GeeftReceivedListFragment getInstance(){
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
            mGeeftListState = savedInstanceState.getParcelable(GEEFT_LIST_STATE_KEY);
            ArrayList<Geeft> array = (ArrayList) savedInstanceState.getSerializable("mGeeftList2");
            mGeeftList.addAll(array);
            mGeeftListShowDialog();
        }
        else{

            if(getContext().getClass().equals(ReceivedActivity.class)){
                Log.d("geeftStoryFragment","getContext is equal to Received Activity: " + getContext().getClass().equals(ReceivedActivity.class));
            new BaaSReceivedDonatedGeeftTask(getContext(), "received" ,mGeeftList,mAdapter,this).execute();
            }
            if(getContext().getClass().equals(DonatedActivity.class)){
                Log.d("geeftStoryFragment","getContext is equal to Donated Activity: " + getContext().getClass().equals(DonatedActivity.class));
                new BaaSReceivedDonatedGeeftTask(getContext(), "donated" ,mGeeftList,mAdapter,this).execute();
            }
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


    /**
     * Show dialog saying no received geeft aviable if necessary
     */
    private boolean mGeeftListShowDialog() {
        if (mGeeftList.size() == 0) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Errore")
                    .setMessage("Nessun oggetto ricevuto disponibile")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    })
                    .show();
            Log.d("DONE", "in done ==0");
            return true;
        }
        return false;
    }
}
