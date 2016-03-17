package samurai.geeft.android.geeft.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.fragments.ListCategoryFragment;
import samurai.geeft.android.geeft.fragments.TabGeeftFragment;
import samurai.geeft.android.geeft.models.Category;

/**
 * Created by ugookeadu on 02/03/16.
 */
public class CategoryActivity extends AppCompatActivity implements ListCategoryFragment.OnCategorySelectedListener{

    private final String TAG = getClass().getSimpleName() ;

    public static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context, CategoryActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_for_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        Log.d(TAG, "oncreate out entries: "+getSupportFragmentManager().getBackStackEntryCount());
        if (fragment == null) {
            Bundle b = new Bundle();
            Log.d(TAG, "oncreate in entries: "+getSupportFragmentManager().getBackStackEntryCount());
            fragment = ListCategoryFragment.newInstance();
            fm.beginTransaction().add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onCategorySelected(Category category) {
        TabGeeftFragment fragment =
                TabGeeftFragment.newInstance(true, category);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
        Log.d(TAG, getFragmentManager().getBackStackEntryCount() + "");
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
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "back pressed entries: " + getSupportFragmentManager().getBackStackEntryCount());
    }


}
