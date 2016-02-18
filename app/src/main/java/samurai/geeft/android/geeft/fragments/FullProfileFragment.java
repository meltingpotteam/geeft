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
import samurai.geeft.android.geeft.database.BaaSGetGeefterInformation;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by joseph on 16/02/16.
 */
public class FullProfileFragment extends AppCompatActivity {

    private static final String TAG = "SendReportActivity";

    //info dialog attributes---------------------
    private TextView mFullProfileUsername;
    private TextView mFullProfileUserLocation;
    private ImageView mFullProfilegUserImage;
    private TextView mFullProfileUserRank;
    private TextView mFullProfileUserGiven;
    private TextView mFullProfileUserReceived;
    private ImageButton mFullProfileFbButton;
    private ParallaxImageView mFullProfileBackground;
    //-------------------------------------------
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_profile);

        mToolbar = (Toolbar) findViewById(R.id.full_profile_toolbar);
        mToolbar.setTitle("Geefter Page");
        setSupportActionBar(mToolbar);

        mFullProfileUsername = (TextView) findViewById(R.id.full_profile_page_geefter_name);
        mFullProfileUserLocation = (TextView) findViewById(R.id.full_profile_page_geefter_location);
        mFullProfilegUserImage = (ImageView) findViewById(R.id.full_profile_page_geefter_image);
        mFullProfileUserRank = (TextView) findViewById(R.id.full_profile_page_ranking_score);
        mFullProfileUserGiven = (TextView) findViewById(R.id.full_profile_page_given_geeft);
        mFullProfileUserReceived = (TextView) findViewById(R.id.full_profile_page_received_geeft);
        mFullProfileFbButton = (ImageButton) findViewById(R.id.full_profile_page_facebook_button);

        mFullProfileBackground = (ParallaxImageView) findViewById(R.id.full_profile_page_background);

        mFullProfileBackground.setTiltSensitivity(5);
        mFullProfileBackground.registerSensorManager();
        //TODO tenere la parallasse?!
    }

/*

    holder.mProfileClickableArea.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(v.getContext()); //Read Update
            View dialogLayout = inflater.inflate(R.layout.profile_dialog, null);
            alertDialog.setView(dialogLayout);
            //On click, the user visualize can visualize some infos about the geefter
            android.app.AlertDialog dialog = alertDialog.create();

            //profile dialog fields-----------------------
            holder.mProfileDialogUsername = (TextView) dialogLayout.findViewById(R.id.dialog_geefter_name);
            holder.mProfileDialogUserLocation = (TextView) dialogLayout.findViewById(R.id.dialog_geefter_location);
            holder.mProfileDialogUserImage = (ImageView) dialogLayout.findViewById(R.id.dialog_geefter_profile_image);

            holder.mProfileDialogUserRank = (TextView) dialogLayout.findViewById(R.id.dialog_ranking_score);
            holder.mProfileDialogUserGiven = (TextView) dialogLayout.findViewById(R.id.dialog_given_geeft);
            holder.mProfileDialogUserReceived = (TextView) dialogLayout.findViewById(R.id.dialog_received_geeft);
            holder.mProfileDialogFbButton = (ImageButton) dialogLayout.findViewById(R.id.dialog_geefter_facebook_button);

            //--------------------------------------------
            holder.mProfileDialogUsername
                    .setText(item
                            .getUsername());
            holder.mProfileDialogBackground = (ParallaxImageView) dialogLayout.findViewById
                    (R.id.dialog_geefter_background);
            //--------------------------------------------
            holder.mProfileDialogUsername
                    .setText(item
                            .getUsername());
            holder.mProfileDialogUserLocation.setText(item.getUserLocation());
            Picasso.with(mContext).load(item.getUserProfilePic()).fit()
                    .centerInside()
                    .into(holder.mProfileDialogUserImage);
            //Show Facebook profile of geefter------------------------
            if(item.isAllowCommunication()){
                holder.mProfileDialogFbButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent facebookIntent = getOpenFacebookProfileIntent(mContext,item.getUserFbId());
                        mContext.startActivity(facebookIntent);
                    }
                });
            }
            else{
                holder.mProfileDialogFbButton.setVisibility(View.GONE);
            }


            //Parallax background -------------------------------------
            holder.mProfileDialogBackground.setTiltSensitivity(5);
            holder.mProfileDialogBackground.registerSensorManager();
            holder.mProfileDialogBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uriUrl = Uri.parse(WEBSITE_URL);
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    mContext.startActivity(launchBrowser);
                }
            });
            //TODO tenere la parallasse?!
            //---------------------------------------------------------

            //TODO: fill the fields "rank" , "geeven", "receeved"-------
            //----------------------------------------------------------
            //Relaunch AsyncTask anytime is needed for give information updated
            new BaaSGetGeefterInformation(mContext,holder,GeeftItemAdapter.this).execute();

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().getAttributes().windowAnimations = R.style.profile_info_dialog_animation;
            //                dialog.setMessage("Some information that we can take from the facebook shared one");
            dialog.show();  //<-- See This!
            //

        }
    });
    ////



    holder.mProfileDialogUserRank.setText(String.valueOf(userInformation[0]) + "/5.0");
    holder.mProfileDialogUserGiven.setText(String.valueOf((long)userInformation[1]));
    holder.mProfileDialogUserReceived.setText(String.valueOf((long)userInformation[2]));


    */
}