package samurai.geeft.android.geeft.interfaces;

import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;

/**
 * Created by danybr-dev on 08/02/16.
 */
public interface TaskCallbackBooleanArray {
        void done(boolean result,GeeftItemAdapter.ViewHolder holder, double[] userInformation);
}
