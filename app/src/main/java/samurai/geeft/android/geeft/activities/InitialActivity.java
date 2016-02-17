package samurai.geeft.android.geeft.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.baasbox.android.BaasInvalidSessionException;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

/**
 * Created by ugookeadu on 14/01/16.
 * Chooses first activity in base of if the user is signed in (MainActivity)
 * or not (LoginActivity)
 */
public class InitialActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*BaasResult<BaasUser> temp = null;
        try{
            temp = BaasUser.current().followSync();
        }
        catch(BaasInvalidSessionException exception){

        }*/
        if (BaasUser.current() != null) {
            startMainActivity();
        } else {
            startLoginActivity();
        }
        finish();
    }

    private void startMainActivity() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
