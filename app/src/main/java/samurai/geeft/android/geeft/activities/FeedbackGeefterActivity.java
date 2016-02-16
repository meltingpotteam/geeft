package samurai.geeft.android.geeft.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasUser;

import samurai.geeft.android.geeft.R;

/**
 * Created by joseph on 15/02/16.
 */
public class FeedbackGeefterActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RatingBar mRatingCommunication;
    private RatingBar mRatingCourtesy;
    private RatingBar mRatingReliability;
    private RatingBar mRatingDescription;
    private Button  mFeedbackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_geefter_page);

        mToolbar = (Toolbar) findViewById(R.id.feedback_page_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Feedback");

        mFeedbackButton = (Button) findViewById(R.id.feedback_submit_button);
        mRatingCommunication = (RatingBar) findViewById(R.id.ratingBarCommunication);
        mRatingCourtesy = (RatingBar) findViewById(R.id.ratingBarCourtesy);
        mRatingReliability = (RatingBar) findViewById(R.id.ratingBarReliability);

        mFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double UserRatingCommunication = mRatingCommunication.getRating();
                double UserRatingReliability = mRatingReliability.getRating();
                double UserRatingCourtesy = mRatingCourtesy.getRating();
                //-- Feedback calculation. Communication 40% Reliability 25% Courtesy 35%
                double UserFeedback = UserRatingCommunication*0.4+UserRatingReliability*0.25+UserRatingCourtesy*0.35;
                if (UserFeedback==0) { UserFeedback = 0.1; }
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
