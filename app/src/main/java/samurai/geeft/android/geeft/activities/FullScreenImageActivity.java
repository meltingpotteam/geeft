package samurai.geeft.android.geeft.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.fragments.FullScreenImageFragment;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 24/02/16.
 */
public class FullScreenImageActivity extends AppCompatActivity {
    private final String TAG =""+this.getClass().getName();
        private static final String EXTRA_GEEFT_LIST = "extra_geeft_list";

    private static final String EXTRA_POSITION = "extra_position";

    private ViewPager mViewPager;
    private List<Geeft> mGeeftList = new ArrayList<>();
    private View mBallView;
    private int mPosition;
    private Toolbar mToolbar;


    public static Intent newIntent(Context context,List geeftList,int position) {
        Intent intent = new Intent(context, FullScreenImageActivity.class);
        intent.putExtra(EXTRA_GEEFT_LIST, (ArrayList)geeftList);
        intent.putExtra(EXTRA_POSITION,position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        Log.d(TAG, "QUI");
        setContentView(R.layout.activity_full_screen_view);
        mGeeftList = (ArrayList)getIntent().getSerializableExtra(EXTRA_GEEFT_LIST);
        mPosition = getIntent().getIntExtra(EXTRA_POSITION,0);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setVisibility(View.VISIBLE);
        mToolbar.setBackgroundColor(Color.BLACK);
        mToolbar.setTitle("Storia del Geeft");
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViewPager();
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.activity_full_screen_view_pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Geeft geeft = mGeeftList.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable(FullScreenImageFragment.ARG_GEFFT, geeft);
                FullScreenImageFragment fullScreenImageFragment =
                        FullScreenImageFragment.newInstance(bundle);
                return fullScreenImageFragment;
            }

            @Override
            public int getCount() {
                return mGeeftList.size();
            }
        });
        mViewPager.setCurrentItem(mPosition);
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
