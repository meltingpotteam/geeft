package samurai.geeft.android.geeft;

import android.app.Application;
import android.support.v4.app.Fragment;

import com.baasbox.android.BaasBox;
import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ugookeadu on 13/01/16.
 * Class used for initializing all services
 */
public class ApplicationInit extends Application {
    Map<String, Fragment.SavedState> savedStateMap;
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

        savedStateMap = new HashMap<>();
    }
    public void setFragmentSavedState(String key, Fragment.SavedState state){
        savedStateMap.put(key, state);
    }

    public Fragment.SavedState getFragmentSavedState(String key){
        return savedStateMap.get(key);
    }
}
