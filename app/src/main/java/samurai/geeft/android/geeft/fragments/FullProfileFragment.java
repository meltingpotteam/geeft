package samurai.geeft.android.geeft.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import samurai.geeft.android.geeft.R;

/**
 * Created by joseph on 16/02/16.
 */
public class FullProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.full_profile, container, false);
        return rootView;
    }
}
