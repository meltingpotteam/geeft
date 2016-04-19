package samurai.geeft.android.geeft.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;
import samurai.geeft.android.geeft.utilities.TouchImageView;

/**
 * Created by ugookeadu on 24/02/16.
 */
public class FullScreenImageFragment extends StatedFragment {
    private final String TAG = getClass().getSimpleName();

    private Geeft mGeeft;
    private TouchImageView mGeeftImage;
    private ImageView mProgressImageView;

    private static final String GEEFT_KEY = "samurai.geeft.android.geeft.fragments."+
            "GeeftStoryFragment_geeft";
    public static final String ARG_GEFFT = "arg_geeft";
    private Toolbar mToolbar;

    public static FullScreenImageFragment newInstance(Bundle b) {
        FullScreenImageFragment fragment = new FullScreenImageFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_full_screen_image, container, false);
        initUI(rootView);
        //initSupportActionBar(rootView);
        return rootView;
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
                //initSupportActionBar(rootView);
            }
        }
    }

    private void initSupportActionBar(View rootView) {
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(Color.TRANSPARENT);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity())
                .getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    private void initUI(View rootView) {
        mGeeftImage = (TouchImageView)rootView.findViewById(R.id.touch_image_view);
        mProgressImageView = (ImageView)rootView.findViewById(R.id.anim_progress);

        mGeeft = (Geeft)getArguments().getSerializable(ARG_GEFFT);
        Log.d("mGeeft before create", "mGeeft val: " + mGeeft);
        if (mGeeft != null){
            Picasso.with(getContext()).load(mGeeft.getGeeftImage()).fit()
                    .centerInside().into(mGeeftImage,new Callback() {
                @Override
                public void onSuccess() {
                    mProgressImageView.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    Log.e(TAG, "error");
                    mProgressImageView.setVisibility(View.GONE);
                }
            });
        }

    }


    public FullScreenImageFragment getInstance(){
        return this;
    }
}
