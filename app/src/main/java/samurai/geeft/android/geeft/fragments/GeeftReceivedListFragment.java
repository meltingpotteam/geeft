package samurai.geeft.android.geeft.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import samurai.geeft.android.geeft.activities.LoginActivity;
import samurai.geeft.android.geeft.activities.MainActivity;
import samurai.geeft.android.geeft.adapters.GeeftStoryListAdapter;
import samurai.geeft.android.geeft.database.BaaSFetchLinks;
import samurai.geeft.android.geeft.interfaces.ClickListener;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.RecyclerTouchListener;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by ugookeadu on 09/02/16.
 */
public class GeeftReceivedListFragment extends StatedFragment implements TaskCallbackBooleanToken{
    public static final String KEY_LINK_NAME = "key_link_name";
    public static final String KEY_SHOW_WINNER_DIALOG = "key_show_winner_dialog";
    private static final String KEY_LIST_STATE = "key_list_state";

    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    
    private List<Geeft> mGeeftList;
    private RecyclerView mRecyclerView;
    private GeeftStoryListAdapter mAdapter;
    private OnGeeftImageSelectedListener mCallback;
    private Geeft mGeeft;
    private Toolbar mToolbar;

    private Parcelable mGeeftListState;
    private boolean showWinnerDialog;
    private ProgressDialog mProgressDialog;
    private String linkName;

    public static GeeftReceivedListFragment newInstance(String linkName,boolean showWinnerDialog) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LINK_NAME, linkName);
        bundle.putBoolean(KEY_SHOW_WINNER_DIALOG, showWinnerDialog);
        GeeftReceivedListFragment fragment = new GeeftReceivedListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onFirstTimeLaunched() {
        super.onFirstTimeLaunched();

        if(showWinnerDialog){
            showWinnerDialog();
        }else {
            getData();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        initUI(rootView);
        if (savedInstanceState==null)
            initSupportActionBar(rootView);

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

    public void done(boolean result,String firstID, int resultToken){
        Log.d("DONE", "in done");
        if(mProgressDialog!=null)
            mProgressDialog.dismiss();

        if (result) {
            if(mGeeftList!=null)
            Log.d("GeeftReceivedList",mGeeftList.size()+"");
            if (mGeeftList==null || mGeeftList.size()==0) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Oops")
                        .setMessage("Nessun oggetto ricevuto da mostrare!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if(getActivity()
                                        .getSupportFragmentManager().getBackStackEntryCount()>0){
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }else {
                                    getActivity().onBackPressed();
                                }
                            }
                        })
                        .show();
            }
            else {
                mAdapter.notifyDataSetChanged();
            }
        }
        else {
            Toast toast;
            if (resultToken == RESULT_OK) {
                //DO SOMETHING
            } else if (resultToken == RESULT_SESSION_EXPIRED) {
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
        outState.putParcelable(KEY_LIST_STATE, mGeeftListState);
    }


    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        if (savedInstanceState != null) {
            mGeeftList = new ArrayList<>();
            mGeeftListState = savedInstanceState.getParcelable(KEY_LIST_STATE);
            ArrayList<Geeft> array = (ArrayList) savedInstanceState.getSerializable("mGeeftList2");
            mGeeftList.addAll(array);
            mGeeftListShowDialog();
            View rootView = getView();
            if (rootView!=null){
                initUI(rootView);
                initSupportActionBar(rootView);
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

    private void initVariables() {
        mGeeftList = new ArrayList<>();
        showWinnerDialog = getArguments().getBoolean(KEY_SHOW_WINNER_DIALOG);
        linkName = getArguments().getString(KEY_LINK_NAME);
    }

    public void getData() {
        showProgressDialog();
        new BaaSFetchLinks(getContext(),
                linkName, mGeeftList, mAdapter, this).execute();
    }

    private void showWinnerDialog() {
        if(showWinnerDialog){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.winner_screen_message);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    getData();
                }
            });
            builder.setNegativeButton("Chiudi", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = MainActivity.newIntent(getContext());
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            builder.show();
        }
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        try {
//                    mProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.show();
        } catch (WindowManager.BadTokenException e) {
        }
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Attendere");
    }

    private void initSupportActionBar(View rootView) {
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity())
                .getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initUI(View rootView) {
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
    }

}
