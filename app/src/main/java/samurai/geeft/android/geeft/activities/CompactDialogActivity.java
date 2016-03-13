package samurai.geeft.android.geeft.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestOptions;
import com.nvanbenschoten.motion.ParallaxImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSExchangeCompletedTask;
import samurai.geeft.android.geeft.fragments.GeeftReceivedListFragment;
import samurai.geeft.android.geeft.interfaces.TaskCallbackExchange;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by daniele on 05/03/16.
 */
public class CompactDialogActivity extends AppCompatActivity implements TaskCallbackExchange {

    private static final String KEY_TAKEN = "taken";
    private static final String KEY_GIVEN = "given";
    private final String TAG = getClass().getName();
    //info dialog attributes---------------------
    private TextView mReceivedDialogUsername;
    private TextView mReceivedDialogUserLocation;
    private ImageView mReceivedDialogUserImage;
    private ImageView mReceivedDialogFullImage;
    private ParallaxImageView mReceivedDialogBackground;
    private Button mTakenButton;
    private Button mGivenButton;
    private LayoutInflater inflater;
    private Toolbar mToolbar;
    private android.app.AlertDialog mDialog;
    //-------------------------------------------
    private final static String EXTRA_GEFFT = "geeft";
    private static final String EXTRA_CONTEXT = "extra_context";
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;

    //-------------------
    private Geeft mGeeft;
    private boolean mIamGeefter;
    private String mHisBaasboxName;

    public static Intent newIntent(@NonNull Context context, @NonNull Geeft geeft) {
        Intent intent = new Intent(context, CompactDialogActivity.class);
        intent.putExtra(EXTRA_GEFFT, geeft);
        intent.putExtra(EXTRA_CONTEXT, context.getClass().getSimpleName());
        return intent;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_for_fragment);
        inflater = LayoutInflater.from(CompactDialogActivity.this);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                mGeeft = new Geeft();
            } else {
                mGeeft = (Geeft)extras.getSerializable(EXTRA_GEFFT);
            }
        } else {
            mGeeft = (Geeft) savedInstanceState.getSerializable(EXTRA_GEFFT);
        }

        BaasUser currentUser = BaasUser.current();
        mIamGeefter = currentUser.getScope(BaasUser.Scope.PRIVATE).get("name").equals(mGeeft.getUsername());
        Log.d(TAG,"currentUser name: " + currentUser.getScope(BaasUser.Scope.PRIVATE).get("name") );
        Log.d(TAG, "Geefter name: " + mGeeft.getUsername());

        Log.d(TAG, "iAmGeefter flag is:" + mIamGeefter);
        if(mIamGeefter && !mGeeft.isGiven()){ // if I'm the Geefter and Geeft isn't given,show
                            // dialog to set given
            showDialogTakenGiven(mGeeft, true); // I'm the Geefter,so I send "true"

        }
        else if(!mIamGeefter && !mGeeft.isTaken()){ //if I'm the Geefted and Geeft isn't taken,show
                            //dialog to set taken
            showDialogTakenGiven(mGeeft,false);
        }
        else{
            checkConditionForFeedback(); //check for feedback
        }
    }

    public void showDialogTakenGiven(Geeft geeft,boolean geefter) { // give id of image
        initUI();

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
        Picasso.with(CompactDialogActivity.this)
                .load(geeft.getUserProfilePic())
                .fit()
                .centerInside()
                .into(mReceivedDialogUserImage);

        //Parallax background -------------------------------------
        Picasso.with(CompactDialogActivity.this)
                .load(geeft.getGeeftImage())
                .fit()
                .centerInside()
                .into(mReceivedDialogBackground);
        mReceivedDialogBackground.setTiltSensitivity(5);
        mReceivedDialogBackground.registerSensorManager();

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });

        Log.d(TAG,"geefter flag is:" +geefter);
        if(geefter){ //Geefter can see "consegnato" (given) button
            mTakenButton.setVisibility(View.GONE);
            mGivenButton.setVisibility(View.VISIBLE); // prova
        }
        else{ //Geefted can see "ritirato" (taken) button
            mGivenButton.setVisibility(View.GONE);
            mTakenButton.setVisibility(View.VISIBLE); // prova
        }

        //------------- Taken Button
        mTakenButton.setOnClickListener(new View.OnClickListener() { //ritirato
            @Override
            public void onClick(View v) {
                setTakenToGeeft();
                checkConditionForFeedback();
            }
        });
        //------------------------

        //--------------- Given Button
        mGivenButton.setOnClickListener(new View.OnClickListener() { //Consegnato
            @Override
            public void onClick(View v) {
                setGivenToGeeft();
                checkConditionForFeedback();
            }
        });
        //------------------------

        //Listener for the imageView: -----------------------------------
        mReceivedDialogBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showPicture();
            }
        });

        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().getAttributes().windowAnimations = R.style.profile_info_dialog_animation;
        //                dialog.setMessage("Some information that we can take from the facebook shared one");
        //Log.d(TAG,"Show!");
        mDialog.show();  //<-- See This!

    }
    private void initUI(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(CompactDialogActivity.this); //Read Update
        View dialogLayout = inflater.inflate(R.layout.received_geeft_dialog, null);
        alertDialog.setView(dialogLayout);
        //On click, the user visualize can visualize some infos about the geefter
        mDialog = alertDialog.create();

        //profile dialog fields-----------------------
        mReceivedDialogUsername = (TextView) dialogLayout.findViewById(R.id.dialog_geefter_name);
        mReceivedDialogUserLocation = (TextView) dialogLayout.findViewById(R.id.dialog_geefter_location);
        mReceivedDialogUserImage = (ImageView) dialogLayout.findViewById(R.id.dialog_geefter_profile_image);
        //Lasciamo gli stessi?!

        mReceivedDialogBackground = (ParallaxImageView) dialogLayout.findViewById(R.id.dialog_geefter_background);
        mTakenButton = (Button) dialogLayout.findViewById(R.id.received_dialog_takenButton);
        mGivenButton = (Button) dialogLayout.findViewById(R.id.received_dialog_givenButton);
    }

    private void showPicture(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CompactDialogActivity.this); //Read Update
        LayoutInflater inflater = CompactDialogActivity.this.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.geeft_image_dialog, null);
        alertDialog.setView(dialogLayout);

        AlertDialog dialog = alertDialog.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //TODO: Check this

        mReceivedDialogFullImage = (ImageView) dialogLayout.findViewById(R.id.dialogGeeftImage);
        mReceivedDialogFullImage.setImageDrawable(mReceivedDialogBackground.getDrawable());



        dialog.getWindow().getAttributes().windowAnimations = R.style.scale_up_animation;
        //dialog.setMessage("Some information that we can take from the facebook shared one");
        dialog.show();  //<-- See This!
        //Toast.makeText(getApplicationContext(), "TEST IMAGE", Toast.LENGTH_LONG).show();
    }

    private void setTakenToGeeft() {
        BaasDocument.fetch("geeft", mGeeft.getId(), new BaasHandler<BaasDocument>() {
            @Override
            public void handle(BaasResult<BaasDocument> resGeeft) {
                if (resGeeft.isSuccess()) {
                    BaasDocument geeft = resGeeft.value();
                    geeft.put(KEY_TAKEN, true); //Flag taken is true
                    setTakenInBaasbox(geeft);
                } else {
                    if (resGeeft.error() instanceof BaasInvalidSessionException) {
                        Log.e(TAG, "Invalid Session Token");
                        startLoginActivity();
                    } else {
                        Log.e(TAG, "Error while fetching geeft doc");
                        new AlertDialog.Builder(CompactDialogActivity.this)
                                .setTitle("Errore")
                                .setMessage("Operazione non possibile. Riprovare più tardi.").show();
                    }
                }
            }
        });
    }

    private void setTakenInBaasbox(BaasDocument geeft) {
        geeft.save(new BaasHandler<BaasDocument>() {
            @Override
            public void handle(BaasResult<BaasDocument> resSaveGeeft) {
                if (resSaveGeeft.isSuccess()) {
                    mGeeft.setTaken(true);
                    new AlertDialog.Builder(CompactDialogActivity.this)
                            .setTitle("Successo")
                            .setMessage("Appena il Geefter confermerà la consegna,verranno " +
                                    "abilitati i feedback. Grazie.").show();
                    //send push notification to Geefter
                } else {
                    if (resSaveGeeft.error() instanceof BaasInvalidSessionException) {
                        Log.e(TAG, "Invalid Session Token");
                        startLoginActivity();
                    } else {
                        Log.e(TAG, "Error while saving geeft doc:" + resSaveGeeft.error());
                        new AlertDialog.Builder(CompactDialogActivity.this)
                                .setTitle("Errore")
                                .setMessage("Operazione non possibile. Riprovare più tardi.").show();
                    }
                }
            }
        });
    }

    private void setGivenToGeeft() {
        BaasDocument.fetch("geeft", mGeeft.getId(), new BaasHandler<BaasDocument>() {
            @Override
            public void handle(BaasResult<BaasDocument> resGeeft) {
                if (resGeeft.isSuccess()) {
                    BaasDocument geeft = resGeeft.value();
                    geeft.put(KEY_GIVEN, true); //Flag taken is true
                    setGivenInBaasbox(geeft);
                } else {
                    if (resGeeft.error() instanceof BaasInvalidSessionException) {
                        Log.e(TAG, "Invalid Session Token");
                        startLoginActivity();
                    } else {
                        Log.e(TAG, "Error while fetching geeft doc");
                        new AlertDialog.Builder(CompactDialogActivity.this)
                                .setTitle("Errore")
                                .setMessage("Operazione non possibile. Riprovare più tardi.").show();
                    }
                }
            }
        });
    }

    private void setGivenInBaasbox(BaasDocument geeft){
        geeft.save(new BaasHandler<BaasDocument>() {
            @Override
            public void handle(BaasResult<BaasDocument> resSaveGeeft) {
                if (resSaveGeeft.isSuccess()) {
                    mGeeft.setGiven(true);
                    new AlertDialog.Builder(CompactDialogActivity.this)
                            .setTitle("Successo")
                            .setMessage("Appena il Geefted confermerà il ritiro,verranno " +
                                    "abilitati i feedback. Grazie.").show();
                    //send push notification to Geefter
                } else {
                    if (resSaveGeeft.error() instanceof BaasInvalidSessionException) {
                        Log.e(TAG, "Invalid Session Token");
                        startLoginActivity();
                    } else {
                        Log.e(TAG, "Error while saving geeft doc:" + resSaveGeeft.error());
                        new AlertDialog.Builder(CompactDialogActivity.this)
                                .setTitle("Errore")
                                .setMessage("Operazione non possibile. Riprovare più tardi.").show();
                    }
                }
            }
        });
    }

    private void checkConditionForFeedback(){
        if(mIamGeefter && mGeeft.isFeedbackLeftByGeefter()){
            Log.d(TAG, "Show dialog left feedback for geefter");
            showDialogLeftFeedback();
        }
        else if (!mIamGeefter && mGeeft.isFeedbackLeftByGeefted()) {
            showDialogLeftFeedback();
        }
        else { // if feedback isn't left

            if (mGeeft.isTaken() && mGeeft.isGiven()) {
                new BaaSExchangeCompletedTask(getApplicationContext(),mGeeft,mIamGeefter,this).execute();
                             // create link in "ricevuti",delete link in "assegnati" and update
                            //n_given and n_received

            } else if (mGeeft.isTaken() && !mGeeft.isGiven()) {
                //Send push notification to Geefter. One per day!
            } else if (mGeeft.isGiven() && !mGeeft.isTaken()) {
                //Send push notification to Geefted. One per day!
            }
        }

    }

    public void exchangeCompleted(boolean result,int resultToken){ //CALLBACK METHOD
        if(result){
            new AlertDialog.Builder(CompactDialogActivity.this)
                    .setTitle("Evviva!")
                    .setMessage("Avete confermato lo scambio a mano del Geeft. Lasciatevi un feedback.")
                    .setPositiveButton("Procedi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            getHisBaasboxNameAndStartFeedbackActivity(mIamGeefter); //getHisBaasboxName from his doc_id,side effect on mHisBaasboxName

                            finish();
                        }
                    })
                    .show();
        }
        else{
            Toast toast;
            if (resultToken == RESULT_SESSION_EXPIRED) {
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

    private void startLoginActivity(){
        Intent intent = new Intent(CompactDialogActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void startFeedbackActivity(){

        Intent intent = FeedbackPageActivity.newIntent(getApplicationContext(), mGeeft, mHisBaasboxName, mIamGeefter);
        startActivity(intent);
    }

    private void getHisBaasboxNameAndStartFeedbackActivity(boolean iamGeefter) {
        BaasQuery.Criteria query = BaasQuery.builder().where("out.id = '"+ mGeeft.getId() + "'").criteria();
        if(iamGeefter) { // I'm Geefter,so I need BaasboxUsername of Geefted
            BaasLink.fetchAll(TagsValue.LINK_NAME_ASSIGNED, query, RequestOptions.DEFAULT, new BaasHandler<List<BaasLink>>() {
                @Override
                public void handle(BaasResult<List<BaasLink>> resLink) {
                    if (resLink.isSuccess()) {
                        List<BaasLink> links = resLink.value();
                        Log.d(TAG,"Size should be one: " + links.size());
                        Log.d(TAG,"Link id is: " + links.get(0).getId());
                        Log.d(TAG,"link: " + links.get(0).in().toJson());
                        Log.d(TAG,"link: " + links.get(0).out().toJson());
                        try {
                            Log.d(TAG, "link out: " + links.get(0).in().toString());
                            Log.d(TAG, "link out: " + links.get(0).out().toString());
                        }
                        catch(Exception e){
                            Log.e(TAG,e.toString());
                        }
                        mHisBaasboxName = links.get(0).out().getAuthor();//Get doc_id of user from get(0)
                        //so, get baasboxName from getAuthor
                        startFeedbackActivity(); // Feedback enabled;
                    } else {
                        Log.d(TAG,"Error while fetching link");
                        showDialogError();
                    }
                }
            });
        }
        else{// I'm Geefted,so I need BaasboxUsername of Geefter
            BaasLink.fetchAll(TagsValue.LINK_NAME_DONATED, query, RequestOptions.DEFAULT, new BaasHandler<List<BaasLink>>() {
                @Override
                public void handle(BaasResult<List<BaasLink>> resLink) {
                    if (resLink.isSuccess()) {
                        List<BaasLink> links = resLink.value();
                        Log.d(TAG,"Size should be one: " + links.size());
                        Log.d(TAG,"link: " + links.get(0).toString());
                        Log.d(TAG,"link out: " + links.get(0).in().toString());
                        Log.d(TAG,"link out: " + links.get(0).out().toString());
                        mHisBaasboxName = links.get(0).out().getAuthor(); //Get doc_id of user from get(0)
                                    //so, get baasboxName from getAuthor
                        startFeedbackActivity(); // Feedback enabled;

                    } else {
                        Log.d(TAG,"Error while fetching link");
                        showDialogError();
                    }
                }
            });
        }

    }

    private void showDialogError(){
        new AlertDialog.Builder(CompactDialogActivity.this)
                .setTitle("Errore")
                .setMessage("Operazione non possibile. Riprovare più tardi.").show();
    }

    private void showDialogLeftFeedback(){
        new AlertDialog.Builder(CompactDialogActivity.this)
                .setTitle("Errore")
                .setMessage("Hai già lasciato il tuo feedback per questo Geeft. Grazie.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startMainActivity();
                    }
                })
                .show();
    }

    private void startMainActivity(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
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
}
