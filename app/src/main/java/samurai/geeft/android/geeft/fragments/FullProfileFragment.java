package samurai.geeft.android.geeft.fragments;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nvanbenschoten.motion.ParallaxImageView;
import com.squareup.picasso.Picasso;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSGetGeefterFullInformation;
import samurai.geeft.android.geeft.database.BaaSGetGeefterInformation;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanStringArray;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by joseph on 16/02/16.
 */
public class FullProfileFragment extends AppCompatActivity implements TaskCallbackBooleanStringArray {

    private static final String TAG = "SendReportActivity";

    //info dialog attributes---------------------
    private TextView mFullProfileUsername;
    private TextView mFullProfileUserFbId;
    private ImageView mFullProfilegUserImage;
    private TextView mFullProfileUserRank;
    private TextView mFullProfileUserGiven;
    private TextView mFullProfileUserReceived;
    private ParallaxImageView mFullProfileBackground;
    //-------------------------------------------
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_profile);

        mToolbar = (Toolbar) findViewById(R.id.full_profile_toolbar);
        mToolbar.setTitle("Profilo"); //You are a Geefter if you donate
        setSupportActionBar(mToolbar);

        mFullProfileUserRank = (TextView) findViewById(R.id.full_profile_page_ranking_score);
        mFullProfileUserGiven = (TextView) findViewById(R.id.full_profile_page_given_geeft);
        mFullProfileUserReceived = (TextView) findViewById(R.id.full_profile_page_received_geeft);
        mFullProfileUsername = (TextView) findViewById(R.id.full_profile_page_geefter_name);
        mFullProfilegUserImage = (ImageView) findViewById(R.id.full_profile_page_geefter_image);
        mFullProfileUserFbId = (TextView) findViewById(R.id.full_profile_page_geefter_fb_id);
        mFullProfileBackground = (ParallaxImageView) findViewById(R.id.full_profile_page_background);

        mFullProfileBackground.setTiltSensitivity(5);
        mFullProfileBackground.registerSensorManager();
        //TODO tenere la parallasse?!
        new BaaSGetGeefterFullInformation(getApplicationContext(),this).execute();

    }

    public void done(boolean result,String[] userInformations){
        /* mFullInformation[i] with i = {0,..,5}
            order = feedback,given,received,fbName,profilePicUri,fbId*/

        mFullProfileUserRank.setText(userInformations[0]);
        mFullProfileUserGiven.setText(userInformations[1]);
        mFullProfileUserReceived.setText(userInformations[2]);
        mFullProfileUsername.setText(userInformations[3]);
        Picasso.with(getApplicationContext()).load(Uri.parse(userInformations[4])).fit()
                .centerInside()
                .into(mFullProfilegUserImage);
        mFullProfileUserFbId.setText(userInformations[5]);

    }

}