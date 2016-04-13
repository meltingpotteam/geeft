package samurai.geeft.android.geeft.interfaces;

import samurai.geeft.android.geeft.adapters.NavigationDrawerItemAdapter;

/**
 * Created by daniele on 13/04/16.
 */
public interface TaskCallbackBooleanIntHolder {
    void done(boolean result,NavigationDrawerItemAdapter.ViewHolder holder,
              int count,int resultToken);
}
