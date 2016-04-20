package samurai.geeft.android.geeft.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSUploadStory;
import samurai.geeft.android.geeft.fragments.AddStoryFragment;
import samurai.geeft.android.geeft.fragments.GeeftListFragment;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by ugookeadu on 16/02/16.
 */
public class AddStoryActivity extends AppCompatActivity implements TaskCallbackBoolean,
            AddStoryFragment.OnCheckOkSelectedListener,
            GeeftListFragment.OnGeeftImageSelectedListener{
    private final String TAG = getClass().getSimpleName().toUpperCase();

    private Geeft mGeeft;
    private String mId;
    private ProgressDialog mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_for_fragment);
        mGeeft = new Geeft();

        if (savedInstanceState!=null){
            mId = savedInstanceState.getString("STORY_ID");
        }else{
            askIfIsAGefft();
        }
    }

    private void askIfIsAGefft() {
        final android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(AddStoryActivity.this,
                        R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);
        builder.setTitle("Hey!");
        builder.setMessage("Hai ricevuto o già messo in mostra l'oggetto tramite Geeft?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startFragmentGeeftList();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startFragmentGeeftory();
            }
        });
        builder.show();
    }

    private void startFragmentGeeftList() {
        Fragment fragment = GeeftListFragment.newInstance(TagsValue.LINK_NAME_RECEIVED,
                false);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void startFragmentGeeftory() {
        AddStoryFragment fragment = AddStoryFragment.newInstance(new Bundle());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onImageSelected(String id) {
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        mId = id;
        startFragmentGeeftory();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("STORY_ID", mId);
    }

    @Override
    public void onImageSelected(Geeft geeft) {
        return;
    }

    @Override
    public void onCheckSelected(boolean startChooseStory,Geeft geeft) {
        mGeeft = geeft;
        mProgress = new ProgressDialog(AddStoryActivity.this);
        mProgress.show();
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        mProgress.setMessage("Attendere");
        new BaaSUploadStory(getApplicationContext(),mGeeft,mId,this).execute();
    }

    public void done(boolean result){
        //enables all social buttons
        if (mProgress!=null) {
            mProgress.dismiss();
        }
        if(result){
            Toast.makeText(getApplicationContext(),
                    "Annuncio inserito con successo", Toast.LENGTH_LONG).show();
            finish();
        } else {
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Errore")
                    .setMessage("Riprovare più tardi")
                    .show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgress!=null){
            mProgress.dismiss();
        }
    }
}
