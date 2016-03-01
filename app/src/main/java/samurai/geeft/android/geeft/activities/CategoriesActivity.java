package samurai.geeft.android.geeft.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by joseph on 29/02/16.
 */
public class CategoriesActivity extends AppCompatActivity {

    private Context mContext;

    private CardView mCategory1card;
    private CardView mCategory2card;
    private CardView mCategory3card;
    private CardView mCategory4card;
    private CardView mCategory5card;
    private CardView mCategory6card;
    private CardView mCategory7card;
    private CardView mCategory8card;
    private CardView mCategory9card;

    private ImageView mImage1;
    private ImageView mImage2;
    private ImageView mImage3;
    private ImageView mImage4;
    private ImageView mImage5;
    private ImageView mImage6;
    private ImageView mImage7;
    private ImageView mImage8;
    private ImageView mImage9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_categories_layout);

        mContext = getApplicationContext();

        mImage1 = (ImageView) findViewById(R.id.category_cloth);
        Glide.with(mContext).load(TagsValue.ASSETS_CLOTH).fitCenter()
                .centerCrop().into(mImage1);
        mImage2 = (ImageView) findViewById(R.id.category_house);
        Glide.with(mContext).load(TagsValue.ASSETS_HOUSE).fitCenter()
                .centerCrop().into(mImage2);
        mImage3 = (ImageView) findViewById(R.id.category_electronics);
        Glide.with(mContext).load(TagsValue.ASSETS_ELECTRONICS).fitCenter()
                .centerCrop().into(mImage3);
        mImage4 = (ImageView) findViewById(R.id.category_films);
        Glide.with(mContext).load(TagsValue.ASSETS_FILMS).fitCenter()
                .centerCrop().into(mImage4);
        mImage5 = (ImageView) findViewById(R.id.category_kids);
        Glide.with(mContext).load(TagsValue.ASSETS_KIDS).fitCenter()
                .centerCrop().into(mImage5);
        mImage6 = (ImageView) findViewById(R.id.category_sports);
        Glide.with(mContext).load(TagsValue.ASSETS_SPORTS).fitCenter()
                .centerCrop().into(mImage6);
        mImage7 = (ImageView) findViewById(R.id.category_motor);
        Glide.with(mContext).load(TagsValue.ASSETS_CARS).fitCenter()
                .centerCrop().into(mImage7);
        mImage8 = (ImageView) findViewById(R.id.category_services);
        Glide.with(mContext).load(TagsValue.ASSETS_SERVICES).fitCenter()
                .centerCrop().into(mImage8);
        mImage9 = (ImageView) findViewById(R.id.category_other);
        Glide.with(mContext).load(TagsValue.ASSETS_OTHER).fitCenter()
                .centerCrop().into(mImage9);

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
