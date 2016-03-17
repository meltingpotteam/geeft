package samurai.geeft.android.geeft.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by joseph on 13/03/16.
 */
public class CreditsActivity  extends AppCompatActivity {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits_view);

        TextView tx = (TextView) findViewById(R.id.credits_tittle_text);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/magnolia_sky.ttf");

        tx.setTypeface(custom_font);


    }

}
