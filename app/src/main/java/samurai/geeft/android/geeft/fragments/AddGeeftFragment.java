package samurai.geeft.android.geeft.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 08/02/16.
 */
public class AddGeeftFragment extends Fragment{
    private final String TAG = getClass().getName();

    private Geeft mGeeft;
    private ImageButton cameraButton;
    private static final int CAPTURE_NEW_PICTURE = 1888;

    //field to fill with the edited parameters in the form  field
    private TextView mGeeftTitle;  //name of the object
    private TextView mGeeftDescription;   //description of the object
    private Spinner mGeeftLocation;   //location of the geeft
    private TextView mGeeftCAP;     //cap of the area
    private Spinner mGeeftExpirationTime; //expire time of the Geeft
    private Spinner mGeeftCategory; //Category of the Geeft

    //filed for automatic selection of the geeft and for allowing the the message exchanges
    private CheckBox mAutomaticSelection;
    private CheckBox mAllowCommunication;

    private ImageView mGeeftImageView;
    private ImageView mDialogImageView;

    private File mGeeftImage;
    private Toolbar mToolbar;
    private String name;
    private String description;
    private String location;
    private String cap;
    private String expTime;
    private String category;
    private boolean automaticSelection;
    private boolean allowCommunication;
    private byte[] streamImage;
    private OnCheckOkSelectedListener mCallback;

    private static final String ARG_GEEFT = "geeft";

    public static AddGeeftFragment newInstance(Geeft geeft) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_GEEFT, geeft);
        AddGeeftFragment addGeeftFragment = new AddGeeftFragment();
        addGeeftFragment.setArguments(args);
        return addGeeftFragment;
    }

    public Geeft getGeeft() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            return (Geeft) getArguments().getSerializable(ARG_GEEFT);
        }
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_geeft_fields, container, false);
        mToolbar = (Toolbar)rootView.findViewById(R.id.fragment_add_geeft_toolbar);
        Log.d("TOOLBAR", "" + (mToolbar != null));
        if (mToolbar!=null)
            ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        mGeeftImageView = (ImageView) rootView.findViewById(R.id.geeft_add_photo_frame);
        mGeeftTitle = (TextView) rootView.findViewById(R.id.fragment_add_geeft_form_name);
        mGeeftDescription = (TextView) rootView.findViewById
                (R.id.fragment_add_geeft_form_description);
        mGeeftLocation = (Spinner) rootView.findViewById(R.id.form_field_location_spinner);
        mGeeftCAP = (TextView) rootView.findViewById(R.id.form_field_location_cap);
        mGeeftExpirationTime = (Spinner) rootView.findViewById(R.id.expire_time_spinner);
        mGeeftCategory = (Spinner) rootView.findViewById(R.id.categories_spinner);
        this.mAutomaticSelection = (CheckBox) rootView
                .findViewById(R.id.automatic_selection_checkbox);
        this.mAllowCommunication = (CheckBox) rootView
                .findViewById(R.id.allow_communication_checkbox);

        cameraButton = (ImageButton) rootView.findViewById(R.id.geeft_photo_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                mGeeftImage = new File(Environment.getExternalStorageDirectory()
                        +File.separator + "image.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mGeeftImage));
                startActivityForResult(intent, CAPTURE_NEW_PICTURE);
            }
        });
        //--------------------------------------------------------------


        //Listener for te imageView: -----------------------------------
        mGeeftImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext()); //Read Update
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.geeft_image_dialog, null);
                alertDialog.setView(dialogLayout);

                //On click, the user visualize can visualize some infos about the geefter
                AlertDialog dialog = alertDialog.create();
                //the context i had to use is the context of the dialog! not the context of the app.
                //"dialog.findVie..." instead "this.findView..."
                mDialogImageView = (ImageView) dialogLayout.findViewById(R.id.dialogGeeftImage);
                mDialogImageView.setImageDrawable(mGeeftImageView.getDrawable());

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                //dialog.setMessage("Some information that we can take from the facebook shared one");
                dialog.show();  //<-- See This!
                //Toast.makeText(getApplicationContext(), "TEST IMAGE", Toast.LENGTH_LONG).show();

            }
        });
        //--------------------------------------------------------------

        //Spinner for Location Selection--------------------------------
        Spinner spinner = (Spinner) rootView.findViewById(R.id.form_field_location_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.cities_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        //--------------------------------------------------------------

        // Spinner for Expiration Time----------------------------------
        Spinner spinner_exp_time = (Spinner) rootView.findViewById(R.id.expire_time_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_exp_time = ArrayAdapter.createFromResource(getContext(),
                R.array.week_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_exp_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_exp_time.setAdapter(adapter_exp_time);
        //--------------------------------------------------------------

        // Spinner for the Geeft Categories-----------------------------
        Spinner spinner_categories = (Spinner) rootView.findViewById(R.id.categories_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter_categories = ArrayAdapter.createFromResource
                (getContext(), R.array.categories_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter_categories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_categories.setAdapter(adapter_categories);
        //--------------------------------------------------------------

        return rootView;
    }

    /**
     * positioning uploaded; it works now: the image fit the central part of the imageView in the form
     **/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_NEW_PICTURE && resultCode == Activity.RESULT_OK) {
            File file = new File(Environment.getExternalStorageDirectory()
                    +File.separator + "image.jpg");
//            Picasso.with(this).load(file).into(mGeeftImageView);
            mGeeftImageView.setImageDrawable(null);
            Picasso.with(getContext()).load(file)
                    .fit()
                    .centerInside()
                    .into(mGeeftImageView);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.add_geeft_fragment_toolbar_menu, menu);
        Log.d("TOOLBAR", "" + inflater.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.fragment_add_geeft_ok_button:
                //Toast.makeText(this, "TEST OK BUTTON IN TOOLBAR ", Toast.LENGTH_SHORT).show();

                //Things TODO: Send to baasbox also the "Expire time" and "Category"
                name = mGeeftTitle.getText().toString();
                description = mGeeftDescription.getText().toString();
                location = mGeeftLocation.getSelectedItem().toString();
                cap = mGeeftCAP.getText().toString();
                expTime = mGeeftExpirationTime.getSelectedItem().toString();
                category = mGeeftCategory.getSelectedItem().toString();
                automaticSelection = mAutomaticSelection.isChecked();
                allowCommunication = mAllowCommunication.isChecked();

                Log.d(TAG, "name: " + name + " description: " + description + " location: " + location
                        + " cap: " + cap + " expire time: " + expTime + " category: " + category +
                        " automatic selection: " + automaticSelection + " allow communication: " +
                        allowCommunication);

                if(name.length() <= 1 || description.length() <= 1 || mGeeftImage == null
                        || location == null || cap.length() < 5 || expTime == null){
                    //TODO controlare se il cap corrisponde alla location selezionata
                    Toast.makeText(getContext(),
                            "Bisogna compilare tutti i campi prima di procedere",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    //geeftImage could be useful i the case we'll want to use the stored image and not the drawn one
                    mGeeft = getGeeft();
                    Bitmap bitmap = ((BitmapDrawable)mGeeftImageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    streamImage = stream.toByteArray();
                    //Log.d("log", "creato stream byte");
                    mGeeft.setGeeftTitle(name);
                    mGeeft.setGeeftDescription(description);
                    mGeeft.setUserLocation(location);
                    mGeeft.setUserCap(cap);
                    mGeeft.setExpTime(expTime);
                    mGeeft.setCategory(category);
                    mGeeft.setAutomaticSelection(automaticSelection);
                    mGeeft.setAllowCommunication(allowCommunication);
                    mGeeft.setStreamImage(streamImage);

                    final android.support.v7.app.AlertDialog.Builder builder =
                            new android.support.v7.app.AlertDialog.Builder(getContext(),
                                    R.style.AppCompatAlertDialogStyle); //Read Update

                    builder.setTitle("Hey");
                    builder.setMessage("Hai ricevuto in precedanza tale oggetto in regalo " +
                            "tremite Geeft?");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        //the positive button should call the "logout method"
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //here you can add functions
                            mCallback.onCheckSelected(true);

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        //cancel the intent
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //here you can add functions
                            mCallback.onCheckSelected(false);
                        }
                    });
                    //On click, the user visualize can visualize some infos about the geefter
                    android.support.v7.app.AlertDialog dialog = builder.create();
                    //the context i had to use is the context of the dialog! not the context of the
                    dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                    dialog.show();
                }
                ///////////////////////////////////////
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface OnCheckOkSelectedListener {
        void onCheckSelected(boolean startChooseStory);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnCheckOkSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
}
