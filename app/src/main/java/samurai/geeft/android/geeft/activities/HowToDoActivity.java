package samurai.geeft.android.geeft.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.fragments.HowToDoFragment;

/**
 * Created by ugookeadu on 18/02/16.
 */
public class HowToDoActivity extends AppCompatActivity {
    private static final String EXTRA_STORY_ID =
            "samurai.geeft.android.geeft.story_id";
    private final String TAG = getClass().getSimpleName();

    public static Intent newIntent(Context context, String geeftId) {
        Intent intent = new Intent(context, HowToDoActivity.class);
        intent.putExtra(EXTRA_STORY_ID, geeftId);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_for_fragment);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = HowToDoFragment.newInstance(new Bundle());
            fm.beginTransaction().add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Log.d(TAG, "HOME");
                if(getSupportFragmentManager().getBackStackEntryCount()>0){
                    getSupportFragmentManager().popBackStack();
                }else {
                    super.onBackPressed();
                }
        }
        return super.onOptionsItemSelected(item);
    }



}
