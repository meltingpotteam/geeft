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

    //for the search activity
    private SearchView searchView;
    private MenuItem searchMenuItem;

    public static Intent newIntent(@NonNull Context context, ViewPagerAdapter viewPagerAdapter) {
        Intent intent = new Intent(context, SearchGeeftActivity.class);
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
                AlertDialog.Builder searchDialogBuilder = new AlertDialog.Builder(SearchGeeftActivity.this,
                        R.style.AppCompatAlertDialogStyle); //Read Update

                searchDialogBuilder.setTitle("Cerca");
                searchDialogBuilder.setMessage("In quale sezione desideri effettuare la ricerca?");
                searchDialogBuilder.setPositiveButton("Geeft", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * Start a fragment
                         */
                        Log.d(TAG, "FRAGMENT_CALLED");
                        mFragment = TabGeeftFragment.newInstance(true, query);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment)
                                .commit();
                    }
                });
                searchDialogBuilder.setNegativeButton("Geeftory", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * Start a fragment
                         */
                        //MAGHEGGIO PER EVITARE QUERY DOPPIE
                        //searchView.clearFocus();
                        Log.d(TAG, "FRAGMENT_CALLED");
                        mFragment = TabGeeftoryFragment.newInstance(true, query);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment)
                                .commit();
                    }
                });
//                Toast.makeText(SearchGeeftActivity.this, query, Toast.LENGTH_SHORT).show();
                AlertDialog searchDialog = searchDialogBuilder.create();
                //the context i had to use is the context of the dialog! not the context of the
                searchDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                searchDialog.show();
                searchView.clearFocus();    //clear the focus on the search tool
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
