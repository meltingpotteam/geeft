package samurai.geeft.android.geeft.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

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
    private static boolean sLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        //setting colored translucent status bar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(
                    getApplicationContext(), R.color.colorPrimaryDark));
        }

        BaasUser.current().logout(new BaasHandler<Void>() {
            @Override
            public void handle(BaasResult<Void> baasResult) {
                if (baasResult.isSuccess())
                    setLogoutOk(true);
                else
                    setLogoutOk(false);
                finish();
            }
        });
        setLogoutOk(false);
    }

    public static boolean wasLogoutOk(Intent result) {
        return result.getBooleanExtra(EXTRA_LOGOUT_OK, false);
    }

    private void setLogoutOk(boolean isLogoutOk) {
        Intent data = new Intent();
        data.putExtra(EXTRA_LOGOUT_OK, isLogoutOk);
        setResult(RESULT_OK, data);
    }

    public static Intent newIntent(Context packageContext) {
        Intent i = new Intent(packageContext, LogoutActivity.class);
        return i;
    }
}
