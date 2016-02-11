package samurai.geeft.android.geeft.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSGeeftHistoryArrayTask;
import samurai.geeft.android.geeft.fragments.GeeftStoryFragment;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 02/02/16.
 */
public class FullScreenViewActivity extends AppCompatActivity implements TaskCallbackBoolean{
    private final String TAG =""+this.getClass().getName();
    private static final String EXTRA_GEEFT_ID =
            "samurai.geeft.android.geeft.geeft_id";

    private ViewPager mViewPager;
    private List<Geeft> mGeeftList = new ArrayList<>();
    private View mBallView;
    private Toolbar mToolbar;


    public static Intent newIntent(Context context, String geeftId) {
        Intent intent = new Intent(context, FullScreenViewActivity.class);
        intent.putExtra(EXTRA_GEEFT_ID, geeftId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "QUI");
        setContentView(R.layout.activity_full_screen_view);
        mViewPager = (ViewPager) findViewById(R.id.activity_full_screen_view_pager);
        mBallView = findViewById(R.id.loading_balls);

        new BaaSGeeftHistoryArrayTask(getApplicationContext(),mGeeftList,
                getIntent().getStringExtra(EXTRA_GEEFT_ID),this).execute();
    }


    @Override
    public void done(boolean result) {
        mBallView.setVisibility(View.GONE);
        if(result) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
                @Override
                public Fragment getItem(int position) {
                    Geeft geeft = mGeeftList.get(position);
                    GeeftStoryFragment geeftStoryFragment = new GeeftStoryFragment();
                    geeftStoryFragment.setGeeft(geeft);
                    return geeftStoryFragment;
                }

                @Override
                public int getCount() {
                    return mGeeftList.size();
                }
            });
        }else {
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Errore")
                    .setMessage("Operazione non possibile. Riprovare più tardi.").show();
        }
    }
}