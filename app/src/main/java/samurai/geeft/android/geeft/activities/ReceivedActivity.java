package samurai.geeft.android.geeft.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nvanbenschoten.motion.ParallaxImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSGetGeefterInformation;
import samurai.geeft.android.geeft.database.BaaSUploadGeeft;
import samurai.geeft.android.geeft.fragments.AddGeeftFragment;
import samurai.geeft.android.geeft.fragments.GeeftStoryListFragment;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by danybr-dev on 15/02/16.
 */
public class ReceivedActivity extends AppCompatActivity implements GeeftStoryListFragment.OnGeeftImageSelectedListener{

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
    //-------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geeft);
        Bundle bundle = new Bundle();

        inflater = LayoutInflater.from(ReceivedActivity.this);
        GeeftStoryListFragment geeftStoryListFragment = new GeeftStoryListFragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.add_geeft_fields_fragment, geeftStoryListFragment)
                .commit();
    }

    @Override
    public void onImageSelected(String id) {}
    
    @Override
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
        Picasso.with(ReceivedActivity.this).load(geeft.getUserProfilePic()).fit()
                .centerInside()
                .into(mReceivedDialogUserImage);

        //Parallax background -------------------------------------
        Picasso.with(ReceivedActivity.this).load(geeft.getGeeftImage()).fit()
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

    }
}
