package samurai.geeft.android.geeft.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.FullScreenImageActivity;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by ugookeadu on 02/02/16.
 */
public class GeeftStoryFragment extends StatedFragment {
    private Geeft mGeeft;
    private ImageView mGeeftImage;
    private CircleImageView mUserProfilePic;
    private TextView mUserLocationTextView;
    private TextView mUsernameTextView;
    private TextView mGeeftDescriptionTextView;
    private TextView mGeeftTitleTextView;
    private Boolean mTextIsSingleLine;

    private static final String GEEFT_KEY = "samurai.geeft.android.geeft.fragments."+
            "GeeftStoryFragment_geeft";
    private Toolbar mToolbar;
    private int mPosition;
    private List<Geeft> mList;

    public static GeeftStoryFragment newInstance(Bundle b) {
        GeeftStoryFragment fragment = new GeeftStoryFragment();
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_geeft_story_scrollableview, container, false);

        initUI(rootView);
        //if (savedInstanceState==null)
            //initSupportActionBar(rootView);

        return rootView;
    }

    private void initSupportActionBar(View rootView) {
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity())
                .getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Save Fragment's State here
     */
    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        // Save items for later restoring them on rotation
        outState.putSerializable(GEEFT_KEY, mGeeft);
    }

    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        if (savedInstanceState != null) {
            mGeeft = (Geeft)savedInstanceState.getSerializable(GEEFT_KEY);
            View rootView = getView();
            if (rootView!=null){
                initUI(rootView);
                initSupportActionBar(rootView);
            }
        }
    }

    private void initUI(View rootView) {
        mGeeftImage = (ImageView)rootView.findViewById(R.id.geeft_story_view);
        mUserProfilePic = (CircleImageView) rootView.findViewById(R.id.geefter_profile_image);
        mUserLocationTextView = (TextView) rootView.findViewById(R.id.location);
        mUsernameTextView = (TextView) rootView.findViewById(R.id.geefter_name);
        mGeeftDescriptionTextView = (TextView) rootView.findViewById(R.id.geeft_description);
        mGeeftTitleTextView = (TextView) rootView.findViewById(R.id.geeft_name);

        Log.d("mGeeft before create", "mGeeft val: " + mGeeft);
        if (mGeeft != null){

            mGeeftDescriptionTextView.setText(mGeeft.getGeeftDescription());
            mGeeftDescriptionTextView.setSingleLine(true);
            mGeeftDescriptionTextView.setEllipsize(TextUtils.TruncateAt.END);
            mTextIsSingleLine = true;

            Picasso.with(getContext()).load(mGeeft.getGeeftImage()).fit()
                    .centerInside().placeholder(R.drawable.ic_image_multiple).into(mGeeftImage);
            Picasso.with(getContext()).load(Uri.parse(mGeeft.getUserProfilePic())).fit()
                    .centerInside().placeholder(R.drawable.ic_account_circle).into(mUserProfilePic);
            mUserLocationTextView.setText(mGeeft.getUserLocation());
            mUsernameTextView.setText(mGeeft.getUsername());
            mGeeftDescriptionTextView.setText(mGeeft.getGeeftDescription());
            mGeeftTitleTextView.setText(mGeeft.getGeeftTitle());

            //Text Expander///////////////
            mGeeftDescriptionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mTextIsSingleLine) {
                        mGeeftDescriptionTextView.setSingleLine(false);
                        mTextIsSingleLine = false;
                    } else {
                        mGeeftDescriptionTextView.setSingleLine(true);
                        mTextIsSingleLine = true;
                    }

                }
            });

            mGeeftImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startImageGallery(mList);
                }
            });
            //////////////////////////////
        }
    }

    private void startImageGallery(List<Geeft> geeftList) {
        Intent intent =
                FullScreenImageActivity.newIntent(getContext(), geeftList,mPosition);
        startActivity(intent);
    }

    public void setGeeft(Geeft geeft){
        mGeeft = geeft;
    }

    public GeeftStoryFragment getInstance(){
        return this;
    }


    public void setPosition(int position) {
        mPosition = position;
    }

    public void setList(List<Geeft> list) {
        mList = list;
    }
}
