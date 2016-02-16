package samurai.geeft.android.geeft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasUser;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSSendSuggestions;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;

/**
 * Created by oldboy on 15/02/16.
 */
public class SendFeedbackActivity extends AppCompatActivity implements TaskCallbackBoolean {

    private TextView mFeedbackTitle;
    private TextView mFeedbackDesription;
    private Toolbar mToolbar;

    private String mUserDisplayName;
    private String mTitle;
    private String mDescription;

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


        mUserDisplayName = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).get("name");
        mTitle = mFeedbackTitle.getText().toString();
        mDescription = mFeedbackDesription.getText().toString();
        //TODO: Launch AsyncTask in a button listener
        new BaaSSendSuggestions(getApplicationContext(),mUserDisplayName,mTitle,mDescription,this).execute();
    }


    public void done(boolean result){
        //enables all social buttons
        if(result){
            Toast.makeText(getApplicationContext(),
                    "Report inviato", Toast.LENGTH_LONG).show();
            // SEND Hidden E-mail with title ,description and User Display Name
            BaasUser user = BaasUser.current();
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "geeft.app@gmail.com");
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Suggestion/Feedback from "
                    + mUserDisplayName);
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "User: " + user.getName() +
                    " \n" + "Title: " + mTitle + "\n" + "Description: " + mDescription);
            SendFeedbackActivity.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Errore nell'invio del Report",Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
