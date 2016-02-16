package samurai.geeft.android.geeft;

import android.app.Application;

import com.baasbox.android.BaasBox;
import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by ugookeadu on 13/01/16.
 * Class used for initializing all services
 */
public class ApplicationInit extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * BaasBox initialization
         */
        BaasBox.builder(this).setAuthentication(BaasBox.Config.AuthType.SESSION_TOKEN)
                .setApiDomain("geeft1.cloudapp.net")
                .setPort(9000)
                .setAppCode("1234567890")
                .init();

        /**
         * Facebook sdk initialization
         */
        FacebookSdk.sdkInitialize(getApplicationContext());

        /**
         * Fresco sdk initialization
         */
        Fresco.initialize(getApplicationContext());
    }
}
