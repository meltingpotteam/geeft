package samurai.geeft.android.geeft.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.fragments.ListCategoryFragment;
import samurai.geeft.android.geeft.fragments.SearchGeeftFragment;
import samurai.geeft.android.geeft.fragments.TabGeeftFragment;
import samurai.geeft.android.geeft.fragments.TabGeeftoryFragment;
import samurai.geeft.android.geeft.models.Category;

/**
 * Created by gabriel-dev on 06/03/16.
 */
public class SearchGeeftActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName() ;

    private Toolbar mToolbar;

    //for the search activity
    private SearchView searchView;
    private MenuItem searchMenuItem;

    public static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context, SearchGeeftActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_geeft_activity_container);

        mToolbar = (Toolbar)findViewById(R.id.search_bar);
        mToolbar.setTitle("Cerca Geeft");
        setSupportActionBar(mToolbar);
        //for the fragment
        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Log.d(TAG, "ON_CREATE_SEARCH_CALLED");

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_activity_menu, menu);


        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        //to have the bar always open
        searchView.setIconified(false);
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
//        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "ON_QUERY_TEXT_SUBMIT_CALLED");
                Toast.makeText(SearchGeeftActivity.this, query, Toast.LENGTH_SHORT).show();
                /**
                 * Start a fragment
                 */
                //MAGHEGGIO PER EVITARE QUERY DOPPIE
//                searchView.clearFocus();
                searchView.setIconified(true);
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = fm.findFragmentById(R.id.fragment_container);
                if (fragment == null) {
                    Log.d(TAG, "FRAGMENT_CALLED");
                    fragment = TabGeeftFragment.newInstance(false );
                    fm.beginTransaction().add(R.id.fragment_container, fragment)
                            .commit();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                friendListAdapter.getFilter().filter(newText);
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
