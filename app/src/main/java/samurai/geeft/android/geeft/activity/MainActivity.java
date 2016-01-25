package samurai.geeft.android.geeft.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.widget.ShareDialog;

import samurai.geeft.android.geeft.R;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG ="MainActivity";
    private Toolbar mToolbar;
    /**
     * Facebook share button implementation..... If you make this better,make it!
     */
    CallbackManager mCallbackManager;
    static ShareDialog mShareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        /**
         * Facebook shareButton implementation
         */
        mCallbackManager = CallbackManager.Factory.create();
        mShareDialog = new ShareDialog(this);
        // this part is optional
        mShareDialog.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d(TAG,"shareDialog success");
            }

            @Override
            public void onCancel() {
                Log.d(TAG,"shareDialog cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "shareDialog error");
            }
        });
        if (fragment == null) {
            fragment = new GeeftListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        /*if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(),"Logout",Toast.LENGTH_LONG);
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    public static ShareDialog getShareDialog(){ return mShareDialog;}
}
