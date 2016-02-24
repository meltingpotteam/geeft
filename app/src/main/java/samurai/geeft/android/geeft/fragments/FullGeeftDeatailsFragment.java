package samurai.geeft.android.geeft.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.FullScreenImageActivity;
import samurai.geeft.android.geeft.database.BaaSGeeftHistoryArrayTask;
import samurai.geeft.android.geeft.database.BaaSSignalisationTask;
import samurai.geeft.android.geeft.interfaces.TaskCallBackBooleanInt;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.StatedFragment;

/**
 * Created by ugookeadu on 20/02/16.
 */
public class FullGeeftDeatailsFragment extends StatedFragment implements TaskCallBackBooleanInt
        , TaskCallbackBoolean{
    private final String TAG = getClass().getSimpleName();

    public static final String GEEFT_KEY = "geeft_key";
    private Geeft mGeeft;
    private Toolbar mToolbar;
    private ImageView mGeeftImageView;
    private ImageView mGeefterProfilePicImageView;
    private TextView mGeefterNameTextView;
    private TextView mGeeftTitleTextView;
    private TextView mGeeftDescriptionTextView;
    private View mStoryView;
    private List<Geeft> mGeeftList = new ArrayList<>();
    private ProgressDialog mProgressDialog;

    public static FullGeeftDeatailsFragment newInstance(Bundle b) {
        FullGeeftDeatailsFragment fragment = new FullGeeftDeatailsFragment();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState==null)
            mGeeft = (Geeft)getArguments().getSerializable(GEEFT_KEY);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_geeft_deatails, container, false);
        initUI(rootView);
        if (savedInstanceState==null)
            initSupportActionBar(rootView);
        return rootView;
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        // Save items for later restoring them on rotation
        outState.putSerializable(GEEFT_KEY, mGeeft);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        Log.d("OnRestore", savedInstanceState + "");
        if (savedInstanceState != null) {
            mGeeft = (Geeft)savedInstanceState.getSerializable(GEEFT_KEY);
            View rootView = getView();
            if (rootView!=null){
                initUI(rootView);
                initSupportActionBar(rootView);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.geeft_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_segnalation:
                segnalateGeeft();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void segnalateGeeft() {
        final android.support.v7.app.AlertDialog.Builder alertDialog =
                new android.support.v7.app.AlertDialog.Builder(getContext(),
                        R.style.AppCompatAlertDialogStyle); //Read Update

        alertDialog.setPositiveButton(R.string.segnalate_dialog_positive_answer, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new BaaSSignalisationTask(getContext(), mGeeft.getId(),FullGeeftDeatailsFragment.this).execute();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setMessage(R.string.segnalate_dialog_message);
        android.support.v7.app.AlertDialog dialog = alertDialog.create();
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.show();
    }

    private void initUI(View rootView) {
        mGeeftImageView = (ImageView)rootView.findViewById(R.id.collapsing_toolbar_image);
        mGeefterProfilePicImageView = (ImageView)rootView.findViewById(R.id.geefter_profile_image);
        mGeefterNameTextView = (TextView)rootView.findViewById(R.id.geefter_name);
        mGeeftTitleTextView = (TextView)rootView.findViewById(R.id.geeft_title_textview);
        mGeeftDescriptionTextView = (TextView)rootView
                .findViewById(R.id.geeft_description_textview);
        mStoryView = rootView.findViewById(R.id.item_geeft_story);

        if(mGeeft!=null) {
            Picasso.with(getContext()).load(mGeeft.getGeeftImage())
                    .fit().centerInside().into(mGeeftImageView);
            Picasso.with(getContext()).load(mGeeft.getUserProfilePic())
                    .fit().centerInside().placeholder(R.drawable.ic_profile_24dp)
                    .into(mGeefterProfilePicImageView);

            mGeefterNameTextView.setText(mGeeft.getUsername());
            //mGeeftTitleTextView.setText(mGeeft.getGeeftTitle());
            mGeeftDescriptionTextView.setText(mGeeft.getGeeftDescription());

            mGeeftImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Geeft> geeftList = new ArrayList<>();
                    geeftList.add(mGeeft);
                    startImageGallery(geeftList);
                }
            });

            mStoryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mGeeftList.size()==1) {
                        new AlertDialog.Builder(getContext()).setTitle(R.string.ooops)
                                .setMessage(R.string.no_story_alert_dialog_message).show();
                    }
                    else if(mGeeftList.size()>1){
                        startImageGallery(mGeeftList);
                    }
                    else {
                        mProgressDialog = ProgressDialog.show(getContext(),"","Attendere...");
                        new BaaSGeeftHistoryArrayTask(getContext(), mGeeftList,
                                mGeeft.getId(), "geeft", FullGeeftDeatailsFragment.this).execute();
                    }
                }
            });
        }
    }

    private void startImageGallery(List<Geeft> geeftList) {
        Intent intent =
                FullScreenImageActivity.newIntent(getContext(), geeftList);
        startActivity(intent);
    }

    private void initSupportActionBar(View rootView) {
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity())
                .getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mGeeft.getGeeftTitle());
    }

    @Override
    public void done(boolean result) {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        if(result) {
            if (mGeeftList.size()<2){
                new AlertDialog.Builder(getContext()).setTitle(R.string.ooops)
                        .setMessage(R.string.no_story_alert_dialog_message).show();
            }
            else {
                startImageGallery(mGeeftList);
            }
        }else {
            new AlertDialog.Builder(getContext())
                    .setTitle("Errore")
                    .setMessage("Operazione non possibile. Riprovare più tardi.").show();
        }
    }

    public void done(boolean result,int action,String docId){ //This is for signalisation button!
        // action_i with i={1,2,3}
        if(result) {
            switch (action) {
                case 1:
                    sendEmail(docId); //I'm registered user
                    break;
                case 2: //document is already deleted by BaaSSignalisationTask, I'm a moderator
                    Toast.makeText(getContext(),"Documento eliminato con successo",Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(getContext(),"C'è stato un errore nella segnalazione",Toast.LENGTH_LONG).show();
                    break;
            }
        }
        else{
            Toast.makeText(getContext(),"C'è stato un errore nella segnalazione", Toast.LENGTH_LONG).show();
        }
    }

    private void sendEmail(String docId){
        BaasUser currentUser = BaasUser.current();
        //final Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);
        //ACTION_SENDTO is filtered,but my list is empty
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "geeft.app@gmail.com" });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Segnalazione oggetto: "
                + docId);
        //Name is added in e-mail for debugging,TODO: delete
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "User: " + currentUser.getName() +
                " \n" + "E' presente un Geeft non conforme al regolamento. " + "\n"
                + "ID: " + docId);
        getContext().startActivity(Intent.createChooser(emailIntent, "Invia mail..."));
    }
}
