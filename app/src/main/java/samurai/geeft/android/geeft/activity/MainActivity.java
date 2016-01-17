package samurai.geeft.android.geeft.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.adapter.FeedListAdapter;
import samurai.geeft.android.geeft.data.FeedItem;
import samurai.geeft.android.geeft.db.BaaSFeedImageTask;
import samurai.geeft.android.geeft.db.TaskCallbackBoolean;

/**
 * Created by ugookeadu on 13/01/16.
 */
public class MainActivity extends AppCompatActivity implements TaskCallbackBoolean {

    private static final String TAG = "MainActivity";
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private View loadingView;
    public static void setIsGoogleUser(boolean bool){ mIsGoogleUser = bool;}
    public static boolean getIsGoogleUser(){ return mIsGoogleUser;}
    private static boolean mIsGoogleUser = false;
    private final int REQUEST_CODE_LOGOUT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(TAG,"I'm in MainActivity");
        Log.d(TAG,"Google user is: "+ mIsGoogleUser);

        if(!BaasUser.isAuthentcated()) { //Bypass G+
            BaasUser user = BaasUser.withUserName("admin")
                    .setPassword("admin");
            user.login(new BaasHandler<BaasUser>() {
                @Override
                public void handle(BaasResult<BaasUser> result) {
                    if (result.isSuccess()) {
                        Log.d("LOG", "The user is currently logged in: " + result.value());
                    } else {
                        Log.e("LOG", "Show error", result.error());
                    }
                }
            });
        }
        Log.d(TAG,"Logged in: " + BaasUser.isAuthentcated());
        Log.d(TAG,"Username is: " + BaasUser.current().getName());
        listView = (ListView) findViewById(R.id.list);

        feedItems = new ArrayList<FeedItem>();
        listAdapter = new FeedListAdapter(this, feedItems);
        Log.d(TAG,"listAdapter Created ");
        listView.setAdapter(listAdapter);
        loadingView = (View) findViewById(R.id.loading);
        loadingView.setVisibility(View.VISIBLE);

        // These two lines not needed,
        // just to get the look of facebook (changing background color & hiding the icon)
        /*getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));
*/
        new BaaSFeedImageTask(getApplicationContext(),feedItems,this).execute();
    }

    public void done(boolean result){
        // notify data changes to list adapater
        loadingView.setVisibility(View.INVISIBLE);
        if(result == true)
            listAdapter.notifyDataSetChanged();
        else
            Toast.makeText(getApplicationContext(),"Caricamento elementi non riuscito" +
                    "riprovare pri tardi.",Toast.LENGTH_LONG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Intent i = LogoutActivity.newIntent(MainActivity.this);
            startActivityForResult(i, REQUEST_CODE_LOGOUT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_LOGOUT){
            if (data == null) {
                return;
            }
            if( LogoutActivity.wasLogoutOk(data)) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Non è possible disconnettersi ora," +
                        "riprovare più tardi.", Toast.LENGTH_LONG);
            }
        }
    }
}



    /* activity_main_old code
    Button mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_old);
        mLogout = (Button) findViewById(R.id.button);
        mLogout.setOnClickListener(new View.OnClickListener() {
            //G+ Login Problem bypassed
            @Override
            public void onClick(View v) {
                if (mIsGoogleUser) {
                    Toast.makeText(MainActivity.this, "Can't Logout", Toast.LENGTH_LONG).show();
                } else {
                    BaasUser.current().logout(new BaasHandler<Void>() {
                        @Override
                        public void handle(BaasResult<Void> baasResult) {
                            if (baasResult.isSuccess()) {
                                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MainActivity.this, InitialActivity.class));
                                finish();
                            } else if (baasResult.isFailed())
                                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

    }*/
