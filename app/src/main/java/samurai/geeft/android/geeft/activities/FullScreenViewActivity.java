package samurai.geeft.android.geeft.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSGeeftHistoryArrayTask;
import samurai.geeft.android.geeft.fragments.GeeftStoryFragment;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanToken;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 02/02/16.
 */
public class FullScreenViewActivity extends AppCompatActivity implements TaskCallbackBooleanToken{
    private final String TAG =""+this.getClass().getName();
    private static final String EXTRA_GEEFT_ID =
            "samurai.geeft.android.geeft.geeft_id";
    private final static String ARG_COLLECTION = "samurai.geeft.android.geeft.activities." +
            "FullScreenViewActivity_collection";


    private ViewPager mViewPager;
    private List<Geeft> mGeeftList = new ArrayList<>();
    private Toolbar mToolbar;
    private static String mCollection;
    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    //-------------------

    public static Intent newIntent(Context context, String geeftId, String collection) {
        Intent intent = new Intent(context, FullScreenViewActivity.class);
        intent.putExtra(EXTRA_GEEFT_ID, geeftId);
        mCollection = collection;
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "QUI");
        setContentView(R.layout.activity_full_screen_view);
        mViewPager = (ViewPager) findViewById(R.id.activity_full_screen_view_pager);
        if(savedInstanceState!=null)
                mCollection = savedInstanceState.getString(ARG_COLLECTION);

        new BaaSGeeftHistoryArrayTask(getApplicationContext(),mGeeftList,
                getIntent().getStringExtra(EXTRA_GEEFT_ID),mCollection,this).execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_COLLECTION, mCollection);
    }

    @Override
    public void done(boolean result,int resultToken) {
        if(result) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
                @Override
                public Fragment getItem(int position) {
                    Geeft geeft = mGeeftList.get(position);
                    GeeftStoryFragment geeftStoryFragment = new GeeftStoryFragment();
                    geeftStoryFragment.setGeeft(geeft);
                    return geeftStoryFragment;
                }

                @Override
                public int getCount() {
                    return mGeeftList.size();
                }
            });
        }else {
            Toast toast;
            if (resultToken == RESULT_OK) {
                //DO SOMETHING
            } else if (resultToken == RESULT_SESSION_EXPIRED) {
                toast = Toast.makeText(getApplicationContext(), "Sessione scaduta,è necessario effettuare di nuovo" +
                        " il login", Toast.LENGTH_LONG);
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                toast.show();
            } else {
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Errore")
                        .setMessage("Operazione non possibile. Riprovare più tardi.").show();
            }
        }
    }
}