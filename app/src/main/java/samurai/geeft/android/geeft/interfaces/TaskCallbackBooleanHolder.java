package samurai.geeft.android.geeft.interfaces;

import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 03/02/16.
 */
public interface TaskCallbackBooleanHolder {
    void done(boolean result, GeeftItemAdapter.ViewHolder holder,Geeft item);
}
