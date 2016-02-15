package samurai.geeft.android.geeft.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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
        GeeftStoryListFragment.OnGeeftImageSelectedListener {

    private Geeft mGeeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeeft = new Geeft();
        setContentView(R.layout.activity_add_geeft);
        Bundle bundle = new Bundle();

        AddGeeftFragment addGeeftFragment = AddGeeftFragment.newInstance(mGeeft);
        FragmentManager fm = getSupportFragmentManager();
        Log.d("SERIAL", "" + addGeeftFragment.getArguments());
        fm.beginTransaction()
                .replace(R.id.add_geeft_fields_fragment, addGeeftFragment)
                .commit();
    }


    @Override
    public void onCheckSelected(boolean startChooseStory) {

        if (startChooseStory){
            Log.d("GeeftStoryListFragment", "GeeftStoryListFragment");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            GeeftStoryListFragment geeftStoryListFragment= new GeeftStoryListFragment();
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.add_geeft_fields_fragment,
                    geeftStoryListFragment);
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();
        }
        else{
            Log.d("GEEFT",mGeeft.getUserCap()+" "+mGeeft.getGeeftTitle());
            new BaaSUploadGeeft(getApplicationContext(),mGeeft,this).execute();
        }
    }

    @Override
    public void onImageSelected(String id) {
        new BaaSUploadGeeft(getApplicationContext(),mGeeft,id,this).execute();
    }
    public void onImageSelected(Geeft geeft){}

    public void done(boolean result){
        //enables all social buttons
        if(result){
            Toast.makeText(getApplicationContext(),
                    "Annuncio inserito con successo", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "E' accaduto un errore",Toast.LENGTH_LONG).show();
        }
        finish();
    }
}