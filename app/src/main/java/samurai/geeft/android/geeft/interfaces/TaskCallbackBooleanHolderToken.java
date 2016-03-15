package samurai.geeft.android.geeft.interfaces;

import samurai.geeft.android.geeft.adapters.GeeftItemAdapter;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 03/02/16.
 */
public interface TaskCallbackBooleanHolderToken {
    void done(boolean result, GeeftItemAdapter.GeeftViewHolder holder,Geeft item,int resultToken);
}
