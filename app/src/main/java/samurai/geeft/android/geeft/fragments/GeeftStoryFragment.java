package samurai.geeft.android.geeft.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 02/02/16.
 */
public class GeeftStoryFragment extends Fragment{
    private Geeft mGeeft;
    private ImageView mImageView;

    public GeeftStoryFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_geeft_story, container, false);
        mImageView = (ImageView)rootView.findViewById(R.id.geeft_story_view);
        Picasso.with(getContext()).load(mGeeft.getGeeftImage()).fit()
                .centerInside().placeholder(R.drawable.ic_image_multiple).into(mImageView);
        return   rootView;
    }

    public void setGeeft(Geeft geeft){
        mGeeft = geeft;
    }

    public GeeftStoryFragment getInstance(){
        return this;
    }
}
