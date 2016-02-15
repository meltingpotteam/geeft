package samurai.geeft.android.geeft.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;

import samurai.geeft.android.geeft.ApplicationInit;
import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSUploadGeeft;
import samurai.geeft.android.geeft.fragments.AddGeeftFragment;
import samurai.geeft.android.geeft.fragments.AddGeeftRecievedListFragment;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;


/**
 * Created by gabriel-dev on 26/01/16.
 * Updated by danybr-dev on 1/02/16
 * Updated by gabriel-dev on 3/02/16
 */

public class AddGeeftActivity extends AppCompatActivity implements TaskCallbackBoolean,
        AddGeeftFragment.OnCheckOkSelectedListener,
        AddGeeftRecievedListFragment.OnGeeftImageSelectedListener {

    private Geeft mGeeft;
    private  FragmentTransaction transaction;
    private final static String TAG_ADD_GEEFT_FIELDS = "samurai.geeft.android.geeft.activities." +
            "stack_add_geeft";
    private final static String TAG_ADD_GEEFT_RECIEVED_LIST = "samurai.geeft.android.geeft.activities." +
            "stack_add_geeft_recieved_list";
    private final static String ADD_GEEFT_FRAGMENT_SAVED_STATE_KEY= "samurai.geeft.android.geeft.activities."+
            "add_geeft_fragment_seved_state";
    private final static String ADD_GEEFT_RECIEVED_LIST_FRAGMENT_SAVED_STATE_KEY = "samurai.geeft.android.geeft.activities."+
            "add_geeft_recieved_list_fragment_saved_state";

    private Map<String, Fragment.SavedState> savedStateMap;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeeft = new Geeft();
        setContentView(R.layout.activity_add_geeft);

        AddGeeftFragment fragment = new AddGeeftFragment();
        Fragment.SavedState fragmentSavedState = ((ApplicationInit)getApplication())
                .getFragmentSavedState(ADD_GEEFT_FRAGMENT_SAVED_STATE_KEY);

        if(fragmentSavedState == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.add_geeft_fields_fragment,fragment).commit();
        }else{
            fragment.setInitialSavedState(fragmentSavedState);
        }

        Log.d("ADDGEEFT1", getSupportFragmentManager().getBackStackEntryCount() + "");
    }


    @Override
    public void onCheckSelected(boolean startChooseStory,Geeft geeft) {
        mGeeft = geeft;
        if (startChooseStory){
            AddGeeftRecievedListFragment addGeeftRecievedListFragment = new AddGeeftRecievedListFragment();
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            Fragment.SavedState fragmentSavedState = ((ApplicationInit)getApplication())
                    .getFragmentSavedState(ADD_GEEFT_RECIEVED_LIST_FRAGMENT_SAVED_STATE_KEY);

            AddGeeftRecievedListFragment fragment = new AddGeeftRecievedListFragment();
            if(fragmentSavedState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.add_geeft_fields_fragment,fragment).commit();
            }else{
                fragment.setInitialSavedState(fragmentSavedState);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.add_geeft_fields_fragment,fragment).commit();
            }
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

    public void setFragmentSavedState(String key, Fragment.SavedState state){
        savedStateMap.put(key, state);
    }

    public Fragment.SavedState getFragmentSavedState(String key){
        return savedStateMap.get(key);
    }

}