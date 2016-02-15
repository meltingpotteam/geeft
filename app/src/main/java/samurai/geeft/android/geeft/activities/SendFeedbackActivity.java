package samurai.geeft.android.geeft.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import samurai.geeft.android.geeft.R;

/**
 * Created by oldboy on 15/02/16.
 */
public class SendFeedbackActivity extends AppCompatActivity{

    private TextView mFeedbackTitle;
    private TextView mFeedbackDesription;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_report_help_page);

        mToolbar = (Toolbar) findViewById(R.id.fragment_feedback_help_page_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Feedback Report");
        Bundle bundle = new Bundle();

        mFeedbackTitle = (TextView) findViewById(R.id.fragment_feedback_help_page_title);
        mFeedbackDesription = (TextView) findViewById(R.id.fragment_feedback_help_page_description);
    }


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
}
