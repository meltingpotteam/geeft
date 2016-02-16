package samurai.geeft.android.geeft.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
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
public class GeeftStoryListFragment extends Fragment implements TaskCallbackBoolean{
    private List<Geeft> mGeeftList;
    private RecyclerView mRecyclerView;
    private GeeftStoryListAdapter mAdapter;
    private OnGeeftImageSelectedListener mCallback;
    private Geeft mGeeft;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_geeft_story_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);
//        mRecyclerView.setHasFixedSize(true);


        mAdapter = new GeeftStoryListAdapter(getActivity(), mGeeftList);
        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL));
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

        new BaasRecievedGeeftTask(getContext(),"received",mGeeftList,mAdapter,this).execute();

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeeftList = new ArrayList<>();

    }


    public void done(boolean result){
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
        mAdapter.notifyDataSetChanged();
    }

    public GeeftStoryListFragment getInstance(){
        return this;
    }
}
