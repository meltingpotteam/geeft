package samurai.geeft.android.geeft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasUser;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSSendSuggestions;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;

/**
 * Created by oldboy on 15/02/16.
 */

public class SendReportActivity extends AppCompatActivity implements TaskCallbackBoolean {

    private static final String TAG = "SendReportActivity";

    private TextView mReportTitle;
    private TextView mReportDesription;
    private Toolbar mToolbar;

    private String mUserDisplayName;
    private String mTitle;
    private String mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_report_help_page);
        Bundle bundle = new Bundle();
        initActionBar();

        mReportTitle = (TextView) findViewById(R.id.fragment_feedback_help_page_title);
        mReportDesription = (TextView) findViewById(R.id.fragment_feedback_help_page_description);

        mReportDesription.setMinLines(1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        getMenuInflater().inflate(R.menu.toolbar_button_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
                    // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Log.d(TAG, "HOME");
                if(getSupportFragmentManager().getBackStackEntryCount()>0){
                    getSupportFragmentManager().popBackStack();
                }else {
                    super.onBackPressed();
                }
                return true;
            case R.id.fragment_add_geeft_ok_button:
                //Toast.makeText(this, "TEST OK BUTTON IN TOOLBAR ", Toast.LENGTH_SHORT).show();
                mUserDisplayName = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).get("name");
                mTitle = mReportTitle.getText().toString();
                mDescription = mReportDesription.getText().toString();
                //TODO: Launch AsyncTask in a button listener
                Log.d(TAG,"report_title: " + mTitle + " report_description: " + mDescription);
                if(mTitle.length() <= 1 || mDescription.length() <= 1){
                    Toast.makeText(getApplicationContext(), "Bisogna compilare tutti i campi prima di procedere", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else{
                    new BaaSSendSuggestions(getApplicationContext(),mUserDisplayName, mTitle, mDescription,this).execute();
                    finish();
                    return true;
                }
                ///////////////////////////////////////
        }
        return false;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void initActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("Manda un report");
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
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "geeft.app@gmail.com" });
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Suggestion/Feedback from "
                    + mUserDisplayName);
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "User: " + user.getName() +
                    " \n" + "Title: " + mTitle + "\n" + "Description: " + mDescription);
            SendReportActivity.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Errore nell'invio del Report",Toast.LENGTH_LONG).show();
        }
        finish();
    }


}
