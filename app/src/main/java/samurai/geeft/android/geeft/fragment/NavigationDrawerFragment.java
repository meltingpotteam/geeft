package samurai.geeft.android.geeft.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.adapter.NavigationDrawerItemAdapter;
import samurai.geeft.android.geeft.model.NavigationDrawerItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {
    // SharedPreferences file name
    private static final  String PREF_FILE_NAME = "samurai.geeft.android.geeft.fragment." +
            "pref_name";
    private static final String KEY_USER_LEARNED_DRAWER = "samurai.geeft.android.geeft.fragment." +
            "uesr_learned_drawer";

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View mContainerView;
    private NavigationDrawerItemAdapter mNavigationDrawerItemAdapter;
    private RecyclerView mRecyclerView;

    // indicates if user is aware that the NavigationBar exists
    private boolean mUserLearnedDrawer;

    // indicate if we runnig the fragment lifecycle from begining
    // for example when back from rotation
    private boolean mFromSavedIstanceState;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // reading if user knows of the drawer existence, by default is false
        // false means that user never opened the drawer
        mUserLearnedDrawer= Boolean.valueOf(readFromPreferences(getActivity(),
               KEY_USER_LEARNED_DRAWER,"false"));

        //checking if fragment is comming back from a rotation or restrting from onCreate
        if(savedInstanceState!=null){
            mFromSavedIstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.navigation_drawer_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        mNavigationDrawerItemAdapter = new NavigationDrawerItemAdapter(getActivity(), getData());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mNavigationDrawerItemAdapter);

        return rootView;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        this.mDrawerLayout = drawerLayout;
        this.mContainerView = getActivity().findViewById(fragmentId);

        // overwriting two internal ActionBarDrawerToggle class methods
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(),mDrawerLayout,
        toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // if is the first time user is opening the app,then he learned it exists
                if(mUserLearnedDrawer == false) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(),
                            KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }

                getActivity().supportInvalidateOptionsMenu();
            }

           @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if(slideOffset<0.6)
                    toolbar.setAlpha(1-slideOffset);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                getActivity().supportInvalidateOptionsMenu();
            }
        };

        // if the user have never seen the drawer and if the very first time this fragment is starting
        // in that case display the drawer
        if (!mUserLearnedDrawer && !mFromSavedIstanceState)
            mDrawerLayout.openDrawer(mContainerView );

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });


        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    // implements the SharedPreferences with private mode
    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);

        // using async method apply that's is faster
        editor.apply();
    }

    // returns the SharedPreferences
    public static String readFromPreferences(Context context, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName,defaultValue);
    }

    public static List<NavigationDrawerItem> getData(){
        List<NavigationDrawerItem> navigationDrawerItems = new ArrayList<>();
        int icons[] = {R.drawable.account,R.drawable.gift, R.drawable.recycle,
                R.drawable.settings, R.drawable.mail};

        int titles[] = {R.string.account_title,R.string.gift_title,
                R.string.recycle_title, R.string.settings_title, R.string.mail_title};

        int descriptions[] = {R.string.account_description, R.string.gift_description,
                R.string.recycle_description,R.string.settings_description,
                R.string.mail_description};

        for(int i=0;i<icons.length && i<titles.length && i<descriptions.length;i++){
            NavigationDrawerItem item = new NavigationDrawerItem(titles[i],
                    descriptions[i],icons[i]);
            navigationDrawerItems.add(item);
        }
        return navigationDrawerItems;
    }
}
