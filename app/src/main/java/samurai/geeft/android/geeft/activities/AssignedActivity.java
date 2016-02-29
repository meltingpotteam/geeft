package samurai.geeft.android.geeft.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.fragments.GeeftReceivedListFragment;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 18/02/16.
 */
public class AssignedActivity extends AppCompatActivity implements GeeftReceivedListFragment.OnGeeftImageSelectedListener{
    private final String TAG = getClass().getName();

    private final static String EXTRA_COLLECTION = "extra_collection";
    private final static String EXTRA_SHOW_WINNER_DIALOG = "extra_show_winner_dialog";

    //info dialog attributes---------------------
    private LayoutInflater inflater;
    //-------------------------------------------

    public static Intent newIntent(Context context, String collection, boolean showWinnerDialog) {
        Intent intent = new Intent(context, AssignedActivity.class);
        intent.putExtra(EXTRA_COLLECTION, collection);
        intent.putExtra(EXTRA_SHOW_WINNER_DIALOG, showWinnerDialog);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO da rivedere assolutamente la logica
        setContentView(R.layout.container_for_fragment);
        inflater = LayoutInflater.from(AssignedActivity.this);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        Log.d(TAG,""+getIntent().getStringExtra(EXTRA_COLLECTION));
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
        // launch full screen activity
        Intent intent = FullScreenViewActivity.newIntent(AssignedActivity.this,
                geeft.getId(),"geeft");
        AssignedActivity.this.startActivity(intent);
    }
}
