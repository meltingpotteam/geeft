package samurai.geeft.android.geeft.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.baasbox.android.BaasUser;
import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.adapters.ViewPagerAdapter;
import samurai.geeft.android.geeft.fragments.NavigationDrawerFragment;
import samurai.geeft.android.geeft.utilities.RegistrationIntentService;
import samurai.geeft.android.geeft.utilities.SlidingTabLayout;
import samurai.geeft.android.geeft.utilities.TagsValue;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by ugookeadu on 20/01/16.
 * Updated by gabriel-dev on 29/01/16.
 * Update by danybr-dev on 2/02/16
 */
public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.OnDrawerClosedListener{
    private final String TAG ="MainActivity";
    private static final int REQUEST_CODE_LOGOUT = 0;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String SHOWCASE_ID_MAIN = "Showcase_single_use_main";

    private final String GEEFT_FOLDER = Environment.getExternalStorageDirectory()
            +File.separator+"geeft";

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private SlidingTabLayout mSlidingTabLayoutTabs;
    private CharSequence mTitles[]={"Geeft","Geeftory"};
    private FloatingActionButton mActionNewGeeft;
    private FloatingActionButton mActionGeeftStory;
    private int mNumboftabs =2;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    /**
     * Facebook share button implementation..... If you make this better,make it!
     */
    CallbackManager mCallbackManager;
    static ShareDialog mShareDialog;
    private DrawerLayout mDrawerLayout;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         *
         */
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(TagsValue.SENT_TOKEN_TO_SERVER, false);
                Log.d(TAG,"sent token? "+sentToken);
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
        /**
         *
         */

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar)findViewById(R.id.main_app_bar);
        mViewPager = (ViewPager)findViewById(R.id.pager);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null)
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        // TODO: Move this to where you establish a user session
        logUser();

        /**
        * Facebook shareButton implementation
        **/
        mCallbackManager = CallbackManager.Factory.create();
        mShareDialog = new ShareDialog(this);
        // this part is optional
        mShareDialog.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(TAG,"shareDialog success");
            }

            @Override
            public void onCancel() {
                Log.d(TAG,"shareDialog cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "shareDialog error");
            }
        });
        /**
         * End implementation
         */
        final FloatingActionMenu floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floating_menu);

        NavigationDrawerFragment drawerFragment =  (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);
        Log.d("LOG", "" + drawerFragment);
        drawerFragment.setUp(R.id.navigation_drawer_fragment, mDrawerLayout, mToolbar);


//        ViewTarget target = new ViewTarget(R.id.floating_menu, this);
//
//        new ShowcaseView.Builder(this)
//                .withMaterialShowcase()
//                .setTarget(target)
//                .setContentTitle("TestShowcase")
//                .setContentText("Questo è un test per l'implementazione dello ShowcaseView")
//                .hideOnTouchOutside()
//                .build();

        /**This is the floating menu button section; the button , when clicked, open a submenu
         that give the possibility to select the action that the user what to do (the action button)
         clicked will start the associated activity.
        **/
        floatingActionMenu.setClosedOnTouchOutside(true);
        mActionNewGeeft = (FloatingActionButton) findViewById(R.id.add_geeft_button);
        mActionNewGeeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Activity to 'Add Geeft' started", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AddGeeftActivity.class);
                startActivity(intent);
                floatingActionMenu.close(true);
            }
        });

        mActionGeeftStory = (FloatingActionButton) findViewById(R.id.geeft_around_me_button);
        mActionGeeftStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MainActivity.this, AddStoryActivity.class);
                startActivity(intent);
                floatingActionMenu.close(true); //TODO: Enable this after is stable,in beta version

                //TODO: This is for the alpha versione. Delete this before.
                /*new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Spiacenti")
                        .setMessage("Al momento stai usando la versione alpha, questa " +
                                "funzionalità non è ancora attiva.").show();*/
            }
        });

//        FloatingActionButton mActionGeeftSearch = (FloatingActionButton) findViewById(R.id.geeft_search_button);
//        mActionGeeftSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, SearchGeeftActivity.class);
//                startActivity(intent);
//                floatingActionMenu.close(true);
//            }
//        });
        /**
         * End implementation
         */
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles for the Tabs and Number Of Tabs.
        mViewPagerAdapter =  new ViewPagerAdapter(getSupportFragmentManager(),mTitles,mNumboftabs);

        // Assigning ViewPager View and setting the adapter
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mViewPagerAdapter);

        // Assiging the Sliding Tab Layout View
        mSlidingTabLayoutTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mSlidingTabLayoutTabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        mSlidingTabLayoutTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return getResources().getColor(R.color.white, null);
                } else {
                    return getResources().getColor(R.color.white);
                }
            }

        });

        // Setting the ViewPager For the SlidingTabsLayout
        mSlidingTabLayoutTabs.setViewPager(mViewPager);

        /**
         * implementation of tutorial behaviour
         */


//
//        floatingActionMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "METODO IS OPENED", Toast.LENGTH_SHORT).show();
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        presentShowcaseFabView(350);
//
//                    }
//                }, 1000);
//            }
//        });


//        if (!mActionGeeftStory.isHidden()){
//            Toast.makeText(MainActivity.this, "METODO IS HIDDEN", Toast.LENGTH_SHORT).show();
//            presentShowcaseFabView(350);
//        }

//        if (!drawerFragment.isMenuVisible() || drawerFragment.getProfileLayout().getVisibility() == View.GONE) {
////            Toast.makeText(MainActivity.this, "FUNGEEEE", Toast.LENGTH_SHORT).show();
//            presentShowcaseView(500);
//        }
//
//        mDrawerLayout.openDrawer(drawerFragment.getView());

    }

    /**
     * callback from drawer implementation
     */

    @Override
    public void onDrawerClosed() {
        presentShowcaseView(500);
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(TagsValue.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,
                    R.style.AppCompatAlertDialogStyle); //Read Update

            builder.setTitle(R.string.logout_dialog_title);
            builder.setMessage(R.string.logout_dialog_message);
            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                //the positive button should call the "logout method"
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //here you can add functions
                    Intent i = new Intent(getApplicationContext(), LogoutActivity.class);
                    startActivityForResult(i, REQUEST_CODE_LOGOUT);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                //cancel the intent
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //here you can add functions
                    dialog.dismiss();
                }
            });

            //On click, the user visualize can visualize some infos about the geefter
            AlertDialog dialog = builder.create();
            //the context i had to use is the context of the dialog! not the context of the
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.show();  //<-- See This!
            return true;
        }

        if(id == R.id.action_reset_tutorial){

            MaterialShowcaseView.resetAll(this);
            Toast.makeText(this, "All Showcases reset", Toast.LENGTH_SHORT).show();
            presentShowcaseView(350);

        }

        if(id == R.id.action_search){
            Intent intent = new Intent(MainActivity.this, SearchGeeftActivity.class);

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_LOGOUT) {
            if(resultCode == Activity.RESULT_OK){
                if(LogoutActivity.wasLogoutOk(data)) {
                    startLoginActivity();
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(),
                        "Logout non possibile, riprovare più tardi",Toast.LENGTH_LONG).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                Toast.makeText(getApplicationContext(),
                        "Logout cancellato",Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public static void showToastError(Context context){
        Toast.makeText(context,
                "E' accaduto un errore imprevisto!", Toast.LENGTH_LONG).show();
    }

    private void startLoginActivity() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    class MyPagerAdapter extends FragmentPagerAdapter{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return mNumboftabs;
        }
    }

    public static ShareDialog getShareDialog(){ return mShareDialog;}

    private void hideViews() {
        mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mActionNewGeeft.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        mActionNewGeeft.animate().translationY(mActionNewGeeft.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        mActionNewGeeft.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }
    private void logUser() {
        BaasUser current = BaasUser.current();
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12345");
        String fbName = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("name");
        Crashlytics.setUserEmail(fbName + "@fabric.io");
        Crashlytics.setUserName(fbName);
    }

//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//
//    }

    private void presentShowcaseView(int withDelay){
//        new MaterialShowcaseView.Builder(this)
//                .setTarget(mSlidingTabLayoutTabs)
//                .setTitleText("Hello")
//                .setDismissText("Ho Capito")
//                .setContentText("Queste solo ne zezioni thell'applicazione! \n Geeftory è la sezione in cui puoi trovare le sotrie degli oggetti \n Geeft è dove puoi vedere gli oggeti presenti su geeft e prenotare quello a cui sei interessato!")
//                .setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
//                .singleUse(SHOWCASE_ID_MAIN) // provide a unique ID used to ensure it is only shown once
//                .show();

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(withDelay); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID_MAIN);

//        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
//            @Override
//            public void onShow(MaterialShowcaseView itemView, int position) {
//                Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
//            }
//        });

        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(mSlidingTabLayoutTabs)
                        .setDismissText("OK")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_tabsections_text))
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(mSlidingTabLayoutTabs.getChildAt(0))
                        .setDismissText("HO CAPITO")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_geeftinfo_text))
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(mSlidingTabLayoutTabs.getChildAt(0))
                        .setDismissText("OK")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_geeftoryinfo_text))
                        .withRectangleShape()
                        .build()
        );

        sequence.start();

    }

}
