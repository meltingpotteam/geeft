package samurai.geeft.android.geeft.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import samurai.geeft.android.geeft.R;

/**
 * Created by joseph on 29/02/16.
 */
public class CategoriesActivity extends AppCompatActivity {

    private CardView mCategory1card;
    private CardView mCategory2card;
    private CardView mCategory3card;
    private CardView mCategory4card;
    private CardView mCategory5card;
    private CardView mCategory6card;
    private CardView mCategory7card;
    private CardView mCategory8card;
    private CardView mCategory9card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_categories_layout);
        mCategory1card = (CardView) findViewById(R.id.category_1);
        mCategory1card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mCategory2card = (CardView) findViewById(R.id.category_2);
        mCategory2card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mCategory3card = (CardView) findViewById(R.id.category_3);
        mCategory3card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mCategory4card = (CardView) findViewById(R.id.category_4);
        mCategory4card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mCategory5card = (CardView) findViewById(R.id.category_5);
        mCategory5card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mCategory6card = (CardView) findViewById(R.id.category_6);
        mCategory6card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mCategory7card = (CardView) findViewById(R.id.category_7);
        mCategory7card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mCategory8card = (CardView) findViewById(R.id.category_8);
        mCategory8card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mCategory9card = (CardView) findViewById(R.id.category_9);
        mCategory9card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }




/*
    public void done(boolean result){
        //enables all social buttons
        if(result){
            Toast.makeText(getApplicationContext(),
                    "Report inviato", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Errore nell'invio del Report",Toast.LENGTH_LONG).show();
        }
        finish();

    }
*/
}
