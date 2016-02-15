package samurai.geeft.android.geeft.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.adapters.GeeftStoryListAdapter;
import samurai.geeft.android.geeft.database.BaasRecievedGeeftTask;
import samurai.geeft.android.geeft.interfaces.ClickListener;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.RecyclerTouchListener;

/**
 * Created by ugookeadu on 09/02/16.
 */
public class AddGeeftRecievedListFragment extends Fragment implements TaskCallbackBoolean{
    private List<Geeft> mGeeftList;
    private RecyclerView mRecyclerView;
    private GeeftStoryListAdapter mAdapter;
    private OnGeeftImageSelectedListener mCallback;
    private Geeft mGeeft;
    private Parcelable mGeeftListState;
    private static final String ARG_GEEFT_LIST_STATE = "samurai.geeft.android.geeft.fragments." +
            "AddGeeftRecievedListFragment_geeftListState";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_geeft_recieved_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);


        mAdapter = new GeeftStoryListAdapter(getActivity(), mGeeftList);
        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity()
                , mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                mGeeft = mGeeftList.get(position);
                mCallback.onImageSelected(mGeeft.getId());
            }

            @Override
            public void onLongClick(View view, int position) {
                //TODO what happens on long press
                mGeeft = mGeeftList.get(position);
                mCallback.onImageSelected(mGeeft.getId());
            }
        }));

        return rootView;
    }

    public interface OnGeeftImageSelectedListener {
        void onImageSelected(String id);
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
        if (result) {
            if(mGeeftList.size()==0)
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
            else
                mAdapter.notifyDataSetChanged();
        }
        else {
            new AlertDialog.Builder(getContext())
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

    public AddGeeftRecievedListFragment getInstance(){
        return this;
    }

    /**
     * Savind list state and items
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save list items
        outState.putParcelableArrayList("mGeeftList", (ArrayList) mGeeftList);
        // Save list state
        mGeeftListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(ARG_GEEFT_LIST_STATE, mGeeftListState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //retrievs mGeeftList position and state else gets items from database
        if (savedInstanceState != null) {
            mGeeftListState = savedInstanceState.getParcelable(ARG_GEEFT_LIST_STATE);
        }
        else{
            new BaasRecievedGeeftTask(getContext(),mGeeftList,mAdapter,this).execute();
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
            ArrayList<Geeft> array = (ArrayList)
                    savedInstanceState.getParcelableArrayList("mGeeftList");
            mGeeftList.addAll(array);
        }

    }
    /**
     * END Saving list state and items
     */
}
