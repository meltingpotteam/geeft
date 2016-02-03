package samurai.geeft.android.geeft.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.facebook.drawee.view.SimpleDraweeView;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.ImageControllerGenerator;

/**
 * Created by ugookeadu on 02/02/16.
 */
public class GeeftStoryFragment extends Fragment{
    private Geeft mGeeft;
    private SimpleDraweeView mDraweeView;

    public GeeftStoryFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_geeft_story, container, false);
        mDraweeView = (SimpleDraweeView)rootView.findViewById(R.id.geeft_story_view);
        ImageControllerGenerator.generateSimpleDrawee(getContext(),mDraweeView,
                mGeeft.getGeeftImage());
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        return   rootView;

    }

    public void setGeeft(Geeft geeft){
        mGeeft = geeft;
    }

    public GeeftStoryFragment getInstance(){
        return this;
    }
}
