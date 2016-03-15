package samurai.geeft.android.geeft.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSUpdateUserFeedback;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by joseph on 15/02/16.
 */
public class FeedbackPageActivity extends AppCompatActivity implements TaskCallbackBooleanToken {

    private final String TAG = getClass().getSimpleName();
    private Toolbar mToolbar;
    private RatingBar mRatingCommunication;
    private RatingBar mRatingCourtesy;
    private RatingBar mRatingReliability;
    private RatingBar mRatingDescription;
    private TextView mTextViewFeedbackDescription;
    private EditText mRatingComment;
    private Button  mFeedbackButton;
    private final static String EXTRA_GEEFT = "extra_geeft";
    private static final String EXTRA_CONTEXT = "extra_context";
    private static final String EXTRA_BAASBOX_NAME = "extra_baasbox_name";
    private static final String EXTRA_IS_GEEFTER = "extra_is_geefter";

    private Geeft mGeeft;
    private String mCallingActivity;
    private String mUsername;
    private boolean mIamGeefter;

    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    //-------------------

    public static Intent newIntent(@NonNull Context context, @NonNull Geeft geeft,String username,boolean isGeefter) {
        Intent intent = new Intent(context, FeedbackPageActivity.class);
        intent.putExtra(EXTRA_GEEFT, geeft);
        intent.putExtra(EXTRA_CONTEXT,context.getClass().getSimpleName());
        intent.putExtra(EXTRA_BAASBOX_NAME,username);
        intent.putExtra(EXTRA_IS_GEEFTER, isGeefter);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feedback_geefted_page);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                mGeeft = new Geeft();
            } else {
                mGeeft = (Geeft)extras.getSerializable(EXTRA_GEEFT);
                mCallingActivity = extras.getString(EXTRA_CONTEXT);
                mUsername = extras.getString(EXTRA_BAASBOX_NAME);
                mIamGeefter = extras.getBoolean(EXTRA_IS_GEEFTER);

            }
        } else {
            mGeeft = (Geeft) savedInstanceState.getSerializable(EXTRA_GEEFT);
            mCallingActivity = savedInstanceState.getString(EXTRA_CONTEXT);
            mUsername = savedInstanceState.getString(EXTRA_BAASBOX_NAME);
            mIamGeefter = savedInstanceState.getBoolean(EXTRA_IS_GEEFTER);
        }

        if(mIamGeefter){
            if(mGeeft.isFeedbackLeftByGeefter()){
                showDialogLeftFeedback();
            }
        }
        else{
            if(mGeeft.isFeedbackLeftByGeefted()){
                showDialogLeftFeedback();
            }
        }

        initUI();

        mFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendFeedback();
            }
        });

    }

    private void initUI(){
        // mToolbar = (Toolbar) findViewById(R.id.feedback_page_toolbar);
        //setSupportActionBar(mToolbar);
        //mToolbar.setTitle("Feedback");

        mFeedbackButton = (Button) findViewById(R.id.feedback_submit_button);
        mRatingCommunication = (RatingBar) findViewById(R.id.ratingBarCommunication);
        mRatingCourtesy = (RatingBar) findViewById(R.id.ratingBarCourtesy);
        mRatingDescription = (RatingBar) findViewById(R.id.ratingBarDescription);
        mRatingReliability = (RatingBar) findViewById(R.id.ratingBarReliability);
        mRatingComment = (EditText) findViewById(R.id.fragment_feedback_help_page_description);
        mTextViewFeedbackDescription = (TextView) findViewById(R.id.text_view_feedback_description);

        Log.d(TAG, "getSimpleName su questa classe:" + TAG);
       // if(mCallingActivity.equals("AssignedActivity")){
        if(mIamGeefter){
            mRatingDescription.setVisibility(View.GONE);
            Log.d(TAG, "is null? " + (mTextViewFeedbackDescription == null));
            mTextViewFeedbackDescription.setVisibility(View.GONE);
        }
    }

    private void sendFeedback(){

        double userRatingCommunication = mRatingCommunication.getRating();
        double userRatingReliability = mRatingReliability.getRating();
        double userRatingCourtesy = mRatingCourtesy.getRating();
        double userRatingDescription;
        String userRatingComment = mRatingComment.getText().toString();


        //if(mCallingActivity.equals("AssignedActivity")){ // I'm Geefter,I can't set Geeft description
        if(mIamGeefter){
            userRatingDescription = 0;
        }
        else{
            userRatingDescription = mRatingDescription.getRating();
        }

        double[] feedbackArray = {
                userRatingCommunication,
                userRatingReliability,
                userRatingCourtesy,
                userRatingDescription
        };

        //TODO: ASyncTask
        new BaaSUpdateUserFeedback(getApplicationContext(),mGeeft.getId(),mUsername,feedbackArray,userRatingComment,mIamGeefter,this).execute();
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

    public void done(boolean result,int resultToken){
        if(result){
            new AlertDialog.Builder(FeedbackPageActivity.this)
                    .setTitle("Successo")
                    .setMessage("Grazie per il tuo Feedback.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startMainActivity();
                        }
                    })
                    .show();
        }
        else{
            if(resultToken == RESULT_SESSION_EXPIRED){
                Log.e(TAG,"Invalid Session token");
                startLoginActivity();
            }
            else{
                Log.e(TAG,"Error occured");
                new AlertDialog.Builder(FeedbackPageActivity.this)
                        .setTitle("Errore")
                        .setMessage("Operazione non possibile. Riprovare più tardi.").show();
            }
        }
    }

    private void startLoginActivity(){
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);

    }

    private void startMainActivity(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    private void showDialogLeftFeedback(){
        new AlertDialog.Builder(FeedbackPageActivity.this)
                .setTitle("Errore")
                .setMessage("Hai già lasciato il tuo feedback per questo Geeft. Grazie.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startMainActivity();
                    }
                })
                .show();
    }
/*
    public void done(boolean result){
        //enables all social buttons
        if(result){
            Toast.makeText(getApplicationContext(),
                    "Report inviato", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Errore nell'invio del Report",Toast.LENGTH_LONG).show();
        }
        finish();

    }
*/
}
