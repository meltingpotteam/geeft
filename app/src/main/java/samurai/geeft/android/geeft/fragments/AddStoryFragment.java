package samurai.geeft.android.geeft.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.CursorLoader;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by ugookeadu on 17/02/16.
 * Updated by gabriele-dev on 25/03/16
 */
public class AddStoryFragment extends StatedFragment {
    private final String TAG = getClass().getName();
    private static final String ARG_GEEFT = "samurai.geeft.android.geeft.fragments." +
            "AddStoryFragment_geeft";
    private final static String ARG_ARRAY_STRINGS = "samurai.geeft.android.geeft.fragments." +
            "AddStoryFragment_arrayStrings";
    private final static String ARG_SELECTED_ITEMS = "samurai.geeft.android.geeft.fragments." +
            "AddStoryFragment_selectedItems";
    private final static String ARG_CHECKED_ITEMS = "samurai.geeft.android.geeft.fragments." +
            "AddStoryFragment_checkedItems";
    private final static String ARG_FILE = "samurai.geeft.android.geeft.fragments." +
            "AddStoryFragment_file";

    private static final String SHOWCASE_ID_STORY = "Showcase_single_story";

    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_CAMERA =2000 ;
    private final String GEEFT_FOLDER = Environment.getExternalStorageDirectory()
            +File.separator+"geeft";

    private Geeft mGeeft;
    private FloatingActionButton cameraButton;
    private static final int CAPTURE_NEW_PICTURE = 1888;

    //field to fill with the edited parameters in the form  field
    private TextView mGeeftTitle;  //name of the object
    private TextView mGeeftDescription;   //description of the object
    private Spinner mGeeftCategory; //Category of the Geeft

    //filed for automatic selection of the geeft and for allowing the the message exchanges
    private CheckBox mAllowCommunication;

    private ImageView mGeeftImageView;
    private ImageView mDialogImageView;

    private File mGeeftImage;
    private Toolbar mToolbar;
    private String name;
    private String description;
    private String category;
    private boolean automaticSelection;
    private byte[] streamImage;
    private OnCheckOkSelectedListener mCallback;
    private LinearLayout mCategoryFieldLayout;
    private LinearLayout mDescriptionFieldLayout;
    private String mGeeftImagePath;

    public static AddStoryFragment newInstance(Bundle b) {
        AddStoryFragment fragment = new AddStoryFragment();
        fragment.setArguments(b);

        return fragment;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_story, container, false);
        initUI(rootView);
        if (savedInstanceState==null)
            initSupportActionBar(rootView);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.toolbar_button_menu, menu);
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
                category = mGeeftCategory.getSelectedItem().toString();

                Log.d(TAG, "name: " + name + " description: " + description + " location: " +
                        " category: " + category + " automatic selection: "
                        + automaticSelection + " allow communication: " );

                if(name.length() <= 1 || description.length() <= 1
                        || mGeeftImageView.getDrawable() == null
                        || mGeeftCategory.getSelectedItemPosition() == 0){
                    //TODO controlare se il cap corrisponde alla location selezionata
                    Toast.makeText(getContext(),
                            "Bisogna compilare tutti i campi prima di procedere",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    //geeftImage could be useful i the case we'll want to use the stored image and not the drawn one
                    mGeeft = new Geeft();
                    //------- Create a byteStream of image
                    Bitmap bitmap = ((BitmapDrawable) mGeeftImageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                    streamImage = stream.toByteArray();
                    //--------
                    mGeeft.setGeeftTitle(name);
                    mGeeft.setGeeftDescription(description);
                    mGeeft.setCategory(category);
                    mGeeft.setStreamImage(streamImage);
                    ///////////////////////////////////////
                    mCallback.onCheckSelected(true,mGeeft);
                    return true;
                }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectImage() {
        final CharSequence[] items = { "Scatta una foto", "Aggiungi dalla galleria", "Annulla" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Scatta una foto")) {
                    File folder = new File(GEEFT_FOLDER);
                    boolean success = true;
                    if (!folder.exists()) {
                        success = folder.mkdir();
                    }
                    mGeeftImage = new File(GEEFT_FOLDER + File.separator + "geeftimg" + ".jpg");
                    Log.d(TAG, "mGeeftImage = "+mGeeftImage.getAbsolutePath());
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mGeeftImage));
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Aggiungi dalla galleria")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_PICTURE);
                } else if (items[item].equals("Cancella")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * positioning uploaded; it works now: the image fit the central part of the imageView in the form
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"ON ACTIVITY RESULT");
        Log.d(TAG,"resultCode == Activity.RESULT_OK ? "+(resultCode == Activity.RESULT_OK));
        Log.d(TAG,"requestCode == REQUEST_CAMERA ? "+(requestCode == REQUEST_CAMERA));
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                mGeeftImagePath = "file://"+mGeeftImage.getAbsolutePath();
                Picasso.with(getActivity()).load(mGeeftImagePath)
                        .fit()
                        .centerInside()
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(mGeeftImageView);
            } else if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(getContext(), selectedImageUri,
                        projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                mGeeftImage = new File(selectedImageUri.getPath());
                Log.d(TAG, "getAbsolutePath() = " + mGeeftImage.getAbsolutePath());
                Log.d(TAG,"selectedImagePath() = "+selectedImagePath);
                Log.d(TAG, "selectedImagePath() = " + selectedImageUri);
                Log.d(TAG, "mGeeftImage.getAbsolutePath() = "+mGeeftImage.getAbsolutePath());
                mGeeftImagePath= "file://" + selectedImagePath;
                Picasso.with(getActivity()).load(mGeeftImagePath)
                        .fit()
                        .centerInside()
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(mGeeftImageView);
            }
        }
    }

    public interface OnCheckOkSelectedListener {
        void onCheckSelected(boolean startChooseStory, Geeft mGeeft);
    }


    /**
     * Save Fragment's State here
     */
    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        outState.putSerializable(ARG_FILE, mGeeftImage);

        String arrayStrings[] = {
                mGeeftTitle.getText().toString(),
                mGeeftDescription.getText().toString(),
        };
        outState.putStringArray(ARG_ARRAY_STRINGS, arrayStrings);
        outState.putSerializable(ARG_SELECTED_ITEMS, mGeeftCategory.getSelectedItemPosition());
    }

    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        if (savedInstanceState != null) {
            View rootView = getView();
            if (rootView!=null){
                initUI(rootView);
                initSupportActionBar(rootView);
            }
            mGeeftImage = (File)savedInstanceState.getSerializable(ARG_FILE);
            if(mGeeftImage!=null) {
                Picasso.with(getActivity()).invalidate(mGeeftImage);
                Picasso.with(getContext()).load(mGeeftImagePath)
                        .fit()
                        .centerInside()
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(mGeeftImageView);
            }

            String arrayStrings[] =  savedInstanceState.getStringArray(ARG_ARRAY_STRINGS);
            if(arrayStrings!=null) {
                mGeeftTitle.setText(arrayStrings[0]);
                mGeeftDescription.setText(arrayStrings[1]);
            }


            int selectedItems = (int)savedInstanceState.getSerializable(ARG_SELECTED_ITEMS);
            mGeeftCategory.setSelection(selectedItems);
            Log.d("SELECTED",mGeeftCategory.getSelectedItemPosition()+"");
        }

    }

    @Override
    protected void onFirstTimeLaunched() {

    }

    private void initSupportActionBar(View rootView) {
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity())
                .getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Geeftory");
    }

    private void initUI(View rootView) {
        mCategoryFieldLayout = (LinearLayout) rootView.findViewById(R.id.add_story_category_field);
        mDescriptionFieldLayout = (LinearLayout) rootView.findViewById(R.id.add_story_description_field);
        mGeeftImageView = (ImageView) rootView.findViewById(R.id.geeft_add_photo_frame);
        mGeeftTitle = (TextView) rootView.findViewById(R.id.fragment_add_geeft_form_name);
        mGeeftDescription = (TextView) rootView.findViewById
                (R.id.fragment_add_geeft_form_description);
        mGeeftCategory = (Spinner) rootView.findViewById(R.id.categories_spinner);
        this.mAllowCommunication = (CheckBox) rootView
                .findViewById(R.id.allow_communication_checkbox);

        cameraButton = (FloatingActionButton) rootView.findViewById(R.id.geeft_photo_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        //--------------------------------------------------------------


        //Listener for te imageView: -----------------------------------
        mGeeftImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity()); //Read Update
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.geeft_image_dialog, null);
                alertDialog.setView(dialogLayout);

                //On click, the user visualize can visualize some infos about the geefter
                AlertDialog dialog = alertDialog.create();
                //the context i had to use is the context of the dialog! not the context of the app.
                //"dialog.findVie..." instead "this.findView..."

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                mDialogImageView = (ImageView) dialogLayout.findViewById(R.id.dialogGeeftImage);
//                mDialogImageView.setImageDrawable(mGeeftImageView.getDrawable());

                Picasso.with(getActivity()).load(mGeeftImagePath)
                        .config(Bitmap.Config.ARGB_8888)
                        .fit()
                        .centerInside()
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(mDialogImageView);

                dialog.getWindow().getAttributes().windowAnimations = R.style.scale_up_animation;
                //dialog.setMessage("Some information that we can take from the facebook shared one");
                dialog.show();  //<-- See This!
                //Toast.makeText(getApplicationContext(), "TEST IMAGE", Toast.LENGTH_LONG).show();

            }
        });
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
        Log.d("onCreateView", "onActivityCreated2");

        presentShowcaseView(350);

    }

    private void presentShowcaseView(int withDelay){

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(withDelay); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID_STORY);

        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setDismissText("OK")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_storyinfo_text))
                        .withoutShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(cameraButton)
                        .setDismissText("OK")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_storycamerabutton_text))
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mCategoryFieldLayout)
                        .setDismissText("OK")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_storycategory_text))
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mDescriptionFieldLayout)
                        .setDismissText("HO CAPITO")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_storydescription_text))
                        .withRectangleShape()
                        .build()
        );

        sequence.start();

    }
}
