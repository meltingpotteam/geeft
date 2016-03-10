package samurai.geeft.android.geeft.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import java.io.Serializable;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanGeeft;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 14/01/16.
 * Chooses first activity in base of if the user is signed in (MainActivity)
 * or not (LoginActivity)
 */
public class InitialActivity extends Activity  {


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
                            if (baasResult.isSuccess()){
                                startMainActivity();
                            }else if(baasResult.isFailed()){
                                startLoginActivity();
                            }
                        }
                    });
                }else{
                    startLoginActivity();
                }
//                    finish();
//                InitialActivity.this.finish();
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
