package samurai.geeft.android.geeft.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by ugookeadu on 20/02/16.
 */
public class FullGeeftDeatailsFragment extends StatedFragment{
    private final String TAG = getClass().getSimpleName();

    public static final String GEEFT_KEY = "geeft_key";

    public static FullGeeftDeatailsFragment newInstance(Bundle b) {
        FullGeeftDeatailsFragment fragment = new FullGeeftDeatailsFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"DETAIL FRAGMENT");
        View rootView = inflater.inflate(R.layout.fragment_geeft_deatails, container, false);
        return rootView;
    }

}
