package samurai.geeft.android.geeft.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;

import samurai.geeft.android.geeft.ApplicationInit;
import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSUploadGeeft;
import samurai.geeft.android.geeft.fragments.AddGeeftFragment;
import samurai.geeft.android.geeft.fragments.GeeftStoryListFragment;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by gabriel-dev on 26/01/16.
 * Updated by danybr-dev on 1/02/16
 * Updated by gabriel-dev on 3/02/16
 */

public class AddGeeftActivity extends AppCompatActivity implements TaskCallbackBoolean,
        AddGeeftFragment.OnCheckOkSelectedListener,
        GeeftStoryListFragment.OnGeeftImageSelectedListener{

    private Geeft mGeeft;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeeft = new Geeft();
        setContentView(R.layout.activity_add_geeft);
        init = (ApplicationInit)getApplication();
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.add_geeft_fields_fragment);
        if (fragment == null) {
            fragment = AddGeeftFragment.newInstance(new Bundle());
            Fragment.SavedState savedState =
                    init.getFragmentSavedState(ADD_GEEFT_FRAGMENT_SAVED_STATE_KEY);
            if(savedState!=null)
                fragment.setInitialSavedState(savedState);

            fm.beginTransaction().add(R.id.add_geeft_fields_fragment, fragment)
                    .commit();
        }

    }

    @Override
    public void onCheckSelected(boolean startChooseStory,Geeft geeft) {
        mGeeft = geeft;
        if (startChooseStory){
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            Log.d("DONE", "in startChooseStory");
            GeeftStoryListFragment fragment = GeeftStoryListFragment.newInstance(new Bundle());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.add_geeft_fields_fragment, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
            Log.d("ADDGEEFT2", getFragmentManager().getBackStackEntryCount() + "");
        }
        else{
            Log.d("AAAA",geeft.getUserCap()+" "+geeft.getGeeftTitle());
            new BaaSUploadGeeft(getApplicationContext(),geeft,this).execute();
        }
    }

    @Override
    public void onImageSelected(String id) {
        new BaaSUploadGeeft(getApplicationContext(),mGeeft,id,this).execute();
    }

    public void done(boolean result){
        //enables all social buttons
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