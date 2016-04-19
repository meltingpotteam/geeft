package samurai.geeft.android.geeft.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSGeeftHistoryArrayTask;
import samurai.geeft.android.geeft.fragments.GeeftStoryFragment;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Geeft;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by ugookeadu on 02/02/16.
 */
public class FullScreenViewActivity extends AppCompatActivity implements TaskCallbackBooleanToken{
    private final String TAG =""+this.getClass().getName();
    private static final String EXTRA_GEEFT_ID =
            "samurai.geeft.android.geeft.geeft_id";
    private final static String ARG_COLLECTION = "samurai.geeft.android.geeft.activities." +
            "FullScreenViewActivity_collection";
    private static final String SHOWCASE_ID_STORY = "Showcase_id_story";


    private ViewPager mViewPager;
    private List<Geeft> mGeeftList = new ArrayList<>();
    private Toolbar mToolbar;
    private static String mCollection;
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    private ProgressDialog mProgressDialog;
    //-------------------

    public static Intent newIntent(Context context, String geeftId, String collection) {
        Intent intent = new Intent(context, FullScreenViewActivity.class);
        intent.putExtra(EXTRA_GEEFT_ID, geeftId);
        mCollection = collection;
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "QUI");
        setContentView(R.layout.activity_full_screen_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setVisibility(View.VISIBLE);
        mViewPager = (ViewPager) findViewById(R.id.activity_full_screen_view_pager);
        if(savedInstanceState!=null)
                mCollection = savedInstanceState.getString(ARG_COLLECTION);

        mToolbar.setTitle("Storia del Geeft");
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mProgressDialog = new ProgressDialog(FullScreenViewActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Operazione in corso...");
        mProgressDialog.show();

        new BaaSGeeftHistoryArrayTask(getApplicationContext(),mGeeftList,
                getIntent().getStringExtra(EXTRA_GEEFT_ID),mCollection,this).execute();

        presentShowcaseView(450);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_COLLECTION, mCollection);
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
    public void done(boolean result, int resultToken) {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        if(result) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
                @Override
                public Fragment getItem(int position) {
                    Geeft geeft = mGeeftList.get(position);
                    GeeftStoryFragment geeftStoryFragment = new GeeftStoryFragment();
                    geeftStoryFragment.setGeeft(geeft);
                    geeftStoryFragment.setPosition(position);
                    geeftStoryFragment.setList(mGeeftList);
                    return geeftStoryFragment;
                }

                @Override
                public int getCount() {
                    return mGeeftList.size();
                }
            });
        }else {
            Toast toast;
            if (resultToken == RESULT_OK) {
                //DO SOMETHING
            } else if (resultToken == RESULT_SESSION_EXPIRED) {
                toast = Toast.makeText(getApplicationContext(), "Sessione scaduta,è necessario effettuare di nuovo" +
                        " il login", Toast.LENGTH_LONG);
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                toast.show();
            } else {
                new AlertDialog.Builder(FullScreenViewActivity.this)
                        .setTitle("Errore")
                        .setMessage("Operazione non possibile. Riprovare più tardi.").show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }

    // Showcase for timeline
    private void presentShowcaseView(int withDelay){
//        new MaterialShowcaseView.Builder(this)
//                .setTarget(mSlidingTabLayoutTabs)
//                .setTitleText("Hello")
//                .setDismissText("Ho Capito")
//                .setContentText("Queste solo ne zezioni thell'applicazione! \n Geeftory è la sezione in cui puoi trovare le sotrie degli oggetti \n Geeft è dove puoi vedere gli oggeti presenti su geeft e prenotare quello a cui sei interessato!")
//                .setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
//                .singleUse(SHOWCASE_ID_MAIN) // provide a unique ID used to ensure it is only shown once
//                .show();

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(withDelay); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID_STORY);

//        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
//            @Override
//            public void onShow(MaterialShowcaseView itemView, int position) {
//                Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
//            }
//        });

        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setDismissText("OK")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_geeftory_details))
                        .withoutShape()
                        .build()

        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setDismissText("OK")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_geeftory_behaviordescription))
                        .withoutShape()
                        .build()
        );

        sequence.start();

    }
}