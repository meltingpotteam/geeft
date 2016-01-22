package samurai.geeft.android.geeft.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
<<<<<<< HEAD
import android.view.Menu;
import android.view.MenuItem;
=======
>>>>>>> 66d50033b0ce29e5f1da9a3b5378e0c299af3287

import samurai.geeft.android.geeft.R;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
<<<<<<< HEAD

        mToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

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
=======

        mToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new GeeftListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
>>>>>>> 66d50033b0ce29e5f1da9a3b5378e0c299af3287
    }
}
