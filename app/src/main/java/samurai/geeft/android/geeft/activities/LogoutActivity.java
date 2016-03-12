package samurai.geeft.android.geeft.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import samurai.geeft.android.geeft.R;

/**
 * Created by ugookeadu on 17/01/16.
 */
public class LogoutActivity extends Activity {
    private static final String EXTRA_LOGOUT_OK =
            "com.bignerdranch.android.geoquiz.logout_ok";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        BaasUser.current().logout(new BaasHandler<Void>() {
            @Override
            public void handle(BaasResult<Void> baasResult) {
                if (baasResult.isSuccess())
                    setLogoutOk(true);
                else if( baasResult.isFailed())
                    setLogoutOk(false);
                else if( baasResult.isCanceled()) {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_CANCELED, intent);
                }
                finish();
            }
        });
        setLogoutOk(false);
    }

    public static boolean wasLogoutOk(Intent result) {
        return result.getBooleanExtra(EXTRA_LOGOUT_OK, false);
    }

    private void setLogoutOk(boolean isLogoutOk) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_LOGOUT_OK, isLogoutOk);
        setResult(Activity.RESULT_OK, intent);

    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, LogoutActivity.class);
    }
}
