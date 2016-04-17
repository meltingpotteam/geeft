package samurai.geeft.android.geeft.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanStringArrayToken;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by oldboy on 18/02/16.
 * Update by danybr-dev
 */
public class WinnerScreenActivity extends AppCompatActivity implements TaskCallbackBooleanStringArrayToken {

    private final String TAG = getClass().getSimpleName();

    private String url = "http://geeft.tk";
    //info dialog attributes---------------------
    private TextView mWinnerScreenGeefterName;
    private TextView mWinnerScreenGeeftedName;
    private ImageView mWinnerScreenGeeftBackground;
    private ImageButton mWinnerScreenFbButton;
    private ImageButton mWinnerScreenEmailButton;
    private ImageButton mWinnerScreenLocationButton;
    private TextView mWinnerMessage;
    //-------------------------------------------
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    //-------------------

    private Context mContext;
    private Toolbar mToolbar;
    private Intent mIntent;
    private int mAction;
    private String mGeeftId;
    private String mUserId;
    private String mLocation;
    private String mUserFbId;
    private ProgressDialog mProgressDialog;

    private final static String EXTRA_ACTION = "extra_action";
    private final static String EXTRA_GEEFT_ID  = "extra_geeft_id";
    private final static String EXTRA_ID = "extra_id";

    //info dialog attributes---------------------
    private LayoutInflater inflater;
    private Geeft mGeeft;
    //-------------------------------------------

    /**
     *
     * @param context
     * @param action : 1 is for Geefter mode,2 is for Geefted mode
     * @param geeftId : is GeeftId,it can be null in second case
     * @param docUserId : is docUserId,it can be null in first case
     * @return
     */
    public static Intent newIntent(Context context, int action,String geeftId, String docUserId) {
        Intent intent = new Intent(context, WinnerScreenActivity.class);
        intent.putExtra(EXTRA_ACTION, action);
        intent.putExtra(EXTRA_GEEFT_ID, geeftId);
        intent.putExtra(EXTRA_ID, docUserId);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.winner_screen);

        initUI();
        initActionBar();

        switch(mAction){
            case 1: geeftedCase();
                break;
            case 2: geefterCase();
                break;
            default: defaultCase();
                break;
        }

        //--------------------- Location Button implementation
         mWinnerScreenLocationButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (!isGoogleMapsInstalled()) {
                     Toast.makeText(mContext,
                             "Installa Google Maps per usare questa funzionalità", Toast.LENGTH_LONG).show();
                 } else if (!mLocation.equals("")) {
                     parseLocation(mLocation);
                 } else
                     Toast.makeText(mContext,
                             "Non ha fornito indirizzo", Toast.LENGTH_LONG).show();
             }
         });
        //-----------------------
        mWinnerScreenFbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFbIntent(mUserFbId);
            }
        });

        mWinnerScreenEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailToGeefter();
            }
        });

    }

    private void initUI(){
        mContext = getApplicationContext();
        mAction = getIntent().getIntExtra(EXTRA_ACTION, -1);
        mGeeftId = getIntent().getStringExtra(EXTRA_GEEFT_ID);
        mUserId = getIntent().getStringExtra(EXTRA_ID);

        mWinnerScreenGeefterName = (TextView) findViewById(R.id.winner_screen_geefter_name);
        mWinnerScreenGeeftedName = (TextView) findViewById(R.id.winner_screen_geefted_name);
        mWinnerScreenGeeftBackground = (ImageView) findViewById(R.id.winner_screen_geeft_background);
        mWinnerScreenFbButton = (ImageButton) findViewById(R.id.winner_screen_facebook_button);
        mWinnerScreenEmailButton = (ImageButton) findViewById(R.id.winner_screen_email_button);
        mWinnerScreenLocationButton = (ImageButton) findViewById(R.id.winner_screen_location_button);
        mWinnerMessage = (TextView) findViewById(R.id.winner_screen_message_text);
        mIntent = getIntent();
        showProgressDialog();

        mWinnerScreenGeeftBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Geeft> geeftList = new ArrayList<>();
                geeftList.add(mGeeft);
                startImageGallery(geeftList);
            }
        });
    }

    private void startImageGallery(List<Geeft> geeftList) {
        Intent intent =
                FullScreenImageActivity.newIntent(getApplicationContext(), geeftList, 0);
        startActivity(intent);
    }

    private void initActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(mAction == 1) {
            mToolbar.setTitle("Geefted"); //You are a Geefter if you donate
        }
        else if(mAction == 2) {
            mToolbar.setTitle("Geefter");
        }
    }

    private void sendEmailToGeefter(){
        BaasUser.fetch(mGeeft.getBaasboxUsername(), new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> geefterResult) {
                //if(progressDialog != null){
                //}
                if (geefterResult.isSuccess()) {
                    BaasUser geefter = geefterResult.value();
                    String geefterEmail = geefter.getScope(BaasUser.Scope.REGISTERED).getString("email");
                    if (geefterEmail == null) {
                        Toast.makeText(WinnerScreenActivity.this, "Spiacenti,l'e-mail fornito non è valido", Toast.LENGTH_LONG).show();
                    } else {
                        sendEmail(geefterEmail);
                    }
                } else {
                    showAlertDialog();
                }
            }
        });
    }

    private void sendEmail(String email){

        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        String message;
        if(mAction == 1){
            message = "Gentile " + mGeeft.getUsername() +
                    " \n\n" + "Mi è stato assegnato l'oggetto: '" + mGeeft.getGeeftTitle() + "' " +
                    "tramite l'applicazione "
                    + "android 'Geeft'" +"\n" + "CAMPO DA COMPILARE";
        }
        else{
            message = "Gentile utente\n\n" + "ti ho assegnato l'oggetto: '"
                    + mGeeft.getGeeftTitle() + "' tramite l'applicazione "
                    + "android 'Geeft'" +"\n" + "CAMPO DA COMPILARE";
        }
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "GEEFT: Richiesta di contatto per '"
                + mGeeft.getGeeftTitle() + "'");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        WinnerScreenActivity.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    private void geeftedCase() {
        String GeeftDocId = mGeeftId;
        BaasDocument.fetch("geeft", GeeftDocId, new BaasHandler<BaasDocument>() {
            @Override
            public void handle(BaasResult<BaasDocument> resGeeft) {
                if (resGeeft.isSuccess()) {

                    fillWinnerActivityGeeftedCase(resGeeft);

                } else {
                    if (resGeeft.error() instanceof BaasInvalidSessionException) {
                        Log.e(TAG, "Invalid Session Token");
                        startLoginActivity();
                    } else {
                        showAlertDialog();
                    }
                }
            }
        });
    }

    private void fillWinnerActivityGeeftedCase(BaasResult<BaasDocument> resGeeft) {
        BaasDocument docGeeft = resGeeft.value();
        mGeeft = new Geeft();
        mGeeft.setGeeftImage(docGeeft.getString("image") + BaasUser.current().getToken());
        mGeeft.setBaasboxUsername(docGeeft.getString("baasboxUsername"));
        mGeeft.setGeeftTitle(docGeeft.getString("title"));
        mGeeft.setUsername(docGeeft.getString("username"));

        mWinnerScreenGeefterName.setText(docGeeft.getString("name"));
        mWinnerScreenGeeftedName.setText(BaasUser.current().
                getScope(BaasUser.Scope.PRIVATE).get("name").toString() + ",");
        Picasso.with(mContext).load(docGeeft.getString("image")+ BaasUser.current().getToken())
                .fit().centerCrop().into(mWinnerScreenGeeftBackground);
        mLocation = docGeeft.getString("location").concat("," + docGeeft.getString("cap"));
        mUserFbId = docGeeft.getString("userFbId");
        String message;
        if(mUserFbId != null){
            message = "ti ha assegnato questo Geeft!\nContattalo tramite facebook o e-mail per" +
                    " concordare la posizione precisa per il ritiro e nel frattempo osserva la sua " +
                    "posizione approssimativa";
        }
        else{
            message = "ti ha assegnato questo Geeft!\nContattalo tramite e-mail per" +
                    " concordare la posizione precisa per il ritiro e nel frattempo osserva la sua " +
                    "posizione approssimativa";
            mWinnerScreenFbButton.setVisibility(View.GONE);
        }

        mWinnerMessage.setText(message);
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    private void showAlertDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        new AlertDialog.Builder(WinnerScreenActivity.this)
                .setTitle("Errore")
                .setMessage("Operazione non possibile. Riprovare più tardi.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mAction == 1) { // if I'm Geefted,on error return on AssignedActivity
                            startAssignedActivity();
                        }
                        else if(mAction == 2){ //else if I'm Geefter,on error return on DonatedActivity
                            startDonatedActivity();
                        }//else,others case
                    }
                }).show();
    }

    private void geefterCase(){
        String userDocId = mUserId;
        BaasUser.fetch(userDocId, new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> resUser) {
                if (resUser.isSuccess()) {
                    final BaasUser geefted = resUser.value();
                    BaasDocument.fetch("geeft", mGeeftId, new BaasHandler<BaasDocument>() {
                        @Override
                        public void handle(BaasResult<BaasDocument> resGeeft) {
                            if (resGeeft.isSuccess()) {
                                BaasDocument docGeeft = resGeeft.value();
                                fillWinnerActivityGeefterCase(geefted, docGeeft);
                            } else {
                                if (resGeeft.error() instanceof BaasInvalidSessionException) {
                                    Log.e(TAG, "Invalid Session Token");
                                    startLoginActivity();
                                } else {
                                    showAlertDialog();
                                }
                            }
                        }
                    });
                } else {
                    if (resUser.error() instanceof BaasInvalidSessionException) {
                        Log.e(TAG, "Invalid Session Token");
                        startLoginActivity();
                    } else {
                        showAlertDialog();
                    }
                }
            }
        });
    }

    private void fillWinnerActivityGeefterCase(BaasUser geefted,BaasDocument docGeeft){
        //In this case, mWinnerScreenGeeftedName is Geefter,and mWinnerScreenGeefterName is Geefted
        // this is only for bound of the layout,not a problem.
        mGeeft = new Geeft();
        mGeeft.setGeeftImage(docGeeft.getString("image") + BaasUser.current().getToken());
        mWinnerScreenGeeftedName.setText(BaasUser.current().
                getScope(BaasUser.Scope.PRIVATE).get("name").toString() + ",");
        //mWinnerScreenGeeftedName.setText("prova");
        Log.d(TAG, "informations: " + geefted.getName() + " ," + geefted.getStatus() + " ," + docGeeft.getId());
        mWinnerScreenGeefterName.setText(geefted.getScope(BaasUser.Scope.REGISTERED).get("username").toString());
        Picasso.with(mContext).load(docGeeft.getString("image")+BaasUser.current().getToken())
                .fit().centerCrop().into(mWinnerScreenGeeftBackground);
        mWinnerScreenLocationButton.setVisibility(View.GONE);
        JsonObject field = geefted.getScope(BaasUser.Scope.REGISTERED);
        String message;
        if(field.getObject("_social").getObject("facebook")!=null) {
            mUserFbId = field.getObject("_social").getObject("facebook").getString("id");
            message = "è stato selezionato per ricevere il Geeft. Prendi i contatti " +
                    "tramite social, oppure inviagli un'e-mail utilizzando i pulsanti qui sotto.";
        }else{
            mUserFbId = "";
            mWinnerScreenFbButton.setVisibility(View.GONE);
            message = "è stato selezionato per ricevere il Geeft. Prendi i contatti " +
                    "inviandogli un' e-mail.";
        }
        mWinnerMessage.setText(message);
        if (mProgressDialog != null)
            mProgressDialog.dismiss();

    }

    private void defaultCase(){
        startMainActivity();
    }


    private void startMainActivity() {
        Intent intent = MainActivity.newIntent(mContext);
        startActivity(intent);
        finish();
    }

    private void startAssignedActivity() {
        Intent intent = AssignedActivity.newIntent(mContext, "geeft", false);
        startActivity(intent);
        finish();
    }

    private void startDonatedActivity() {
        Intent intent = DonatedActivity.newIntent(mContext,"geeft",false);
        startActivity(intent);
        finish();
    }



    /**
     * @param userFbId
     */
    public void launchFbIntent(String userFbId){
        Intent facebookIntent = GeeftItemAdapter.getOpenFacebookProfileIntent(mContext, userFbId);
        startActivity(facebookIntent);

    }

    /**
     *
     * @param location
     */
    public void parseLocation(String location) {
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

    private void startLoginActivity(){
        Intent intent = new Intent(mContext,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void done(boolean result,String[] userInformations,int resultToken){
        /* mFullInformation[i] with i = {0,..,5}
            order = feedback,given,received,fbName,profilePicUri,fbId*/

//        fare query e settare il ritorno
        if(!result) {
            Toast toast;
            if (resultToken == RESULT_OK) {
                toast = Toast.makeText(getApplicationContext(), "Nessuna nuova storia", Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.BOTTOM, 0, 0);
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
    public boolean isGoogleMapsInstalled()
    {
        try
        {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

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

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(WinnerScreenActivity.this);
        try {
//                    mProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Attendere");
            mProgressDialog.show();
        } catch (WindowManager.BadTokenException e) {
            Log.e(TAG,"error: " + e.toString());
        }/*
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Attendere");*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }
}
