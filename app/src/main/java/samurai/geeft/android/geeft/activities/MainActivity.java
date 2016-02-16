package samurai.geeft.android.geeft.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.adapters.ViewPagerAdapter;
import samurai.geeft.android.geeft.fragments.NavigationDrawerFragment;
import samurai.geeft.android.geeft.utilities.SlidingTabLayout;

/**
 * Created by ugookeadu on 20/01/16.
 * Updated by gabriel-dev on 29/01/16.
 * Update by danybr-dev on 2/02/16
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG ="MainActivity";
    private static final int REQUEST_CODE_LOGOUT = 0;

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private SlidingTabLayout mSlidingTabLayoutTabs;
    private CharSequence mTitles[]={"Profile","Geeft"};
    private FloatingActionButton mActionNewGeeft;
    private int mNumboftabs =2;

    /**
     * Facebook share button implementation..... If you make this better,make it!
     */
    CallbackManager mCallbackManager;
    static ShareDialog mShareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar)findViewById(R.id.main_app_bar);
        mViewPager = (ViewPager)findViewById(R.id.pager);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null)
            getSupportActionBar().setDisplayShowHomeEnabled(true);


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
        NavigationDrawerFragment drawerFragment =  (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);
        Log.d("LOG",""+drawerFragment);
        drawerFragment.setUp(R.id.navigation_drawer_fragment,
                (DrawerLayout)findViewById(R.id.drawer_layout),mToolbar);


        /**This is the floating menu button section; the button , when clicked, open a submenu
         that give the possibility to select the action that the user wat to do (the action button)
         clicked will start the associated activity.
        **/
        FloatingActionMenu floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floating_menu);
        floatingActionMenu.setClosedOnTouchOutside(true);
        mActionNewGeeft = (FloatingActionButton) findViewById(R.id.add_geeft_button);
        mActionNewGeeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Activity to 'Add Geeft' started", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AddGeeftActivity.class);
                startActivity(intent);
            }
        });
        FloatingActionButton actionGeeftAroundMe = (FloatingActionButton) findViewById(R.id.geeft_around_me_button);
        actionGeeftAroundMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddStoryActivity.class);
                startActivity(intent);
            }
        });
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
                    return getResources().getColor(R.color.white,null);
                }
                else {
                    return getResources().getColor(R.color.white);
                }
            }

        });

        // Setting the ViewPager For the SlidingTabsLayout
        mSlidingTabLayoutTabs.setViewPager(mViewPager);
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
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                //the positive button should call the "logout method"
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //here you can add functions
                    Intent i = new Intent(getApplicationContext(), LogoutActivity.class);
                    startActivityForResult(i, REQUEST_CODE_LOGOUT);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

        if(id == R.id.action_feedback){
            Intent intent = new Intent(MainActivity.this, SendFeedbackActivity.class);
            startActivity(intent);

        }

        if(id == R.id.action_feedback_geefted){
            Intent intent = new Intent(MainActivity.this, FeedbackPageActivity.class);
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

}
