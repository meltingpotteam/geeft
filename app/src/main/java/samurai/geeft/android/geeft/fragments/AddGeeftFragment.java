package samurai.geeft.android.geeft.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.database.BaaSUploadGeeft;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

/**
 * Created by ugookeadu on 08/02/16.
 */
public class AddGeeftFragment extends StatedFragment implements TaskCallbackBoolean{
    private static final String KEY_LOCATION_SPINNER = "key_location_spinner" ;
    private static final String KEY_CATEGORY_SPINNER = "key_category_spinner";
    private static final String KEY_EXPIRATION_TIME_SPINNER = "key_expiration_time_spinner";
    private static final String ARG_MODIFY = "arg_modify";
    private static final String KEY_GEEFT_IMAGE = "key_geeft_image";
    private static final int REQUEST_CAMERA =2000 ;
    private final String TAG = getClass().getName();

    private static final String SHOWCASE_ID = "Showcase_single_use";

    private final String GEEFT_FOLDER = Environment.getExternalStorageDirectory()
            +File.separator+"geeft";

    private static final String ARG_GEEFT = "samurai.geeft.android.geeft.fragments." +
            "AddGeeftFragment_geeft";

    private Geeft mGeeft;
    private ImageButton cameraButton;
    private static final int CAPTURE_NEW_PICTURE = 1888;
    private static final int SELECT_PICTURE = 1;

    //field to fill with the edited parameters in the form  field
    private TextView mGeeftTitle;  //name of the object
    private TextView mGeeftDescription;   //description of the object
    private Spinner mGeeftLocation;   //location of the geeft
    private TextView mGeeftCAP;     //cap of the area
    private Spinner mGeeftExpirationTime; //expire time of the Geeft
    private Spinner mGeeftCategory; //Category of the Geeft
    private TextView mGeeftLabels; //Labels of the Geeft


    //filed for automatic selection of the geeft, geeft's dimension and for allowing the the message exchanges
    private CheckBox mAutomaticSelection;
    private CheckBox mAllowCommunication;
    private CheckBox mDimensionRead;


    private ImageView mGeeftImageView;
    private ImageView mDialogImageView;

    private EditText mGeeftHeight;
    private EditText mGeeftWidth;
    private EditText mGeeftDepth;

    private File mGeeftImage;
    private Toolbar mToolbar;
    private String name;
    private String description;
    private String location;
    private String cap;
    private String expTime;
    private String category;
    private String labels;
    private int categoryPos;
    private int locationPos;
    private int expirationDatePos;
    private boolean dimensionRead;
    private boolean automaticSelection;
    private boolean allowCommunication;
    private byte[] streamImage;
    private int deltaExptime; // is the number of "expTime" String. Is delta in integer from now to deadline
    private OnCheckOkSelectedListener mCallback;
    private boolean mModify;
    private ProgressDialog mProgress;
    private long mLastClickTime;
    private LinearLayout mDeadlineFieldLayout;
    private LinearLayout mAutomaticSelectionFieldLayout;
    private String mGeeftImagePath;

    public static AddGeeftFragment newInstance(@Nullable Geeft geeft, boolean modify) {
        AddGeeftFragment fragment = new AddGeeftFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_GEEFT, (Serializable) geeft);
        bundle.putBoolean(ARG_MODIFY, modify);
        fragment.setArguments(bundle);
        return fragment;
    }


    public Geeft getGeeft() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            return (Geeft) getArguments().getSerializable(ARG_GEEFT);
        }
        return null;
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
        Log.d("ADDGEEEFT", "onCreated");
        mGeeft = (Geeft)getArguments().getSerializable(ARG_GEEFT);
        mModify = getArguments().getBoolean(ARG_MODIFY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_geeft_page, container, false);


        if (mGeeft != null) {
            initUI(rootView);
            if(mModify)
                fillUI(rootView);
        }else{
            mGeeft= new Geeft();
            initUI(rootView);
        }

        initActionBar(rootView);


        return rootView;
    }

    private void fillUI(View rootView) {
        Picasso.with(getContext()).load(mGeeft.getGeeftImage())
                .fit()
                .centerInside()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(mGeeftImageView);

        mGeeftTitle.setText(mGeeft.getGeeftTitle());
        mGeeftDescription.setText(mGeeft.getGeeftDescription());
        mGeeftCAP.setText(mGeeft.getUserCap());

        mGeeftLabels.setText(mGeeft.getGeeftLabels());


        //expirationDatePos = (int) mGeeft.getDeadLine(); Don't use this. Is SHIT!!

        if(mModify){
            locationPos = getItemPositionLocation(mGeeftLocation,mGeeft.getUserLocation());
            Log.d(TAG,"Category: " + mGeeft.getCategory().toLowerCase());
            categoryPos = getItemPositionCategory(mGeeftCategory,mGeeft.getCategory().toLowerCase());
        }
        Log.d(TAG, "location = "+locationPos+" category = "+categoryPos);
        mGeeftLocation.setSelection(locationPos);
        mGeeftExpirationTime.setSelection(expirationDatePos);
        mGeeftCategory.setSelection(categoryPos);

        mAutomaticSelection.setChecked(mGeeft.isAutomaticSelection());
        mAllowCommunication.setChecked(mGeeft.isAllowCommunication());
        mDimensionRead.setChecked(mGeeft.isDimensionRead());
        if(mGeeft.isDimensionRead()){
            mGeeftHeight.setText(mGeeft.getGeeftHeight()+"");
            mGeeftWidth.setText(mGeeft.getGeeftWidth()+"");
            mGeeftDepth.setText(mGeeft.getGeeftDepth()+"");
        }



    }

    private void initActionBar(View rootView) {
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity())
                .getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Aggiungi un geeft");
    }

    private void initUI(View rootView) {
        mDeadlineFieldLayout = (LinearLayout) rootView.findViewById(R.id.add_geeft_deadline_field);
        mAutomaticSelectionFieldLayout = (LinearLayout) rootView.findViewById(R.id.add_geeft_automatic_selectin_field);
        mGeeftImageView = (ImageView) rootView.findViewById(R.id.geeft_add_photo_frame);
        mGeeftTitle = (TextView) rootView.findViewById(R.id.fragment_add_geeft_form_name);
        mGeeftDescription = (TextView) rootView.findViewById
                (R.id.fragment_add_geeft_form_description);
        mGeeftLocation = (Spinner) rootView.findViewById(R.id.form_field_location_spinner);
        mGeeftCAP = (TextView) rootView.findViewById(R.id.form_field_location_cap);
        mGeeftExpirationTime = (Spinner) rootView.findViewById(R.id.expire_time_spinner);
        mGeeftCategory = (Spinner) rootView.findViewById(R.id.categories_spinner);
        mGeeftLabels = (TextView) rootView.findViewById(R.id.fragment_add_geeft_labels);

        mAutomaticSelection = (CheckBox) rootView
                .findViewById(R.id.automatic_selection_checkbox);
        mAllowCommunication = (CheckBox) rootView
                .findViewById(R.id.allow_communication_checkbox);



        cameraButton = (ImageButton) rootView.findViewById(R.id.geeft_photo_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               selectImage();
            }
        });


        //Listener for the dimension checkbox--------------------------------------------------------------
        mGeeftHeight = (EditText) rootView.findViewById(R.id.geeft_height);
        mGeeftWidth = (EditText) rootView.findViewById(R.id.geeft_width);
        mGeeftDepth = (EditText) rootView.findViewById(R.id.geeft_depth);
        mDimensionRead = (CheckBox) rootView
                .findViewById(R.id.geeft_dimension_checkbox);
        mDimensionRead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mGeeftHeight.setVisibility(View.VISIBLE);
                    mGeeftWidth.setVisibility(View.VISIBLE);
                    mGeeftDepth.setVisibility(View.VISIBLE);
                    mGeeftHeight.requestFocus();
                } else {
                    mGeeftHeight.setVisibility(View.GONE);
                    mGeeftWidth.setVisibility(View.GONE);
                    mGeeftDepth.setVisibility(View.GONE);
                }
            }
        });
        //Disappear Hints on Click.
        mGeeftTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mGeeftTitle.setHint("");
                else
                    mGeeftTitle.setHint(R.string.add_gift_name_hint);
            }
        });
        mGeeftDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mGeeftDescription.setHint("");
                else
                    mGeeftDescription.setHint(R.string.add_geeft_description_hint);
            }
        });
        mGeeftCAP.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mGeeftCAP.setHint("");
                else
                    mGeeftCAP.setHint("CAP zona");
            }
        });
        mGeeftLabels.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mGeeftLabels.setHint("");
                else
                    mGeeftLabels.setHint(R.string.add_geeft_label_hint);
            }
        });
        mGeeftHeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mGeeftHeight.setHint("");
                else
                    mGeeftHeight.setHint(R.string.geeft_height_text);
            }
        });
        mGeeftWidth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mGeeftWidth.setHint("");
                else
                    mGeeftWidth.setHint(R.string.geeft_width_text);
            }
        });
        mGeeftDepth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mGeeftDepth.setHint("");
                else
                    mGeeftDepth.setHint(R.string.geeft_depth_text);
            }
        });

        /*//--------------------Check postal code   ------------------
        mGeeftCAP.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                new Thread(new CheckCap(mGeeftCAP.getText().toString(),mGeeftLocation.getSelectedItem().toString())).start();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        *///-----------------------------------------------------



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
        Log.d("onCreateView", "onActivityCreated2");
        mAutomaticSelection.setChecked(true);
        mAllowCommunication.setChecked(true);

        presentShowcaseView(350);

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

    //



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
                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return true;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                //Toast.makeText(this, "TEST OK BUTTON IN TOOLBAR ", Toast.LENGTH_SHORT).show();
                //Things TODO: Send to baasbox also the "Expire time" and "Category"
                name = mGeeftTitle.getText().toString();
                description = mGeeftDescription.getText().toString();
                location = mGeeftLocation.getSelectedItem().toString();
                cap = mGeeftCAP.getText().toString();
                expTime = mGeeftExpirationTime.getSelectedItem().toString();

                //label
                labels = mGeeftLabels.getText().toString();

                if(mGeeftExpirationTime.getSelectedItemPosition()>0)
                    deltaExptime = Integer.parseInt(expTime.split(" ")[0]);
                category = mGeeftCategory.getSelectedItem().toString();
                dimensionRead = mDimensionRead.isChecked();
                automaticSelection = mAutomaticSelection.isChecked();
                allowCommunication = mAllowCommunication.isChecked();

                /*Log.d(TAG, "name: " + name + " description: " + description + " location: " + location
                        + " cap: " + cap + " expire time: " + expTime + " category: " + category
                        + " labels: " + labels + " automatic selection: "
                        + automaticSelection + " allow communication: " + allowCommunication);*/

                if(name.length() <= 1 || description.length() <= 1
                        || mGeeftImageView.getDrawable() == null
                        || location == null || cap.length() < 5 || expTime == null ||
                        mGeeftExpirationTime.getSelectedItemPosition() == 0 ||
                        mGeeftCategory.getSelectedItemPosition() == 0){
                    //TODO controlare se il cap corrisponde alla location selezionata

                    Toast.makeText(getContext(),
                            "Bisogna compilare tutti i campi prima di procedere",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    //geeftImage could be useful i the case we'll want to use the stored image and not the drawn one
                    fillGeeft(mGeeft);
                    showDialogForStory();
                }

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * positioning uploaded; it works now: the image fit the central part of the imageView in the form
     **/
    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_NEW_PICTURE && resultCode == Activity.RESULT_OK) {
            getArguments().putString(KEY_GEEFT_IMAGE,mGeeftImage.getAbsolutePath());
            Picasso.with(getActivity()).load("file://"+mGeeftImage.getAbsolutePath())
                    .fit()
                    .centerInside()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(mGeeftImageView);
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"ON ACTIVITY RESULT");
        Log.d(TAG,"resultCode == Activity.RESULT_OK ? "+(resultCode == Activity.RESULT_OK));
        Log.d(TAG,"requestCode == REQUEST_CAMERA ? "+(requestCode == REQUEST_CAMERA));
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                getArguments().putString(KEY_GEEFT_IMAGE,mGeeftImage.getAbsolutePath());
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
                mGeeftImagePath =  "file://"+selectedImagePath;
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
        void onCheckSelected(boolean startChooseStory, Geeft mGeeft,boolean mModify);
    }

    public long getDeadlineTimestamp(int deltaExptime){ // I know,there is a delay between creation and upload time of document,
        //so we have a not matching timestamp (deadline and REAL deadline
        // calculated like creation data + exptime in days)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date.
        c.add(Calendar.DATE, deltaExptime); // Adding "expTime" days
        //String deadline = sdf.format(c.getTime()); //return Date,not timestamp.
        long deadline = c.getTimeInMillis()/1000; //get timestamp
        Log.d(TAG, "deadline is:" + deadline); //DELETE THIS AFTER DEBUG
        return deadline;
    }


    /**
     * Save Fragment's State here
     */
    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        fillGeeft(mGeeft);
        outState.putSerializable(ARG_GEEFT, mGeeft);

        if(mGeeftLocation.getSelectedItem()!=null) {
            outState.putInt(KEY_LOCATION_SPINNER, mGeeftLocation.getSelectedItemPosition());
        } else{
            outState.putInt(KEY_LOCATION_SPINNER, 0);
        }

        if(mGeeftCategory.getSelectedItem()!= null){
            outState.putInt(KEY_CATEGORY_SPINNER, mGeeftCategory.getSelectedItemPosition());
        }else{
            outState.putInt(KEY_CATEGORY_SPINNER,0);
        }

        if(mGeeftExpirationTime.getSelectedItem()!=null){
            outState.putInt(KEY_EXPIRATION_TIME_SPINNER, mGeeftExpirationTime
                    .getSelectedItemPosition());
        }else{
            outState.putInt(KEY_EXPIRATION_TIME_SPINNER, 0);
        }
        outState.putSerializable(KEY_GEEFT_IMAGE,mGeeftImage);
    }

    private void fillGeeft(Geeft geeft) {
        //------- Create a byteStream of image
        Drawable drawable = mGeeftImageView.getDrawable();
        if(drawable != null) {
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    Log.d(TAG,"image size before compression: "+bitmap.getByteCount());
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, stream);
//                    Log.d(TAG,"image size after compression: "+stream.size());
            streamImage = stream.toByteArray();
        }

        //--------
        mGeeft.setGeeftTitle(name);
        mGeeft.setGeeftDescription(description);
        mGeeft.setUserLocation(location);
        mGeeft.setUserCap(cap);
        mGeeft.setDeadLine(getDeadlineTimestamp(deltaExptime));
        mGeeft.setCategory(category);
        mGeeft.setGeeftArrayLabels(labels);
        mGeeft.setAutomaticSelection(automaticSelection);
        mGeeft.setAllowCommunication(allowCommunication);
        mGeeft.setDimensionRead(dimensionRead);
        mGeeft.setStreamImage(streamImage);
        Log.d(TAG,"DimensionRead is:" + dimensionRead+"");
        if(dimensionRead) {
            mGeeft.setGeeftHeight(Integer.parseInt(mGeeftHeight.getText().toString()));
            mGeeft.setGeeftWidth(Integer.parseInt(mGeeftWidth.getText().toString()));
            mGeeft.setGeeftDepth(Integer.parseInt(mGeeftDepth.getText().toString()));
        }
        else{
            mGeeft.setGeeftHeight(0);
            mGeeft.setGeeftWidth(0);
            mGeeft.setGeeftDepth(0);
        }
        ///////////////////////////////////////

    }

    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        if (savedInstanceState != null) {
            mGeeft = (Geeft)savedInstanceState.getSerializable(ARG_GEEFT);
            mGeeftLocation.setSelection(savedInstanceState.getInt(KEY_LOCATION_SPINNER));
            mGeeftCategory.setSelection(savedInstanceState.getInt(KEY_CATEGORY_SPINNER));
            mGeeftExpirationTime.setSelection(savedInstanceState.getInt(KEY_EXPIRATION_TIME_SPINNER));

            String path = getArguments().getString(KEY_GEEFT_IMAGE);
            //TODO: crash on restore/ rotation. Obviously needs a control before calling FIle(path) if path exist
            mGeeftImage = (File)savedInstanceState.getSerializable(KEY_GEEFT_IMAGE);
            if (mGeeftImage!=null) {
                Picasso.with(getActivity())
                        .load("file://"+mGeeftImage.getAbsolutePath())
                        .fit()
                        .centerInside()
                        .into(mGeeftImageView);
            }
        }
    }


    protected void onFirstTimeLaunched() {
        Log.d(TAG, mModify + "");
    }

    private int getItemPositionCategory(Spinner spinnerName,String itemName){
        int spinnerName_length = spinnerName.getAdapter().getCount();
        for(int i = 0;i < spinnerName_length;i++){
            if(spinnerName.getItemAtPosition(i).toString().toLowerCase().equals(itemName)){
                return i;
            }
        }
        return 0;
    }

    private int getItemPositionLocation(Spinner spinnerName,String itemName){
        int spinnerName_length = spinnerName.getAdapter().getCount();
        for(int i = 0;i < spinnerName_length;i++){
            if(spinnerName.getItemAtPosition(i).toString().equals(itemName)){
                return i;
            }
        }
        return 0;
    }



    class CheckCap implements Runnable {
        private String postal_code;
        private String administrative_area;

        public CheckCap(String postal_code, String administrative_area) {
            this.postal_code = postal_code;
            this.administrative_area = administrative_area;
        }

        private void sendGetRequest() {
            //TODO: Implementation, Get JsonObject response and check his status
        }

        @Override
        public void run() {
            sendGetRequest();
            //Do somethings
        }
    }

    private void showDialogForStory() {
        final android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(getContext(),
                        R.style.AppCompatAlertDialogStyle); //Read Update
        builder.setTitle("Hey");
        builder.setMessage("Hai ricevuto in precedenza tale oggetto in regalo " +
                "tramite Geeft?");
        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            //the positive button should call the "logout method"
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //here you can add functions
                Log.d("DONE", "in startChooseStory");
                mCallback.onCheckSelected(true, mGeeft, mModify);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            //cancel the intent
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //here you can add functions
                Log.d("AAAA", mGeeft.getUserCap() + " " + mGeeft.getGeeftTitle());
                mProgress = new ProgressDialog(getContext());
                mProgress.show();
                mProgress.setCancelable(false);
                mProgress.setIndeterminate(true);
                mProgress.setTitle("Attendere");
                mProgress.setMessage("Caricamento in corso");
                new BaaSUploadGeeft(getContext(), mGeeft, mModify, AddGeeftFragment.this).execute();
            }
        });
        //On click, the user visualize can visualize some infos about the geefter
        android.support.v7.app.AlertDialog dialog = builder.create();
        //the context i had to use is the context of the dialog! not the context of the
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.show();
    }
    
    /**
     * Tutorial Implementation
     * @param withDelay
     */
    private void presentShowcaseView(int withDelay){

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(withDelay); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setDismissText("OK")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_geeftinfo_welcomedescription))
                        .withoutShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(cameraButton)
                        .setDismissText("OK")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_camerabutton_text))
                        .build()

        );

//        mDeadlineFieldLayout.requestFocus();

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
                        .setTarget(mDeadlineFieldLayout)
                        .setDismissText("OK")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_deadline_text))
                        .withRectangleShape()
                        .build()
        );
//
//        mAutomaticSelectionFieldLayout.requestFocus();

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
//                        .setTarget(mAutomaticSelectionFieldLayout)
                        .setDismissText("OK")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_additionalinfo_automaticselection_text))
                        .withoutShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
//                        .setTarget(mAutomaticSelectionFieldLayout)
                        .setDismissText("OK")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_additionalinfo_allowContact_text))
                        .withoutShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(getActivity())
//                        .setTarget(mAutomaticSelectionFieldLayout)
                        .setDismissText("HO CAPITO")
                        .setMaskColour(Color.parseColor("#f11d5e88"))
                        .setDismissTextColor(Color.parseColor("#F57C00"))
                        .setContentText(getString(R.string.tutorial_additionalinfo_bigobject_text))
                        .withoutShape()
                        .build()
        );

        sequence.start();

    }


    @Override
    public void done(boolean result) {
        if(mProgress!=null){
            mProgress.dismiss();
        }
        if(result){
            Toast.makeText(getContext(),
                    "Annuncio inserito con successo", Toast.LENGTH_LONG).show();
            getActivity().finish();
        } else {
            Toast.makeText(getContext(),
                    "E' accaduto un errore riprovare",Toast.LENGTH_LONG).show();
        }
    }
}
