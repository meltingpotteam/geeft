package samurai.geeft.android.geeft.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nvanbenschoten.motion.ParallaxImageView;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.fragments.GeeftListFragment;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by danybr-dev on 15/02/16.
 **/
public class ReceivedActivity extends AppCompatActivity implements GeeftListFragment.OnGeeftImageSelectedListener{

    private final String TAG = getClass().getName();
    //info dialog attributes---------------------
    private TextView mReceivedDialogUsername;
    private TextView mReceivedDialogUserLocation;
    private ImageView mReceivedDialogUserImage;
    private ImageView mReceivedDialogFullImage;
    private ParallaxImageView mReceivedDialogBackground;
    private Button mReceivedStoryButton;
    private Button mReceivedGeeftButton;
    private  LayoutInflater inflater;
    private Toolbar mToolbar;
    //-------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO da rivedere assolutamente la logica
        setContentView(R.layout.container_for_fragment);
        Bundle bundle = new Bundle();
        inflater = LayoutInflater.from(ReceivedActivity.this);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = GeeftListFragment.newInstance(TagsValue.LINK_NAME_RECEIVED, false);
            fm.beginTransaction().add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
    @Override
    public void onImageSelected(Geeft geeft) { // give id of image
        // launch full screen activity
        Intent intent = FullGeeftDetailsActivity.newIntent(ReceivedActivity.this,
                geeft);
        ReceivedActivity.this.startActivity(intent);
    }
    @Override
    public void onImageSelected(String id) {}

   /* @Override
    public void onImageSelected(Geeft geeft) { // give id of image
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(ReceivedActivity.this); //Read Update
        View dialogLayout = inflater.inflate(R.layout.received_geeft_dialog, null);
        alertDialog.setView(dialogLayout);
        //On click, the user visualize can visualize some infos about the geefter
        android.app.AlertDialog dialog = alertDialog.create();

        //profile dialog fields-----------------------
        mReceivedDialogUsername = (TextView) dialogLayout.findViewById(R.id.dialog_geefter_name);
        mReceivedDialogUserLocation = (TextView) dialogLayout.findViewById(R.id.dialog_geefter_location);
        mReceivedDialogUserImage = (ImageView) dialogLayout.findViewById(R.id.dialog_geefter_profile_image);
        //Lasciamo gli stessi?!

        mReceivedDialogBackground = (ParallaxImageView) dialogLayout.findViewById(R.id.dialog_geefter_background);
        mReceivedStoryButton = (Button) dialogLayout.findViewById(R.id.received_dialog_storyButton);
        mReceivedGeeftButton = (Button) dialogLayout.findViewById(R.id.received_dialog_geeftButton);
        //--------------------------------------------
        Log.d(TAG,"Geeft: " + geeft.getGeeftImage());
        mReceivedDialogUsername
                .setText(geeft
                        .getUsername());
        //--------------------------------------------
        mReceivedDialogUsername
                .setText(geeft
                        .getUsername());
        mReceivedDialogUserLocation.setText(geeft.getUserLocation());
        Picasso.with(ReceivedActivity.this)
                .load(geeft.getUserProfilePic())
                .fit()
                .centerInside()
                .into(mReceivedDialogUserImage);

        //Parallax background -------------------------------------
        Picasso.with(ReceivedActivity.this)
                .load(geeft.getGeeftImage())
                .fit()
                .centerInside()
                .into(mReceivedDialogBackground);
        mReceivedDialogBackground.setTiltSensitivity(5);
        mReceivedDialogBackground.registerSensorManager();
        //------------- Story Button
        mReceivedStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: Story button
            }
        });
        //------------------------

        //--------------- GeeftButton
        mReceivedGeeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Geeft Button
            }
        });
        //------------------------

        //Listener for te imageView: -----------------------------------
        mReceivedDialogBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReceivedActivity.this); //Read Update
                LayoutInflater inflater = ReceivedActivity.this.getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.geeft_image_dialog, null);
                alertDialog.setView(dialogLayout);

                //On click, the user visualize can visualize some infos about the geefter
                AlertDialog dialog = alertDialog.create();
                //the context i had to use is the context of the dialog! not the context of the app.
                //"dialog.findVie..." instead "this.findView..."

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                mReceivedDialogFullImage = (ImageView) dialogLayout.findViewById(R.id.dialogGeeftImage);
                mReceivedDialogFullImage.setImageDrawable(mReceivedDialogBackground.getDrawable());



                dialog.getWindow().getAttributes().windowAnimations = R.style.scale_up_animation;
                //dialog.setMessage("Some information that we can take from the facebook shared one");
                dialog.show();  //<-- See This!
                //Toast.makeText(getApplicationContext(), "TEST IMAGE", Toast.LENGTH_LONG).show();

            }
        });
        //--------------------------------------------------------------


        //TODO tenere la parallasse?!
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.profile_info_dialog_animation;
        //                dialog.setMessage("Some information that we can take from the facebook shared one");
        //Log.d(TAG,"Show!");
        dialog.show();  //<-- See This!

    }*/

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
}
