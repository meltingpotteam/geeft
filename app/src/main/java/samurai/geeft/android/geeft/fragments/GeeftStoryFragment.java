package samurai.geeft.android.geeft.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 02/02/16.
 */
public class GeeftStoryFragment extends Fragment{
    private Geeft mGeeft;
    private ImageView mGeeftImage;
    private CircleImageView mUserProfilePic;
    private TextView mUserLocationTextView;
    private TextView mUsernameTextView;
    private TextView mGeeftDescriptionTextView;
    private TextView mGeeftTitleTextView;


    public GeeftStoryFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_geeft_story, container, false);
        mGeeftImage = (ImageView)rootView.findViewById(R.id.geeft_story_view);
        mUserProfilePic = (CircleImageView) rootView.findViewById(R.id.geefter_profile_image);
        mUserLocationTextView = (TextView) rootView.findViewById(R.id.location);
        mUsernameTextView = (TextView) rootView.findViewById(R.id.geefter_name);
        mGeeftDescriptionTextView = (TextView) rootView.findViewById(R.id.geeft_description);
        mGeeftTitleTextView = (TextView) rootView.findViewById(R.id.geeft_name);

        Picasso.with(getContext()).load(mGeeft.getGeeftImage()).fit()
                .centerInside().placeholder(R.drawable.ic_image_multiple).into(mGeeftImage);
        Picasso.with(getContext()).load(mGeeft.getUserProfilePic()).fit()
                .centerInside().placeholder(R.drawable.ic_account_circle).into(mUserProfilePic);
        mUserLocationTextView.setText(mGeeft.getUserLocation());
        mUsernameTextView.setText(mGeeft.getUsername());
        mGeeftDescriptionTextView.setText(mGeeft.getGeeftDescription());
        mGeeftTitleTextView.setText(mGeeft.getGeeftTitle());

        return   rootView;
    }

    public void setGeeft(Geeft geeft){
        mGeeft = geeft;
    }

    public GeeftStoryFragment getInstance(){
        return this;
    }
}
