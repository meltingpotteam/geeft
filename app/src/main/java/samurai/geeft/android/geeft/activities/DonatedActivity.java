package samurai.geeft.android.geeft.activities;

/**
 * Created by oldboy on 17/02/16.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nvanbenschoten.motion.ParallaxImageView;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.fragments.GeeftReceivedListFragment;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by danybr-dev on 15/02/16.
 */
public class DonatedActivity extends AppCompatActivity implements GeeftReceivedListFragment.OnGeeftImageSelectedListener{

    private final String TAG = getClass().getName();

    private final static String EXTRA_COLLECTION = "extra_collection";
    private final static String EXTRA_SHOW_WINNER_DIALOG = "extra_show_winner_dialog";

    //info dialog attributes---------------------
    private TextView mReceivedDialogUsername;
    private TextView mReceivedDialogUserLocation;
    private ImageView mReceivedDialogUserImage;
    private ImageView mReceivedDialogFullImage;
    private ParallaxImageView mReceivedDialogBackground;
    private Button mReceivedStoryButton;
    private Button mReceivedGeeftButton;
    private LayoutInflater inflater;
    private Toolbar mToolbar;
    //-------------------------------------------

    public static Intent newIntent(Context context, String collection, boolean showWinnerDialog) {
        Intent intent = new Intent(context, DonatedActivity.class);
        intent.putExtra(EXTRA_COLLECTION, collection);
        intent.putExtra(EXTRA_SHOW_WINNER_DIALOG, showWinnerDialog);
        return intent;
    }
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO da rivedere assolutamente la logica
        setContentView(R.layout.container_for_fragment);
        inflater = LayoutInflater.from(DonatedActivity.this);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            Bundle b = new Bundle();
            b.putString("link_name","donated");
            fragment = GeeftReceivedListFragment
                    .newInstance(TagsValue.LINK_NAME_DONATED, false);
            fm.beginTransaction().add(R.id.fragment_container, fragment)
                    .commit();
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO da rivedere assolutamente la logica
        setContentView(R.layout.container_for_fragment);
        inflater = LayoutInflater.from(DonatedActivity.this);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        Log.d(TAG, "" + getIntent().getStringExtra(EXTRA_COLLECTION));
        if (fragment == null) {
            fragment = GeeftReceivedListFragment
                    .newInstance(getIntent().getStringExtra(EXTRA_COLLECTION),
                            getIntent().getBooleanExtra(EXTRA_SHOW_WINNER_DIALOG,false));
            fm.beginTransaction().add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onImageSelected(String id) {}

    @Override
    public void onImageSelected(Geeft geeft) { // give id of image
        Intent intent;
        // launch full screen activity
        if(geeft.isAssigned() && !geeft.isGiven()){ // if geeft.isAssigned() && geeft.Feed
            intent = CompactDialogActivity.newIntent(DonatedActivity.this,geeft);
        }else {
            intent = FullGeeftDetailsActivity.newIntent(DonatedActivity.this,
                    geeft);
        }
        DonatedActivity.this.startActivity(intent);
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

