package samurai.geeft.android.geeft.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.AssignedActivity;
import samurai.geeft.android.geeft.activities.CategoryActivity;
import samurai.geeft.android.geeft.activities.DonatedActivity;
import samurai.geeft.android.geeft.activities.ReceivedActivity;
import samurai.geeft.android.geeft.activities.ReservedActivity;
import samurai.geeft.android.geeft.activities.SendReportActivity;
import samurai.geeft.android.geeft.activities.UserProfileActivity;
import samurai.geeft.android.geeft.adapters.NavigationDrawerItemAdapter;
import samurai.geeft.android.geeft.interfaces.ClickListener;
import samurai.geeft.android.geeft.models.NavigationDrawerItem;
import samurai.geeft.android.geeft.utilities.RecyclerTouchListener;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {
    // SharedPreferences file name
    private static final String TAG = "NavigationDrawer";

    private static final  String PREF_FILE_NAME = "samurai.geeft.android.geeft.fragment." +
            "pref_name";
    private static final String KEY_USER_LEARNED_DRAWER = "samurai.geeft.android.geeft.fragment." +
            "uesr_learned_drawer";

    private static final String SHOWCASE_ID_NAVDRAW = "Showcase_single_use_navigation_drawer";


    //variables
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View mContainerView;
    private NavigationDrawerItemAdapter mNavigationDrawerItemAdapter;
    private RecyclerView mRecyclerView;
    private OnDrawerClosedListener mCallback; //callback to listen if the navigation drawer is open or closed
    private List<NavigationDrawerItem> mListOfValuesInDrawer;

    public FrameLayout getProfileLayout() {
        return mProfileLayout;
    }

    private LinearLayout mWelcomeLayout;
    private FrameLayout mProfileLayout;
//    private ImageView mProfileImage;
    private ImageView mProfileImage;
    private TextView mProfleName;

    // indicates if user is aware that the NavigationBar exists
    private boolean mUserLearnedDrawer;

    // indicate if we runnig the fragment lifecycle from begining
    // for example when back from rotation
    private boolean mFromSavedIstanceState;

    public interface OnDrawerClosedListener {
        void onDrawerClosed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnDrawerClosedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //checking if fragment is comming back from a rotation or restrting from onCreate
        if(savedInstanceState!=null){
            mFromSavedIstanceState = true;
        }

        // reading if user knows of the drawer existence, by default is false
        // false means that user never opened the drawer
        mUserLearnedDrawer= Boolean.valueOf(readFromPreferences(getActivity(),
               KEY_USER_LEARNED_DRAWER,"false"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.navigation_drawer_recyclerview);
        mRecyclerView.setHasFixedSize(false);

        //initialize UI
        initUI(rootView);

        return rootView;
    }


    private  void initUI(View rootView){
        /**
         * the method was called to initialize the ui of the navigation drawer in when the
         * application starts;
         **/

        mListOfValuesInDrawer = getData();
        // Set adapter data
        mNavigationDrawerItemAdapter = new NavigationDrawerItemAdapter
                (getActivity(), mListOfValuesInDrawer);

        //set manager and adapter dor recycleview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mNavigationDrawerItemAdapter);

        // Initialization of the element in top of the navbar
        mWelcomeLayout = (LinearLayout) rootView.
                findViewById(R.id.navigation_drawer_welcome);
        mProfileLayout = (FrameLayout) rootView.
                findViewById(R.id.navigation_drawer_profile);

        mProfileImage = (ImageView) rootView.findViewById(R.id.navigation_drawer_geefter_profile_image);
        mProfleName = (TextView) rootView.findViewById(R.id.navigation_drawer_geefter_username);

        // ask for the facebook image and username
        String fbImage =BaasUser.current().getScope(BaasUser.Scope.REGISTERED).get("profilePic");
        Log.d(TAG,"URI = "+fbImage);
        String fbName = getFacebookName();

        // set them in the layout
        Picasso.with(getActivity()).load(Uri.parse(fbImage))
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .into(mProfileImage);
        Log.d(TAG, "profilePic loaded: " + fbImage);
        mProfleName.setText(fbName);

        //handle touch event of recycleview
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity()
                , mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(getActivity(), "Click element" + position, Toast.LENGTH_SHORT).show();
                //TODO complete the fragment to start
                startFragmentByPosition(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                //TODO what happens on long press
            }
        }));
    }



    //set up the Recyclerview on creation
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
                //initUI(getView()); //Questo crea un bug,apre tante finestre quante sono le volte
                //                  che si è aperto il menu. Da risolvere in altro modo
                getActivity().supportInvalidateOptionsMenu();

            }

           @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                    //TODO slide effect not saving after rotation
                    /*if(slideOffset<0.5) {
                        toolbar.setAlpha((float) 1 - slideOffset);
                    }*/
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(),
                            KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");

                    mWelcomeLayout.setVisibility(View.GONE);
                    mProfileLayout.setVisibility(View.VISIBLE);
                    mCallback.onDrawerClosed();     //send a "callback" to signal that the navigation drawer is closed
                }
            }
        };

        // if the user have never seen the drawer and if the very first time this fragment is starting
        // in that case display the drawer
        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(mContainerView);
            mWelcomeLayout.setVisibility(View.VISIBLE);
            mProfileLayout.setVisibility(View.GONE);
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });


        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    // implements the SharedPreferences with private mode
    private static void saveToPreferences(Context context, String preferenceName, String preferenceValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);

        // using async method apply that's is faster
        editor.apply();
    }

    // returns the SharedPreferences
    private static String readFromPreferences(Context context, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName,defaultValue);
    }

    //retuns the navigation drawer element list
    public static List<NavigationDrawerItem> getData(){
        List<NavigationDrawerItem> navigationDrawerItems = new ArrayList<>();
        int icons[] = {R.drawable.ic_profile_24dp,R.drawable.ic_object_given_24dp,
                R.drawable.ic_checkbox_marked_circle,
                R.drawable.ic_object_assigned_24dp,R.drawable.ic_object_recieved_24dp,
                R.drawable.ic_search_black_24dp,
                /*R.drawable.ic_settings_24dp,*/ R.drawable.ic_contact_us_24dp};

        int titles[] = {R.string.account_title,R.string.gift_title,
                R.string.reserved_gift_title,
                R.string.assigned_gift_title,R.string.recycle_title,
                R.string.categories_title,/* R.string.settings_title,*/
                R.string.mail_title};

        int descriptions[] = {R.string.account_description, R.string.gift_description,
                R.string.reserved_gift_description, R.string.assigned_gift_description,
                R.string.recycle_description,
                R.string.categories_description,/* R.string.settings_description,*/ R.string.mail_description};

        for(int i=0;i<icons.length && i<titles.length && i<descriptions.length;i++){
            NavigationDrawerItem item = new NavigationDrawerItem(titles[i],
                    descriptions[i],icons[i]);
            navigationDrawerItems.add(item);
        }
        return navigationDrawerItems;
    }


    //TODO put inside the case the corrispondent fragment to start
    private void startFragmentByPosition(final int position){

        switch (position){
            case 0:
                Intent intent0 = new Intent(getContext(), UserProfileActivity.class);
                startActivity(intent0);
                break;
            case 1:
                Intent intent1 = DonatedActivity
                        .newIntent(getContext(), TagsValue.LINK_NAME_DONATED, false);
                startActivity(intent1);
                break;
            case 2:
                Intent intent2 = new Intent(getContext(), ReservedActivity.class);
                startActivity(intent2);
                break;
            case 3:
                Intent intent3 = AssignedActivity
                        .newIntent(getContext(), TagsValue.LINK_NAME_ASSIGNED, false);
                startActivity(intent3);
                break;
            case 4:

                Intent intent4 = new Intent(getContext(), ReceivedActivity.class);
                startActivity(intent4);
                break;
            case 5:
                Intent intent5 = CategoryActivity.newIntent(getContext());
                startActivity(intent5);
                break;
            /*case 6:
                Intent intent6 = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent6);
                break;*/
            case 6:
                Intent intent6 = new Intent(getContext(), SendReportActivity.class);
                startActivity(intent6);
                break;
            default:
                Toast.makeText(getActivity(), "Azione non supportata",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private String getFacebookName(){// return display name of user's profile
        String FbName = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("name");
        Log.d(TAG, "FB_Name is: " + FbName);
        return FbName;
    }

    private String getProfilePicFacebook(){ // return link of user's profile picture
        JsonObject field = BaasUser.current().getScope(BaasUser.Scope.REGISTERED);
        String id = field.getObject("_social").getObject("facebook").getString("id");
        Log.d(TAG, "FB_id" + id);
        return "https://graph.facebook.com/" + id + "/picture?type=large";
    }

    /**
     * Tutorial implementation
     */
//
//    private void presentShowcaseNavigationView(int withDelay){
//
//        ShowcaseConfig config = new ShowcaseConfig();
//        config.setDelay(withDelay); // half second between each showcase view
//
//        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID_NAVDRAW);
//
//        sequence.setConfig(config);
//
//        sequence.addSequenceItem(
//                new MaterialShowcaseView.Builder(getActivity())
//                        .setDismissText("HO CAPITO")
//                        .withRectangleShape()
//                        .setMaskColour(Color.parseColor("#f11d5e88"))
//                        .setDismissTextColor(Color.parseColor("#F57C00"))
//                        .setContentText("test navigation drawer showcase")
//                        .build()
//        );
//
//        sequence.start();
//
//    }


    /**
     *
     */
}
