package samurai.geeft.android.geeft.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.text.BreakIterator;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSGetStatistics;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanArrayToken;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by danybr-dev on 22/04/16.
 */
public class StatisticsActivity extends AppCompatActivity implements TaskCallbackBooleanArrayToken{
    private Toolbar mToolbar;
    private TextView mBaasboxUsersNumber;
    private TextView mFacebookUsersNumber;
    private TextView mGoogleUsersNumber;
    private ProgressDialog mProgressDialog;
    private double mInfo[];
    private TextView mMenNumber;
    private TextView mFemalesNumber;
    private TextView mNaGender;
    private TextView mGeeftsNumber;
    private TextView mGeeftorysNumber;
    private TextView mGeeftsNotAssigned;
    private TextView mPercentualFromRome;
    private TextView mGeeftsNotClosed;


    public static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context, StatisticsActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_get_statistics);
        Log.d("StatisticsActivity","Lanciato");
        initUI();
        initActionBar();
    }

    private void initUI() {
        mBaasboxUsersNumber = (TextView) findViewById(R.id.number_geeft_users);
        mFacebookUsersNumber = (TextView) findViewById(R.id.number_facebook_users);
        mGoogleUsersNumber = (TextView) findViewById(R.id.number_google_users);
        mMenNumber = (TextView) findViewById(R.id.number_females);
        mFemalesNumber = (TextView) findViewById(R.id.number_men);
        mNaGender = (TextView) findViewById(R.id.number_na_gender);
        mGeeftsNotClosed = (TextView) findViewById(R.id.number_geefts_not_closed);
        mGeeftsNotAssigned = (TextView) findViewById(R.id.number_geefts_not_assigned);
        mGeeftsNumber = (TextView) findViewById(R.id.number_geefts);
        mGeeftorysNumber = (TextView) findViewById(R.id.number_geeftorys);
        mPercentualFromRome = (TextView) findViewById(R.id.percentual_from_rome);
        showProgressDialog();
        new BaaSGetStatistics(getApplicationContext(),this).execute();
    }


    private void initActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar!=null){
            setSupportActionBar(mToolbar);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            mToolbar.setTitle("Statistiche");
        }
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(StatisticsActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Attendere");
        mProgressDialog.show();
    }

    public void done(boolean result,double[] mInfo,int mResultToken){
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
        if(result){
            mBaasboxUsersNumber.setText((int)mInfo[0]+"");
            mFacebookUsersNumber.setText((int)mInfo[1]+"");
            mGoogleUsersNumber.setText((int)mInfo[2]+"");
            mMenNumber.setText((int)mInfo[3]+"");
            mFemalesNumber.setText((int)mInfo[4]+"");
            mNaGender.setText((int)mInfo[5]+"");
            mGeeftsNotClosed.setText((int)mInfo[10]+"");
            mGeeftsNotAssigned.setText((int)mInfo[6]+"");
            mGeeftsNumber.setText((int)mInfo[7]+"");
            mGeeftorysNumber.setText((int)mInfo[8]+"");
            mPercentualFromRome.setText(mInfo[9]+"%");
        }
        else{
            Toast.makeText(getApplicationContext(),"E' accaduto un errore",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
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
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }
}
