package samurai.geeft.android.geeft.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeeft = new Geeft();
        if (savedInstanceState!=null){
            mId = savedInstanceState.getString("GEEFT_ID");
            mGeeft = (Geeft)savedInstanceState.getSerializable("GEEFT");
        }
        setContentView(R.layout.container_for_fragment);
        init = (ApplicationInit)getApplication();
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = AddGeeftFragment.newInstance(new Bundle());
            fm.beginTransaction().add(R.id.fragment_container, fragment)
                    .commit();
        }

    }

    @Override
    public void onCheckSelected(boolean startChooseStory,final Geeft geeft) {
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
                Bundle b = new Bundle();
                b.putString("link_name", "received");
                GeeftReceivedListFragment fragment = GeeftReceivedListFragment.newInstance(b);
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
                Log.d("AAAA",geeft.getUserCap()+" "+geeft.getGeeftTitle());
                mProgress = new ProgressDialog(AddGeeftActivity.this);
                mProgress.show();
                mProgress.setCancelable(false);
                mProgress.setIndeterminate(true);
                mProgress.setMessage("Attendere");
                new BaaSUploadGeeft(getApplicationContext(),geeft,AddGeeftActivity.this).execute();
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
        outState.putString("GEEFT_ID",mId);
        outState.putSerializable("GEEFT", mGeeft);
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

}