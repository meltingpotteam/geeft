package samurai.geeft.android.geeft.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import samurai.geeft.android.geeft.ApplicationInit;
import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSUploadGeeft;
import samurai.geeft.android.geeft.fragments.AddGeeftFragment;
import samurai.geeft.android.geeft.fragments.GeeftReceivedListFragment;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by gabriel-dev on 26/01/16.
 * Updated by danybr-dev on 1/02/16
 * Updated by gabriel-dev on 3/02/16
 */

public class AddGeeftActivity extends AppCompatActivity implements TaskCallbackBoolean,
        AddGeeftFragment.OnCheckOkSelectedListener,
        GeeftReceivedListFragment.OnGeeftImageSelectedListener{

    private static final String EXTRA_GEEFT = "extra_geeft" ;
    private static final String KEY_GEEFT_ID = "key_geeft_id";
    private static final String EXTRA_MODIFY = "extra_modify";
    private final String TAG = getClass().getName();
    private Geeft mGeeft;
    private ProgressDialog mProgress;
    private final static String TAG_ADD_GEEFT_FIELDS = "samurai.geeft.android.geeft.activities." +
            "stack_add_geeft";
    private final static String TAG_ADD_GEEFT_RECIEVED_LIST = "samurai.geeft.android.geeft.activities." +
            "stack_add_geeft_recieved_list";
    private final static String ADD_GEEFT_FRAGMENT_SAVED_STATE_KEY= "samurai.geeft.android.geeft.activities."+
            "add_geeft_fragment_seved_state";
    private final static String ADD_GEEFT_RECIEVED_LIST_FRAGMENT_SAVED_STATE_KEY = "samurai.geeft.android.geeft.activities."+
            "add_geeft_recieved_list_fragment_saved_state";


    private Map<String, Fragment.SavedState> savedStateMap;
    private ApplicationInit init;
    private String mId;
    private boolean mModify;

    public static Intent newIntent(Context context,@Nullable Geeft geeft, boolean modify) {
        Intent intent = new Intent(context, AddGeeftActivity.class);
        intent.putExtra(EXTRA_GEEFT, geeft);
        intent.putExtra(EXTRA_MODIFY, modify);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeeft = (Geeft)getIntent().getSerializableExtra(EXTRA_GEEFT);
        mModify = getIntent().getBooleanExtra(EXTRA_MODIFY, false);
        if (savedInstanceState!=null){
            mId = savedInstanceState.getString("GEEFT_ID");
            mGeeft = (Geeft)savedInstanceState.getSerializable(EXTRA_GEEFT);;
            mModify = savedInstanceState.getBoolean(EXTRA_MODIFY);
        }

        setContentView(R.layout.container_for_fragment);
        init = (ApplicationInit)getApplication();
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            if(mGeeft == null) {
                mGeeft = new Geeft();
            }
            fragment = AddGeeftFragment.newInstance(mGeeft, mModify);
            fm.beginTransaction().add(R.id.fragment_container, fragment)
                    .commit();
        }

    }

    @Override
    public void onCheckSelected(boolean startChooseStory,final Geeft geeft,final boolean modify) {
        mGeeft = geeft;
        final android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(this,
                        R.style.AppCompatAlertDialogStyle); //Read Update
        builder.setTitle("Hey");
        builder.setMessage("Hai ricevuto in precedenza tale oggetto in regalo " +
                "tramite Geeft?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            //the positive button should call the "logout method"
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //here you can add functions
                Log.d("DONE", "in startChooseStory");
                GeeftReceivedListFragment fragment =
                        GeeftReceivedListFragment.newInstance("received", false);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                Log.d("ADDGEEFT2", getFragmentManager().getBackStackEntryCount() + "");

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            //cancel the intent
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //here you can add functions
                Log.d("AAAA", geeft.getUserCap() + " " + geeft.getGeeftTitle());
                mProgress = new ProgressDialog(AddGeeftActivity.this);
                mProgress.show();
                mProgress.setCancelable(false);
                mProgress.setIndeterminate(true);
                mProgress.setMessage("Attendere");
                new BaaSUploadGeeft(getApplicationContext(), geeft,modify, AddGeeftActivity.this).execute();
            }
        });
        //On click, the user visualize can visualize some infos about the geefter
        android.support.v7.app.AlertDialog dialog = builder.create();
        //the context i had to use is the context of the dialog! not the context of the
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.show();
    }

    @Override
    public void onImageSelected(String id) {
        String mId = id;
        mProgress = new ProgressDialog(this);
        mProgress.show();
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        mProgress.setMessage("Attendere");
        new BaaSUploadGeeft(getApplicationContext(),mGeeft,mId,this).execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_GEEFT_ID, mId);
        outState.putSerializable(EXTRA_GEEFT, mGeeft);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState!=null){
            mId = savedInstanceState.getString(KEY_GEEFT_ID);
            mGeeft = (Geeft)savedInstanceState.getSerializable(EXTRA_GEEFT);
        }
    }

    public void onImageSelected(Geeft geeft){}

    public void done(boolean result){
        //enables all social buttons
        mProgress.dismiss();
        if(result){
            Toast.makeText(getApplicationContext(),
                    "Annuncio inserito con successo", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(getApplicationContext(),
                    "E' accaduto un errore riprovare",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Log.d(TAG, "HOME");
                if(getSupportFragmentManager().getBackStackEntryCount()>0){
                    getSupportFragmentManager().popBackStack();
                }else {
                    super.onBackPressed();
                }
        }
        return super.onOptionsItemSelected(item);
    }

}