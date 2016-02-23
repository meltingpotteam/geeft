package samurai.geeft.android.geeft.fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by ugookeadu on 20/02/16.
 */
public class FullGeeftDeatailsFragment extends StatedFragment{
    private final String TAG = getClass().getSimpleName();

    public static final String GEEFT_KEY = "geeft_key";
    private Geeft mGeeft;
    private Toolbar mToolbar;
    private ImageView mGeeftImageView;
    private ImageView mGeefterProfilePicImageView;
    private TextView mGeefterNameTextView;
    private TextView mGeeftTitleTextView;
    private TextView mGeeftDescriptionTextView;

    public static FullGeeftDeatailsFragment newInstance(Bundle b) {
        FullGeeftDeatailsFragment fragment = new FullGeeftDeatailsFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState==null)
            mGeeft = (Geeft)getArguments().getSerializable(GEEFT_KEY);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "DETAIL FRAGMENT");
        View rootView = inflater.inflate(R.layout.fragment_geeft_deatails, container, false);

        initSupportActionBar(rootView);
        initUI(rootView);

        return rootView;
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        // Save items for later restoring them on rotation
        outState.putSerializable(GEEFT_KEY, mGeeft);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        if (savedInstanceState != null) {
            mGeeft = (Geeft)savedInstanceState.getSerializable(GEEFT_KEY);
            View rootView = getView();
            if (rootView!=null){
                initUI(rootView);
            }
        }
    }

    private void initUI(View rootView) {
        mGeeftImageView = (ImageView)rootView.findViewById(R.id.collapsing_toolbar_image);
        mGeefterProfilePicImageView = (ImageView)rootView.findViewById(R.id.geefter_profile_image);
        mGeefterNameTextView = (TextView)rootView.findViewById(R.id.geefter_name);
        mGeeftTitleTextView = (TextView)rootView.findViewById(R.id.geeft_title_textview);
        mGeeftDescriptionTextView = (TextView)rootView
                .findViewById(R.id.geeft_description_textview);

        if(mGeeft!=null) {
            Picasso.with(getContext()).load(mGeeft.getGeeftImage())
                    .fit().centerInside().into(mGeeftImageView);
            Picasso.with(getContext()).load(mGeeft.getUserProfilePic())
                    .fit().centerInside().into(mGeefterProfilePicImageView);
            mGeefterNameTextView.setText(mGeeft.getUsername());
            mGeeftTitleTextView.setText(mGeeft.getGeeftTitle());
            mGeeftDescriptionTextView.setText(mGeeft.getGeeftDescription());
        }
    }

    private void initSupportActionBar(View rootView) {
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity())
                .getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

}
