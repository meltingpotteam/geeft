package samurai.geeft.android.geeft.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.Geeft;


/**
 * Created by gabriel-dev on 26/01/16.
 */

public class AddGeeft extends AppCompatActivity{
    private Geeft newGeft;
    private ImageButton cameraButton;
    private static final int CAPTURE_NEW_PICTURE = 1888;

    //field to fill with the edited parameters in the form field
    private TextView mGeeftName;  //name of the object
    private TextView mGeeftDescription;   //description of the object
    private TextView mGeeftLocation;   //location of the geeft
    private ImageView mGeeftImageView;
    private ImageView mDialogImageView;
    private Boolean mEnlargable;

    //Listener for the toolbar Buttons//////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_geeft_fragment_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.fragment_add_geeft_ok_button:
                Toast.makeText(this, "TEST OK BUTTON IN TOOLBAR ", Toast.LENGTH_SHORT).show();

                //Things TODO before close the activity

                ///////////////////////////////////////

                finish();
                return true;
        }
        return false;

//        return super.onOptionsItemSelected(item);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_geeft_fragment_panel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.fragment_add_geeft_toolbar);
        toolbar.setTitle("Add Geeft");
        setSupportActionBar(toolbar);

        //TODO

        this.mEnlargable = false;
        this.mGeeftImageView = (ImageView) this.findViewById(R.id.geeft_add_photo_frame);
        this.mGeeftName = (TextView) this.findViewById(R.id.fragment_add_geeft_form_name);
        this.mGeeftDescription = (TextView) this.findViewById(R.id.fragment_add_geeft_form_description);

        //Listener for te imageButton///////////////////////////////////////////////////////////////
        cameraButton = (ImageButton) findViewById(R.id.geeft_photo_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, CAPTURE_NEW_PICTURE);
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////


        //Listener for te imageView: ///////////////////////////////////////////////////////////////
        mGeeftImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext()); //Read Update
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.geeft_image_dialog, null);
                alertDialog.setView(dialogLayout);
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //here you can add functions
                        dialog.dismiss();
                    }
                });

                //On click, the user visualize can visualize some infos about the geefter

                AlertDialog dialog = alertDialog.create();
                //the context i had to use is the context of the dialog! not the context of the app.
                //"dialog.findVie..." instead "this.findView..."
                mDialogImageView = (ImageView) dialogLayout.findViewById(R.id.dialogGeeftImage);
                mDialogImageView.setImageDrawable(mGeeftImageView.getDrawable());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                //                dialog.setMessage("Some information that we can take from the facebook shared one");
                dialog.show();  //<-- See This!
                //                Toast.makeText(getApplicationContext(), "TEST IMAGE", Toast.LENGTH_LONG).show();

            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////////

        //Spinner for Location Selection////////////////////////////////////////////////////////////
        Spinner spinner = (Spinner) findViewById(R.id.form_field_location_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        ////////////////////////////////////////////////////////////////////////////////////////////
    }
    /**
     * positioning uploaded; it works now: the image fit the central part of the imageView in the form
     **/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_NEW_PICTURE && resultCode == RESULT_OK) {
            File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
//            Picasso.with(this).load(file).into(mGeeftImageView);
            mGeeftImageView.setImageDrawable(null);
            Picasso.with(this).load(file)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)        //avoid the problem of the chached
                    .networkPolicy(NetworkPolicy.NO_CACHE)      //image loading every time a new photo
//                    .config(Bitmap.Config.RGB_565)
                    .fit()
                    .centerInside()
                    .into(mGeeftImageView);
        }
    }


//    //Decoder an image from a saved picture to set it in an imageView //////////////////////////////
//    public static Bitmap decodeSampledBitmapFromFile(String path,
//                                                     int reqWidth, int reqHeight) { // BEST QUALITY MATCH
//
//        // First decode with inJustDecodeBounds=true to check dimensions
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(path, options);
//
//        // Calculate inSampleSize
//        // Raw height and width of image
//        final int height = options.outHeight;
//        final int width = options.outWidth;
//        options.inPreferredConfig = Bitmap.Config.RGB_565;
//        int inSampleSize = 1;
//
//        if (height > reqHeight) {
//            inSampleSize = Math.round((float)height / (float)reqHeight);
//        }
//
//        int expectedWidth = width / inSampleSize;
//
//        if (expectedWidth > reqWidth) {
//            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
//            inSampleSize = Math.round((float)width / (float)reqWidth);
//        }
//
//
//        options.inSampleSize = inSampleSize;
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;
//
//        return BitmapFactory.decodeFile(path, options);
//    }
//    ////////////////////////////////////////////////////////////////////////////////////////////////


}