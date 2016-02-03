package samurai.geeft.android.geeft.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
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

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasFile;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSUploadGeeft;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;


/**
 * Created by gabriel-dev on 26/01/16.
 * Updated by danybr-dev on 1/02/16
 * Updated by gabriel-dev on 3/02/16
 */

public class AddGeeft extends AppCompatActivity implements TaskCallbackBoolean {

    private static final String TAG = "AddGeeft";
    private Geeft newGeft;
    private ImageButton cameraButton;
    private static final int CAPTURE_NEW_PICTURE = 1888;

    //field to fill with the edited parameters in the form field
    private TextView mGeeftTitle;  //name of the object
    private TextView mGeeftDescription;   //description of the object
    private Spinner mGeeftLocation;   //location of the geeft
    private Spinner mGeeftExpirationTime; //expire time of the Geeft
    private Spinner mGeeftCategory; //Category of the Geeft
    private ImageView mGeeftImageView;
    private ImageView mDialogImageView;

    private File mGeeftImage;

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
                //Toast.makeText(this, "TEST OK BUTTON IN TOOLBAR ", Toast.LENGTH_SHORT).show();

                //Things TODO: Send to baasbox also the "Expire time" and "Category"
                String name = mGeeftTitle.getText().toString();
                String description = mGeeftDescription.getText().toString();
                String location = mGeeftLocation.getSelectedItem().toString();
                String expTime = mGeeftExpirationTime.getSelectedItem().toString();
                String category = mGeeftCategory.getSelectedItem().toString();
                Log.d(TAG,"name: " + name + " description: " + description + " location: " +  location + " expire time: " + expTime + " category: " + category);
                if(name.length() <= 1 || description.length() <= 1 || mGeeftImageView.getDrawable() == null || location == null || expTime == null){
                    Toast.makeText(getApplicationContext(), "Bisogna compilare tutti i campi prima di procedere", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else{
                    uploadToBB(name, description, location, mGeeftImage, expTime, category);
                    finish();
                    return true;
                }
                ///////////////////////////////////////
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

        this.mGeeftImageView = (ImageView) this.findViewById(R.id.geeft_add_photo_frame);
        this.mGeeftTitle = (TextView) this.findViewById(R.id.fragment_add_geeft_form_name);
        this.mGeeftDescription = (TextView) this.findViewById(R.id.fragment_add_geeft_form_description);
        this.mGeeftLocation = (Spinner) this.findViewById(R.id.form_field_location_spinner);
        this.mGeeftExpirationTime = (Spinner) this.findViewById(R.id.expire_time_spinner);
        this.mGeeftCategory = (Spinner) this.findViewById(R.id.categories_spinner);
        //Listener for te imageButton///////////////////////////////////////////////////////////////
        cameraButton = (ImageButton) findViewById(R.id.geeft_photo_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                mGeeftImage = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mGeeftImage));
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
                R.array.cities_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        ////////////////////////////////////////////////////////////////////////////////////////////

        // Spinner for Expiration Time////////////////////////////////////////////////////////////
        Spinner spinner_exp_time = (Spinner) findViewById(R.id.expire_time_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_exp_time = ArrayAdapter.createFromResource(this,
                R.array.week_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_exp_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_exp_time.setAdapter(adapter_exp_time);
        ////////////////////////////////////////////////////////////////////////////////////////////

        // Spinner for the Geeft Categories/////////////////////////////////////////////////////////
        Spinner spinner_categories = (Spinner) findViewById(R.id.categories_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_categories = ArrayAdapter.createFromResource(this,
                R.array.categories_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_categories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_categories.setAdapter(adapter_categories);
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
    public void uploadToBB(String name,String description,String location,File geeftImage, String expTime, String category){
        //geeftImage could be useful i the case we'll want to use the stored image and not the drawn one
        Bitmap bitmap = ((BitmapDrawable)mGeeftImageView.getDrawable()).getBitmap();
        byte[] streamImage = getBytesFromBitmap(bitmap);
        //Log.d("log", "creato stream byte");
        if(mGeeftImage == null)
            Log.e(TAG,"Fatal error while upload file");
        else {
//            BaasFile file = new BaasFile();
            //TODO: add the field "automatic_selection" and "allow_comunication"
            new BaaSUploadGeeft(getApplicationContext(), name,description,location,streamImage, expTime, category, this).execute();
        }
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }


    public void done(boolean result){
        //enables all social buttons
        if(result){
            Toast.makeText(getApplicationContext(),"Annuncio inserito con successo",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"E' accaduto un errore",Toast.LENGTH_LONG).show();
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