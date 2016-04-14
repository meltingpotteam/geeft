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

import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

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
                        Arrays.asList("public_profile", "user_friends", "email"));

            }
        });

        /**
         *  Callback registration: succes s-> execute Task else show relative Toast
         */
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.i("LoginActivity", response.toString());
                                // Get facebook data from login
                                Bundle bFacebookData = getFacebookData(object);
                                final String loginToken = loginResult.getAccessToken().getToken();
                                new BaaSLoginTask(LoginActivity.this, "FACEBOOK",
                                        loginToken, bFacebookData, LoginActivity.this).execute();
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, first_name, last_name" +
                                ",email,gender, birthday, location"); // Parámetros que pedimos a facebook
                        request.setParameters(parameters);
                        request.executeAsync();
                        //AsyncTask for BaaS for storing user data
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

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle= new Bundle();;
        try {
            bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    /**
     * TO BE COMPLETED
     */
    private void setUpGoogleButton(){
        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        //requestScopes() and requestServerAuthCode() are added.
        String serverClientId = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestIdToken(getResources().getString(R.string.server_client_id))
                .requestServerAuthCode(getResources().getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]
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

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent,RC_SIGN_IN);
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
        if(result) {
//          //Call to Nickname and Email request
            Log.i("USERNAMEMAIL", "Inside LoginActivity call.");
            BaasUser user = BaasUser.current();
            Log.d(TAG,"user "+user);
            if (user!=null) {
                String mail = user.getScope(BaasUser.Scope.REGISTERED).getString("email");
                Log.d(TAG,"mail "+mail);
                String username = user.getScope(BaasUser.Scope.REGISTERED).getString("username");
                if ((mail==null)||(mail.isEmpty())||(username==null)||(username.isEmpty())){
                    Log.d(TAG,"user "+user);
                    startActivity(new Intent(this, UsernameMailActivity.class));
                } else {
                    startMainActivity();
                }
            }

        }
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from
        //   GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                final GoogleSignInAccount acct = result.getSignInAccount();
                Log.d(TAG, "TOKEN " + acct.getId());


                final BaasUser user = BaasUser
                        .withUserName(
                                UUID.nameUUIDFromBytes(acct.getId().getBytes()).toString())
                        .setPassword(
                                UUID.nameUUIDFromBytes(acct.getId().getBytes()).toString());
                JsonObject extras = user.getScope(BaasUser.Scope.PRIVATE)
                        .put("name", acct.getDisplayName());
                user.getScope(BaasUser.Scope.REGISTERED).put("profilePic",
                        acct.getPhotoUrl().toString());
                user.login(new BaasHandler<BaasUser>() {
                    @Override
                    public void handle(BaasResult<BaasUser> result) {
                        if (result.isSuccess()) {
                            Log.d("LOG", "Current user is: " + result.value());
                            try {
                                BaasUser baasUser = result.get();
                                Log.d(TAG, "" + baasUser.getScope(BaasUser.Scope.REGISTERED)
                                        .get("email"));
                                if (baasUser.getScope(BaasUser.Scope.REGISTERED)
                                        .get("email")==null){
                                    baasUser.getScope(BaasUser.Scope.REGISTERED)
                                            .put("email", acct.getEmail());
                                }
                                new BaaSLoginTask(LoginActivity.this, "GOOGLE",
                                        acct.getServerAuthCode(), baasUser,
                                        LoginActivity.this).execute();
                            } catch (BaasException e) {
                                e.printStackTrace();
                            }
                        } else {
                            user.signup(new BaasHandler<BaasUser>() {
                                @Override
                                public void handle(BaasResult<BaasUser> result) {
                                    if (result.isSuccess()) {
                                        Log.d("LOG", "Current user is: " + result.value());
                                        try {
                                            BaasUser baasUser = result.get();
                                            Log.d(TAG, "" + baasUser.getScope(BaasUser.Scope.REGISTERED)
                                                    .get("email"));
                                            if (baasUser.getScope(BaasUser.Scope.REGISTERED)
                                                    .get("email")==null){
                                                baasUser.getScope(BaasUser.Scope.REGISTERED)
                                                        .put("email",acct.getEmail());
                                            }
                                            new BaaSLoginTask(LoginActivity.this, "GOOGLE",
                                                    acct.getServerAuthCode(), result.get(),
                                                    LoginActivity.this).execute();
                                        } catch (BaasException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Log.e("LOG", "Show error", result.error());
                                    }
                                }
                            });
                            Log.e("LOG", "Show error", result.error());
                        }
                    }
                });
            } else{
                Log.e(TAG, "GOOGLE RESULT =" + result.getStatus().toString());
            }
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
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
    /*@Override
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
    }*/

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
                    acct.getServerAuthCode(),BaasUser.current(),
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

    //Starts the MainActivity
    private void startMainActivity() {
        final Intent mMainIntent = new Intent(LoginActivity.this,
                MainActivity.class);
        startActivity(mMainIntent);
        finish();
    }

}
