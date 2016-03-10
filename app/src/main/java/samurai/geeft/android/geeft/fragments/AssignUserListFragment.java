package samurai.geeft.android.geeft.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.adapters.AssignUserListAdapter;
import samurai.geeft.android.geeft.database.BaaSFetchUsersFromLink;
import samurai.geeft.android.geeft.interfaces.ClickListener;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.models.User;
import samurai.geeft.android.geeft.utilities.RecyclerTouchListener;
import samurai.geeft.android.geeft.utilities.StatedFragment;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by ugookeadu on 07/03/16.
 */
public class AssignUserListFragment extends StatedFragment implements TaskCallbackBoolean{
    private static final String ARG_BAAS_USER = "arg_baas_user";
    private static final String ARG_GEEFT = "arg_geeft";
    private final String TAG = getClass().getSimpleName();

    private List<User> mUserList;
    private RecyclerView mRecyclerView;
    private AssignUserListAdapter mAdapter;
    private Geeft mGeeft;


    public static AssignUserListFragment newInstance(Geeft geeft) {
        AssignUserListFragment fragment = new AssignUserListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_GEEFT, geeft);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeeft = (Geeft)getArguments().getSerializable(ARG_GEEFT);
        mUserList = new ArrayList<>();
        Log.d(TAG, "IN ASSIGN");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview
                , container, false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);
//        mRecyclerView.setHasFixedSize(true);


        mAdapter = new AssignUserListAdapter(getActivity(), mUserList);
        Log.d(TAG,"ON CREATE VIEW");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity()
                , mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void startUserProfileFragmet(User user, boolean isCurrentUser) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = UserProfileFragment.newInstance(user, mGeeft,isCurrentUser);
        fm.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit();

    }


    @Override
    protected void onFirstTimeLaunched() {
        super.onFirstTimeLaunched();
        getData();
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
    }

    public void getData() {
        new BaaSFetchUsersFromLink(getContext(), mGeeft, TagsValue.LINK_NAME_RESERVE
                , mUserList,this).execute();
    }

    @Override
    public void done(boolean isOK) {
        if (isOK){
            mAdapter.notifyDataSetChanged();
        }
    }
}
