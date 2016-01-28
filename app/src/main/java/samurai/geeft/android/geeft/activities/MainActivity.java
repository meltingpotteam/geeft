package samurai.geeft.android.geeft.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.fragments.GeeftListFragment;
import samurai.geeft.android.geeft.fragments.NavigationDrawerFragment;
import samurai.geeft.android.geeft.tabs.SlidingTabLayout;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar)findViewById(R.id.app_bar);
        mViewPager = (ViewPager)findViewById(R.id.pager);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null)
            getSupportActionBar().setDisplayShowHomeEnabled(true);


        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new GeeftListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
        NavigationDrawerFragment drawerFragment =  (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);
        Log.d("LOG",""+drawerFragment);
        drawerFragment.setUp(R.id.navigation_drawer_fragment,
                (DrawerLayout)findViewById(R.id.drawer_layout),mToolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        /*if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(),"Logout",Toast.LENGTH_LONG);
            return true;
        }*/
        return super.onOptionsItemSelected(item);
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
            return 0;
        }
    }
}
