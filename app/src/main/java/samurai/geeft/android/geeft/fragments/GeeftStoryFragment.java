package samurai.geeft.android.geeft.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
    private static final String GEEFT_KEY = "samurai.geeft.android.geeft.fragments."+
            "GeeftStoryFragment_geeft";
    private Geeft mGeeft;
    private ImageView mGeeftImage;
    private CircleImageView mUserProfilePic;
    private TextView mUserLocationTextView;
    private TextView mUsernameTextView;
    private TextView mGeeftDescriptionTextView;
    private TextView mGeeftTitleTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("STORY", "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_geeft_story, container, false);
        mGeeftImage = (ImageView)rootView.findViewById(R.id.geeft_story_view);
        mUserProfilePic = (CircleImageView) rootView.findViewById(R.id.geefter_profile_image);
        mUserLocationTextView = (TextView) rootView.findViewById(R.id.location);
        mUsernameTextView = (TextView) rootView.findViewById(R.id.geefter_name);
        mGeeftDescriptionTextView = (TextView) rootView.findViewById(R.id.geeft_description);
        mGeeftTitleTextView = (TextView) rootView.findViewById(R.id.geeft_name);
        mUserLocationTextView.setText(mGeeft.getUserLocation());
        mUsernameTextView.setText(mGeeft.getUsername());
        mGeeftDescriptionTextView.setText(mGeeft.getGeeftDescription());
        mGeeftTitleTextView.setText(mGeeft.getGeeftTitle());

        Picasso.with(getContext()).load(mGeeft.getGeeftImage()).fit()
                .centerInside().placeholder(R.drawable.ic_image_multiple).into(mGeeftImage);
        Picasso.with(getContext()).load(mGeeft.getUserProfilePic()).fit()
                .centerInside().placeholder(R.drawable.ic_account_circle).into(mUserProfilePic);

        return   rootView;
    }

    public void setGeeft(Geeft geeft){
        mGeeft = geeft;
    }

    public GeeftStoryFragment getInstance(){
        return this;
    }

    /**
     * Savind list state and items
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("STORY", "onSaved");
        // Save items for later restoring them on rotatio
        outState.putSerializable(GEEFT_KEY, mGeeft);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("STORY", "onActivit1");
        //retrievs mGeeftList position and state else gets items from database
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d("STORY", "onCreate");
        if (savedInstanceState != null) {
            Log.d("STORY", "onCreate1");
            mGeeft = (Geeft)savedInstanceState.getSerializable(GEEFT_KEY);
        }
    }
    /**
     * END Saving list state and items
     */
}
