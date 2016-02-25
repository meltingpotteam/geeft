package samurai.geeft.android.geeft.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URLEncoder;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanStringArrayToken;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by oldboy on 18/02/16.
 */
public class WinnerScreenActivity extends AppCompatActivity implements TaskCallbackBooleanStringArrayToken {

    private static final String TAG = "WinnerScreenActivity";
    private String url = "http://geeft.tk";
    //info dialog attributes---------------------
    private TextView mWinnerScreenGeefterName;
    private TextView mWinnerScreenGeeftedName;
    private ImageView mWinnerScreenGeeftBackground;
    private ImageButton mWinnerScreenFbButton;
    private ImageButton mWinnerScreenLocationButton;
    //-------------------------------------------
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    //-------------------

    private Context mContext;
    private Toolbar mToolbar;
    private Intent mIntent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winner_screen);

        mToolbar = (Toolbar) findViewById(R.id.winner_screen_toolbar);
        mToolbar.setTitle("Winner"); //You are a Geefter if you donate
        setSupportActionBar(mToolbar);

        mContext = getApplicationContext();
        mWinnerScreenGeefterName = (TextView) findViewById(R.id.winner_screen_geefter_name);
        mWinnerScreenGeeftedName = (TextView) findViewById(R.id.winner_screen_geefted_name);
        mWinnerScreenGeeftBackground = (ImageView) findViewById(R.id.winner_screen_geeft_background);
        mWinnerScreenFbButton = (ImageButton) findViewById(R.id.winner_screen_facebook_button);
        mWinnerScreenLocationButton = (ImageButton) findViewById(R.id.winner_screen_location_button);
        mIntent = getIntent();
        final Geeft geeft = (Geeft) mIntent.getSerializableExtra("geeft");
        Log.d(TAG,"Geeft image is:" + geeft.getGeeftImage() );

        /*mWinnerScreenFbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WinnerScreenActivity.this, "Start the communication with the Geefter", Toast.LENGTH_SHORT).show();
            }
        });
        mWinnerScreenFbShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WinnerScreenActivity.this, "Show approximate position of the Geef", Toast.LENGTH_SHORT).show();
            }
        });*/
        mWinnerScreenGeefterName.setText(geeft.getUsername());
        mWinnerScreenGeeftedName.setText("Gabriele Vecchia");
        Picasso.with(getApplicationContext()).load(geeft.getGeeftImage()).fit().centerCrop()
                .placeholder(R.drawable.ic_image_multiple)
                .into(mWinnerScreenGeeftBackground);

        //--------------------- Location Button implementation
        final String location = geeft.getUserLocation();
        mWinnerScreenLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!location.equals("")) {
                    call2(location);
                } else
                    Toast.makeText(mContext,
                            "Non ha fornito indirizzo", Toast.LENGTH_LONG).show();

            }
        });

        mWinnerScreenFbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                call1(geeft);
            }
        });



    }

    /**
     * DA MODIFICARE E CANCELLARE E TUTTO QUANTO E BASTA E STO SBROCCANDO E QUESTO E' SOLO UN ACCROCCO
     * @param geeft
     */
    public void call1(Geeft geeft){

        Intent facebookIntent = GeeftItemAdapter.getOpenFacebookProfileIntent(mContext, geeft.getUserFbId());
        startActivity(facebookIntent);

    }

    public void call2(String location) {
        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                    URLEncoder.encode(location, "UTF-8"));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mapIntent.setPackage("com.google.android.apps.maps");
            getApplicationContext().startActivity(mapIntent);
        } catch (java.io.UnsupportedEncodingException e) {
            Toast.makeText(mContext, "Non ha fornito indirizzo", Toast.LENGTH_LONG).show();
        }
    }

    public void done(boolean result,String[] userInformations,int resultToken){
        /* mFullInformation[i] with i = {0,..,5}
            order = feedback,given,received,fbName,profilePicUri,fbId*/

//        fare query e settare il ritorno
        if(!result) {
            Toast toast;
            if (resultToken == RESULT_OK) {
                toast = Toast.makeText(getApplicationContext(), "Nessuna nuova storia", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 0, 0);
                toast.show();
            } else if (resultToken == RESULT_SESSION_EXPIRED) {
                toast = Toast.makeText(getApplicationContext(), "Sessione scaduta,è necessario effettuare di nuovo" +
                        " il login", Toast.LENGTH_LONG);
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                toast.show();
            } else {
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Errore")
                        .setMessage("Operazione non possibile. Riprovare più tardi.").show();
            }
        }

    }

}
