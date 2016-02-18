package samurai.geeft.android.geeft.activities;

/**
 * Created by oldboy on 17/02/16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nvanbenschoten.motion.ParallaxImageView;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.fragments.GeeftReceivedListFragment;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by danybr-dev on 15/02/16.
 */
public class DonatedActivity extends AppCompatActivity implements GeeftReceivedListFragment.OnGeeftImageSelectedListener{

    private final String TAG = getClass().getName();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO da rivedere assolutamente la logica
        setContentView(R.layout.container_for_fragment);
        Bundle bundle = new Bundle();
        inflater = LayoutInflater.from(DonatedActivity.this);
        GeeftReceivedListFragment geeftReceivedListFragment = new GeeftReceivedListFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.fragment_container, geeftReceivedListFragment)
                .commit();
    }

    @Override
    public void onImageSelected(String id) {}

    @Override
    public void onImageSelected(Geeft geeft) { // give id of image
        // launch full screen activity
        Intent intent = FullScreenViewActivity.newIntent(DonatedActivity.this,
        geeft.getId(),"geeft");
        DonatedActivity.this.startActivity(intent);
    }
}

