package samurai.geeft.android.geeft.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.fragments.FullGeeftDeatailsFragment;
import samurai.geeft.android.geeft.models.Geeft;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by ugookeadu on 20/02/16.
 */
public class FullGeeftDetailsActivity extends AppCompatActivity{
    private static final String EXTRA_CONTEXT = "extra_context";

    private final String TAG = getClass().getSimpleName();
    private Geeft mGeeft;
    private ProgressDialog mProgress;
    private final static String EXTRA_GEFFT = "geeft";

    public static Intent newIntent(@NonNull Context context, @NonNull Geeft geeft) {
        Intent intent = new Intent(context, FullGeeftDetailsActivity.class);
        intent.putExtra(EXTRA_GEFFT, geeft);
        intent.putExtra(EXTRA_CONTEXT,context.getClass().getSimpleName());
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_for_fragment);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                mGeeft = new Geeft();
            } else {
                mGeeft = (Geeft)extras.getSerializable(EXTRA_GEFFT);
            }
        } else {
            mGeeft = (Geeft) savedInstanceState.getSerializable(EXTRA_GEFFT);
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            Bundle b = new Bundle();
            b.putSerializable(FullGeeftDeatailsFragment.GEEFT_KEY,mGeeft);
            fragment = FullGeeftDeatailsFragment.newInstance(mGeeft,
                    getIntent().getStringExtra(EXTRA_CONTEXT));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgress!=null){
            mProgress.dismiss();
        }
    }



}
