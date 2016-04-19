package samurai.geeft.android.geeft.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import samurai.geeft.android.geeft.R;

/**
 * Created by ugookeadu on 14/01/16.
 * Chooses first activity in base of if the user is signed in (MainActivity)
 * or not (LoginActivity)
 */
public class InitialActivity extends Activity  {


    private final String TAG = getClass().getSimpleName();
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        /*BaasResult<BaasUser> temp = null;
        try{
            temp = BaasUser.current().followSync();
        }
        catch(BaasInvalidSessionException exception){

        }*/

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
            /* Create an Intent that will start the Menu-Activity. */
                if(BaasUser.current()!=null){
                    BaasUser.current().refresh(new BaasHandler<BaasUser>() {
                        @Override
                        public void handle(BaasResult<BaasUser> baasResult) {
                            if (baasResult.isSuccess()) {
                                try {
                                    Log.d(TAG,"USER FROM BAASBOX ="+baasResult.get().toString());
                                    Log.d(TAG, "CURRENT USER = "+BaasUser.current().toString());
                                } catch (BaasException e) {
                                    e.printStackTrace();
                                }
                                BaasUser user = BaasUser.current();
                                Log.d(TAG, "user " + user);
                                if (user != null) {
                                    String mail = user.getScope(BaasUser.Scope.REGISTERED).getString("email");
                                    Log.d(TAG, "mail " + mail);
                                    String username = user.getScope(BaasUser.Scope.REGISTERED).getString("username");
                                    if ((mail == null) || (mail.isEmpty()) || (username == null) || (username.isEmpty())) {
                                        Log.d(TAG, "user " + user);
                                        startActivity(new Intent(InitialActivity.this, UsernameMailActivity.class));
                                    } else {
                                        startMainActivity();
                                    }
                                }
                                finish();
                            } else if (baasResult.isFailed()) {
                                startLoginActivity();
                                finish();
                            }
                        }
                    });
                }else{
                    startLoginActivity();
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void startMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

//    public void done(boolean result,Geeft geeft){
//        if(result){ //You have a new assignation
//            //TODO: start Winner screen with this geeft
//            Intent intent = new Intent(this,WinnerScreenActivity.class);
//            intent.putExtra("geeft", (Serializable) geeft);
//            startMainActivity();
//            //startActivity(intent);
//        }
//        else
//            startMainActivity();
//    }
}
