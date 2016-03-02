package samurai.geeft.android.geeft.utilities;

import android.content.Context;
import android.content.Intent;

import samurai.geeft.android.geeft.activities.LoginActivity;

/**
 * Created by danybr-dev on 01/03/16.
 */
public class Utils {
    public void startLoginActivity(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }
}
