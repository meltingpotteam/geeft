package samurai.geeft.android.geeft.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.adapters.ViewPagerAdapter;
import samurai.geeft.android.geeft.fragments.TabGeeftFragment;
import samurai.geeft.android.geeft.fragments.TabGeeftoryFragment;

/**
 * Created by gabriel-dev on 06/03/16.
 */
public class SearchGeeftActivity extends AppCompatActivity {

    private static final String EXTRA_VIEW_PAGER = "extra_view_pager";
    private final String TAG = getClass().getSimpleName() ;

    private Toolbar mToolbar;
    private Fragment mFragment;
    private static String mSearchSection;

    //for the search activity
    private SearchView searchView;
    private MenuItem searchMenuItem;

    public static Intent newIntent(@NonNull Context context, ViewPagerAdapter viewPagerAdapter, String searchSection) {
        Intent intent = new Intent(context, SearchGeeftActivity.class);
        mSearchSection = searchSection;
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_geeft_activity_container);

        mToolbar = (Toolbar)findViewById(R.id.search_bar);
        mToolbar.setTitle("Cerca Geeft");
        if (mToolbar != null)
            setSupportActionBar(mToolbar);
        //for the fragment
        if (mFragment == null) {
            FragmentManager fm = getSupportFragmentManager();
            mFragment = fm.findFragmentById(R.id.fragment_container);
        }
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Log.d(TAG, "ON_CREATE_SEARCH_CALLED");

        Toast.makeText(SearchGeeftActivity.this, mSearchSection, Toast.LENGTH_SHORT ).show();

//
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
//        Log.d(TAG, "oncreate out entries: "+getSupportFragmentManager().getBackStackEntryCount());
//        if (fragment == null) {
//            Bundle b = new Bundle();
//            Log.d(TAG, "oncreate in entries: "+getSupportFragmentManager().getBackStackEntryCount());
//            fragment = SearchGeeftFragment.newInstance(false);
//            fm.beginTransaction().add(R.id.fragment_container, fragment)
//                    .commit();
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if(getSupportFragmentManager().getBackStackEntryCount()>0){
                    getSupportFragmentManager().popBackStack();
                    Log.d(TAG, "back stack entries: " + getSupportFragmentManager().getBackStackEntryCount());
                }else {
                    super.onBackPressed();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO rotation error, implement on restore for the toolbar elements

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
//        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
//        super.onRestoreInstanceState(savedInstanceState);
//        mToolbar = (Toolbar)findViewById(R.id.search_bar);
//        mToolbar.setTitle("Cerca Geeft");
//        if (mToolbar != null)
//            setSupportActionBar(mToolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.search_activity_menu, menu);


        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_searchactivity_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        //to have the bar always open
        searchView.setIconified(false);

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
//        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {
                Log.d(TAG, "ON_QUERY_TEXT_SUBMIT_CALLED");

                if (mSearchSection.equals("Geeft")){
                    mFragment = TabGeeftFragment.newInstance(true, query);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment)
                            .commit();
                }
                if (mSearchSection.equals("Geeftory")){
                    mFragment = TabGeeftoryFragment.newInstance(true, query);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment)
                            .commit();
                }

                searchView.clearFocus();    //clear the focus on the search tool
                return true;
            }


            @Override
            public boolean onQueryTextChange(String query) {
//                //removes the previous called fragment to avoid the time waiting
//                if (query.length() > 3) {
//                    Toast.makeText(SearchGeeftActivity.this, "fragment removed", Toast.LENGTH_SHORT ).show();
//                    getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
//                }
//                if (query.length() >= 3) {
//                    mFragment = TabGeeftFragment.newInstance(true, query);
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment)
//                            .commit();
//                }
                return true;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "back pressed entries: " + getSupportFragmentManager().getBackStackEntryCount());
    }


}
