package samurai.geeft.android.geeft.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.baasbox.android.BaasBox;

import samurai.geeft.android.geeft.R;

/**
 * Created by joseph on 15/02/16.
 */
public class FeedbackPageActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RatingBar mRatingCommunication;
    private RatingBar mRatingCourtesy;
    private RatingBar mRatingReliability;
    private RatingBar mRatingDescription;
    private Button  mFeedbackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_geefted_page);

        mToolbar = (Toolbar) findViewById(R.id.feedback_page_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Feedback");

        mFeedbackButton = (Button) findViewById(R.id.feedback_submit_button);
        mRatingCommunication = (RatingBar) findViewById(R.id.ratingBarCommunication);
        mRatingCourtesy = (RatingBar) findViewById(R.id.ratingBarCourtesy);
        mRatingDescription = (RatingBar) findViewById(R.id.ratingBarDescription);
        mRatingReliability = (RatingBar) findViewById(R.id.ratingBarReliability);

        mFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double UserRatingCommunication = mRatingCommunication.getRating();
                double UserRatingDescription = mRatingDescription.getRating();
                double UserRatingReliability = mRatingReliability.getRating();
                double UserRatingCourtesy = mRatingCourtesy.getRating();
                //-- Feedback calculation. Communication 20% Reliability 30% Description 30% Courtesy 20%
                double UserFeedback = UserRatingCommunication*0.2+UserRatingDescription*0.3+UserRatingReliability*0.3+UserRatingCourtesy*0.2;
                Toast.makeText(getApplicationContext(),
                        "Feedback ricevuto. Il tuo feedback è: "+UserFeedback,Toast.LENGTH_SHORT).show();

            }
        });

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
