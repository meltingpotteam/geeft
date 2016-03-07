package samurai.geeft.android.geeft.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.SearchGeeftActivity;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by gabriel-dev on 07/03/16.
 */
public class SearchGeeftFragment extends StatedFragment {
    private static final String ARG_MODIFY = "arg_modify";
    private final String TAG = getClass().getName();

    private Toolbar mToolbar;


    //filed for automatic selection of the geeft, geeft's dimension and for allowing the the message exchanges

    public static SearchGeeftFragment newInstance(boolean modify) {
        SearchGeeftFragment fragment = new SearchGeeftFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_MODIFY, modify);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d("ADDGEEEFT", "onCreated");
//        mGeeft = (Geeft)getArguments().getSerializable(ARG_GEEFT);
//        mModify = getArguments().getBoolean(ARG_MODIFY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_geeft_activity_container, container, false);

//
//        if (mGeeft != null) {
//            initUI(rootView);
//            fillUI(rootView);
//        }else{
//            mGeeft= new Geeft();
//            initUI(rootView);
//        }

//        initActionBar(rootView);


        return rootView;
    }

    private void fillUI(View rootView) {

    }

//    private void initActionBar(View rootView) {
//        mToolbar = (Toolbar)rootView.findViewById(R.id.search_bar);
////        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
//        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity())
//                .getSupportActionBar();
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//    }

    private void initUI(View rootView) {

    }

    //



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.search_activity_menu, menu);

        Log.d("TOOLBAR", "" + inflater.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

//        switch(item.getItemId()){
//
//        }

        return super.onOptionsItemSelected(item);
    }

//    public interface OnCheckOkSelectedListener {
//        void onCheckSelected(boolean startChooseStory, Geeft mGeeft,boolean mModify);
//    }



    /**
     * Save Fragment's State here
     */
    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
//        outState.putSerializable(ARG_GEEFT, mGeeft);
//
//        if(mGeeftLocation.getSelectedItem()!=null) {
//            outState.putInt(KEY_LOCATION_SPINNER, mGeeftLocation.getSelectedItemPosition());
//        } else{
//            outState.putInt(KEY_LOCATION_SPINNER, 0);
//        }
//
//        if(mGeeftCategory.getSelectedItem()!= null){
//            outState.putInt(KEY_CATEGORY_SPINNER, mGeeftCategory.getSelectedItemPosition());
//        }else{
//            outState.putInt(KEY_CATEGORY_SPINNER,0);
//        }
//
//        if(mGeeftExpirationTime.getSelectedItem()!=null){
//            outState.putInt(KEY_EXPIRATION_TIME_SPINNER, mGeeftExpirationTime
//                    .getSelectedItemPosition());
//        }else{
//            outState.putInt(KEY_EXPIRATION_TIME_SPINNER, 0);
//        }
    }

    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
//        if (savedInstanceState != null) {
//            mGeeft = (Geeft)savedInstanceState.getSerializable(ARG_GEEFT);
//            mGeeftLocation.setSelection(savedInstanceState.getInt(KEY_LOCATION_SPINNER));
//            mGeeftCategory.setSelection(savedInstanceState.getInt(KEY_CATEGORY_SPINNER));
//            mGeeftExpirationTime.setSelection(savedInstanceState.getInt(KEY_EXPIRATION_TIME_SPINNER));
//
//            String path = getArguments().getString(KEY_GEEFT_IMAGE);
//            mGeeftImage = new File(path);
//            if (path!=null)
//                Picasso.with(getActivity())
//                        .load(mGeeftImage)
//                        .fit()
//                        .centerInside()
//                        .into(mGeeftImageView);
//        }
    }

}
