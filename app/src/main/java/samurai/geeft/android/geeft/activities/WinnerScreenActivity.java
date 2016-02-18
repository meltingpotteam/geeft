package samurai.geeft.android.geeft.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanStringArray;

/**
 * Created by oldboy on 18/02/16.
 */
public class WinnerScreenActivity extends AppCompatActivity implements TaskCallbackBooleanStringArray {

    private static final String TAG = "WinnerScreenActivity";

    //info dialog attributes---------------------
    private TextView mWinnerScreenGeefterName;
    private TextView mWinnerScreenGeeftedName;
    private ImageView mWinnerScreenGeeftBackground;
    private ImageButton mWinnerScreenFbButton;
    private ImageButton mWinnerScreenLocationButton;
    //-------------------------------------------
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winner_screen);

        mToolbar = (Toolbar) findViewById(R.id.winner_screen_toolbar);
        mToolbar.setTitle("Winner"); //You are a Geefter if you donate
        setSupportActionBar(mToolbar);

        mWinnerScreenGeefterName = (TextView) findViewById(R.id.winner_screen_geefter_name);
        mWinnerScreenGeeftedName = (TextView) findViewById(R.id.winner_screen_geefted_name);
        mWinnerScreenGeeftBackground = (ImageView) findViewById(R.id.winner_screen_geeft_background);
        mWinnerScreenFbButton = (ImageButton) findViewById(R.id.winner_screen_facebook_button);
        mWinnerScreenLocationButton = (ImageButton) findViewById(R.id.winner_screen_location_button);


        mWinnerScreenFbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WinnerScreenActivity.this, "Start the communication with the Geefter", Toast.LENGTH_SHORT).show();
            }
        });
        mWinnerScreenLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WinnerScreenActivity.this, "Show approximate position of the Geef", Toast.LENGTH_SHORT).show();
            }
        });

//        new BaaSGetGeefterFullInformation(getApplicationContext(),this).execute();

    }

    public void done(boolean result,String[] userInformations){
        /* mFullInformation[i] with i = {0,..,5}
            order = feedback,given,received,fbName,profilePicUri,fbId*/

//        fare query e settare il ritorno

    }

}
