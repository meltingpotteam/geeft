package samurai.geeft.android.geeft;

import android.app.Application;
import android.support.v4.app.Fragment;

import com.baasbox.android.BaasBox;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.facebook.FacebookSdk;

import io.fabric.sdk.android.Fabric;
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
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        /**
         * BaasBox initialization
         */
        BaasBox.builder(this).setAuthentication(BaasBox.Config.AuthType.SESSION_TOKEN)
                .setApiDomain("geeft-test1.cloudapp.net")
                .setPort(9000)
                .setAppCode("1234567890")
                .init();

        /**
         * Facebook sdk initialization
         */
        FacebookSdk.sdkInitialize(getApplicationContext());

        savedStateMap = new HashMap<>();
    }
    public void setFragmentSavedState(String key, Fragment.SavedState state){
        savedStateMap.put(key, state);
    }

    public Fragment.SavedState getFragmentSavedState(String key){
        return savedStateMap.get(key);
    }
}
