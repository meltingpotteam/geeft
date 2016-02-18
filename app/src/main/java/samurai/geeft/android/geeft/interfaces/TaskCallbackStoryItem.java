package samurai.geeft.android.geeft.interfaces;

import samurai.geeft.android.geeft.adapters.StoryItemAdapter;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 17/02/16.
 */
public interface TaskCallbackStoryItem {
    void done(boolean result,StoryItemAdapter.ViewHolder holder, double[] userInformation);
    void done(boolean result, StoryItemAdapter.ViewHolder holder,Geeft item);
}
