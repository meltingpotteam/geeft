package samurai.geeft.android.geeft.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import java.util.Arrays;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSLoginTask;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;

/**
 * A login screen that offers login via facebook/twitter/google+.
 * Update by danybr-dev on 21/01/16
 */
public class LoginActivity extends AppCompatActivity implements TaskCallbackBoolean,GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_GET_AUTH_CODE = 9003;

    private GoogleApiClient mGoogleApiClient;
    // UI references.
    private Button mFbLoginButton;
    private Button mGoogleLoginButton;
    private CallbackManager callbackManager;
    private Animation mRotation;
    private Animation mRotation2;
    private long doneButtonClickTime;
    private Vibrator vibe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Default animation on social sign-in button press
        //mRotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_expand_left);
        //mRotation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_expand_right);

        //Vibration feedback
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //On click the rotation animation starts.
        // mGoogleLoginButton.startAnimation(mRotation);
        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        //requestScopes() and requestServerAuthCode() are added.
        String serverClientId = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .enableAutoManage(LoginActivity.this /* FragmentActivity */, LoginActivity.this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        //Method for sign-in buttons settings
        setUpViews();
    }

    /**
     * Method for sign-in buttons settings
     */
    private void setUpViews() {
        setUpFacebookButton();
        setUpGoogleButton();
    }


    private void setUpFacebookButton(){
        callbackManager = CallbackManager.Factory.create();
        mFbLoginButton = (Button) findViewById(R.id.fb_login_button);
        //mFbLoginButton.startAnimation(mRotation);
        mFbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Prevents users double click.
                 */
                if (SystemClock.elapsedRealtime() - doneButtonClickTime < 1000) {
                    return;
                }
                vibe.vibrate(50); // Vibrate for 50 ms
                //Store time of button click.
                doneButtonClickTime = SystemClock.elapsedRealtime();
                disableButtons();

                //On click the rotation animation starts.
                //mFbLoginButton.startAnimation(mRotation);

                //Permission requested to access at Facebook.
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "user_friends"));

            }
        });

        /**
         *  Callback registration: succes s-> execute Task else show relative Toast
         */

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //AsyncTask for BaaS for storing user data
                        new BaaSLoginTask(LoginActivity.this, "FACEBOOK",
                                loginResult.getAccessToken().getToken(),
                                LoginActivity.this).execute();
                    }

                    @Override
                    public void onCancel() {
                        enableButtons();
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.toast_fb_login_canc),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        enableButtons();
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.toast_fb_login_err),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * TO BE COMPLETED
     */
    private void setUpGoogleButton(){
        mGoogleLoginButton = (Button) findViewById(R.id.google_login_button);
        //mGoogleLoginButton.startAnimation(mRotation2);

        mGoogleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Prevents users double click.
                 */
                if (SystemClock.elapsedRealtime() - doneButtonClickTime < 1000) {
                    return;
                }
                vibe.vibrate(50); // Vibrate for 50 ms
                //Store time of button click.
                doneButtonClickTime = SystemClock.elapsedRealtime();
                //disableButtons();

                //signIn(); ENABLE THIS WHEN G+ LOGIN WITH TOKEN IS FIXED
                Toast.makeText(LoginActivity.this, "Non è stato possibile effettuare il login con G+, prova con Facebook o riprova più tardi", Toast.LENGTH_LONG).show();

            }
        });

    }

    /**
     * BaaS user social login result
     * @param result true->success start next activity, false
     */
    public void done(boolean result){
        //enables all social buttons
        enableButtons();
        if(result)
            startMainActivity();
    }

    //Starts the MainActivity
    private void startMainActivity(){
        final Intent mMainIntent = new Intent(LoginActivity.this,
                MainActivity.class);
        startActivity(mMainIntent);
        finish();
    }

    //Disable social sign in buttons
    private void disableButtons(){
        mFbLoginButton.setEnabled(false);
        mGoogleLoginButton.setEnabled(false);
    }

    //Enables social sign in buttons
    private void enableButtons(){
        mFbLoginButton.setEnabled(true);
        mGoogleLoginButton.setEnabled(true);
    }

    //Facebook SDK call result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override //Overrided method for implement GoogleApiClient.OnConnectionFailedListener interface
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    /**
     * Google+ implementation
     */
    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            //showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    //hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            //
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if(acct.getDisplayName()!=null) {
                Log.d(TAG, "Google+ User is: " + acct.getDisplayName() +
                        " with authCode: " + acct.getServerAuthCode());
            }
            new BaaSLoginTask(LoginActivity.this, "GOOGLE",
                    acct.getServerAuthCode(),
                    LoginActivity.this).execute();
        } else {
            // Signed out, show unauthenticated UI.
            Log.d(TAG, "Error when retrieved token");
            //Toast.makeText(this,"Non è stato possibile effettuare il login con G+,prova con Facebook o riprova più tardi",Toast.LENGTH_LONG).show();
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    //private void signOut() { //CONTINUO LA PECIONATA,tornerà private appena sitemato login G+
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        // [START_EXCLUDE]
                        //updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        // [START_EXCLUDE]
                        // updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]


}
