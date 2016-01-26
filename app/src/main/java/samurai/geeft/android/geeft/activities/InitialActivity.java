package samurai.geeft.android.geeft.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.baasbox.android.BaasUser;

/**
 * Created by Ugo Nnanna Okeadu on 14/01/16.
 * Chooses first activity in base of if the user is signed in (MainActivity)
 * or not (LoginActivity)
 */
public class InitialActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BaasUser.current() != null) {
            startLoginActivity();
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
