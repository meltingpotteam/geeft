package samurai.geeft.android.geeft.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import samurai.geeft.android.geeft.R;

/**
 * Created by ugookeadu on 31/01/16.
 */
public class UserProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        return rootView;
    }
}
