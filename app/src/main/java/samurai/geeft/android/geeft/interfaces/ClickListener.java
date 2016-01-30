package samurai.geeft.android.geeft.interfaces;

import android.view.View;

/**
 * Created by ugookeadu on 26/01/16.
 * interface for click events
 */
public interface ClickListener{
    void onClick(View view, int position);
    void onLongClick(View view, int position);

}