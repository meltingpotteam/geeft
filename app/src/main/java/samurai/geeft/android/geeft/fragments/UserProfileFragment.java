package samurai.geeft.android.geeft.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasQuery;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestOptions;
import com.baasbox.android.RequestToken;
import com.baasbox.android.Rest;
import com.baasbox.android.json.JsonObject;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.DonatedActivity;
import samurai.geeft.android.geeft.activities.ReceivedActivity;
import samurai.geeft.android.geeft.interfaces.LinkCountListener;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBoolean;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.models.User;
import samurai.geeft.android.geeft.utilities.CircleTransformation;
import samurai.geeft.android.geeft.utilities.StatedFragment;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by ugookeadu on 31/01/16.
 */
public class UserProfileFragment extends StatedFragment implements
        TaskCallbackBoolean, LinkCountListener{

    private static final String KEY_USER = "key_user";
    private static final String ARG_USER = "arg_user";
    private static final String KEY_IS_CURRENT_USER = "key_is_current_user" ;
    private static final java.lang.String ARG_IS_CURRENT_USER = "arg_is_current_user" ;
    private static final String KEY_IS_EDITING_DESCRIPTION = "key_is_editing_description";
    private static final String ARG_GEEFT = "arg_geeft" ;
    private static final String KEY_GEEFT = "key_geeft" ;

    private  final String TAG = getClass().getSimpleName();

    private TextView mUsernameTextView;
    private TextView mUserDescriptionTextView;
    private TextView mUserGivenTextView;
    private TextView mUserReceivedTextView;
    private TextView mUserFeedbackTextView;
    private Toolbar mToolbar;
    private ImageView mUserProfileImage;
    private User mUser;
    private boolean mIsCurrentUser;
    private ProgressDialog mProgressDialog;
    private LinkCountListener mCallback;
    private Button mButton;
    private boolean mIsEditingDescription;
    private EditText mUserDescriptionEditText;
    private View mLayoutDonatedView;
    private View mLayoutReceivedView;
    private RequestToken mCurrentRequest;
    private RequestToken mLinkCreateRequest;
    private Geeft mGeeft;
    private ProgressDialog progressDialog;


    public static UserProfileFragment newInstance(@Nullable User user,
                                                  boolean isCUrrentUser) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_USER, user);
        bundle.putBoolean(ARG_IS_CURRENT_USER, isCUrrentUser);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static UserProfileFragment newInstance(@Nullable User user, @Nullable Geeft geeft,
                                                  boolean isCUrrentUser) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_USER, user);
        bundle.putSerializable(ARG_GEEFT, geeft);
        bundle.putBoolean(ARG_IS_CURRENT_USER, isCUrrentUser);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "PROVA USER");
        if (savedInstanceState!=null){
            mUser = (User)savedInstanceState.getSerializable(KEY_USER);
            mIsCurrentUser = savedInstanceState.getBoolean(KEY_IS_CURRENT_USER);
            mGeeft = (Geeft)savedInstanceState.getSerializable(KEY_GEEFT);
        }else {
            mUser = (User)getArguments().getSerializable(ARG_USER);
            mIsCurrentUser = getArguments().getBoolean(ARG_IS_CURRENT_USER);
            mGeeft = (Geeft)getArguments().getSerializable(ARG_GEEFT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        initUi(rootView);
        initSupportActionBar(rootView);
        return rootView;
    }

    @Override
    protected void onFirstTimeLaunched() {
        super.onFirstTimeLaunched();
        if(mIsCurrentUser){
            mUser.setLinkGivenCount(TagsValue.USER_LINK_COUNT_NOT_FINESHED);
            mUser.setLinkReceivedCount(TagsValue.USER_LINK_COUNT_NOT_FINESHED);
            if(BaasUser.current()!=null) {
                mUser = fillUser(BaasUser.current());
            }
        }
        fillUI();
        getData();
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        outState.putSerializable(KEY_USER, mUser);
        outState.putBoolean(KEY_IS_CURRENT_USER, mIsCurrentUser);
        outState.putBoolean(KEY_IS_EDITING_DESCRIPTION, mIsEditingDescription);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        mUser = (User) savedInstanceState.getSerializable(KEY_USER);
        Log.d(TAG, "onRestore " + mUser.getFbID());
        mIsCurrentUser = savedInstanceState.getBoolean(KEY_IS_CURRENT_USER);
        mIsEditingDescription = savedInstanceState.getBoolean(KEY_IS_EDITING_DESCRIPTION);
        fillUI();
    }

    @Override
    public void done(boolean result) {
        if(result){
            fillUI();
        }
    }

    private User fillUser(BaasUser baasUser) {
        User user = new User(baasUser.getName());
        JsonObject registeredFields = baasUser.getScope(BaasUser.Scope.REGISTERED);

        String fbID = registeredFields.getObject("_social").getObject("facebook").getString("id");
        String username = registeredFields.getString("username");
        String description = registeredFields.getString("user_description");
        String docId = registeredFields.getString("doc_id");
        double userRank = registeredFields.get("feedback");

        user.setFbID(fbID);
        user.setUsername(username);
        user.setDescription(description);
        user.setDocId(docId);
        user.setRank(userRank);

        return user;
    }

    private void countLinks(BaasQuery.Criteria query, final String linkName) {
         BaasLink.fetchAll(linkName, query, RequestOptions.DEFAULT, new BaasHandler<List<BaasLink>>() {
             @Override
             public void handle(BaasResult<List<BaasLink>> baasResult) {
                 if (baasResult.isSuccess()) {
                     mCallback = UserProfileFragment.this;
                     try {
                         int count = baasResult.get().size();
                         Log.d(TAG, linkName + " size = " + count);
                         if (linkName.equals(TagsValue.LINK_NAME_RECEIVED)) {
                             mCallback.onCountedLinks(TagsValue.LINK_NAME_RECEIVED, count);

                         } else if (linkName.equals(TagsValue.LINK_NAME_DONATED)) {
                             mCallback.onCountedLinks(TagsValue.LINK_NAME_DONATED, count);
                         }
                     } catch (BaasException e) {
                         e.printStackTrace();
                         Log.d(TAG, e.getMessage().toString());
                         if (linkName.equals(TagsValue.LINK_NAME_RECEIVED)) {
                             mCallback.onCountedLinks(TagsValue.LINK_NAME_RECEIVED,
                                     TagsValue.USER_LINK_COUNT_FINESHED_WITH_ERROR);

                         } else if (linkName.equals(TagsValue.LINK_NAME_DONATED)) {
                             mCallback.onCountedLinks(TagsValue.LINK_NAME_DONATED,
                                     TagsValue.USER_LINK_COUNT_FINESHED_WITH_ERROR);
                         }
                     }
                 }
             }
         });
    }

    @Override
    public void onCountedLinks(String linkName, int count) {
        if (linkName.equals(TagsValue.LINK_NAME_DONATED)){
            setLinkCountTextView(mUserGivenTextView, count);
            mUser.setLinkGivenCount(count);
        }else if(linkName.equals(TagsValue.LINK_NAME_RECEIVED)){
            setLinkCountTextView(mUserReceivedTextView, count);
            mUser.setLinkReceivedCount(count);
        }
    }

    private void setLinkCountTextView(TextView linkTextView, int count) {
        if(count == TagsValue.USER_LINK_COUNT_NOT_FINESHED){
            return;
        }
        if(count==TagsValue.USER_LINK_COUNT_FINESHED_WITH_ERROR){
            linkTextView.setText("ND");
        }
        else {
            linkTextView.setText(count+"");
        }
    }

    private void fillUI() {
        int avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        Log.d(TAG, mUser.getProfilePic());
        Picasso.with(getContext())
                .load(mUser.getProfilePic())
                .placeholder(R.drawable.ic_account_circle)
                .centerInside()
                .resize(avatarSize, avatarSize)
                .transform(new CircleTransformation())
                .into(mUserProfileImage);
        mUsernameTextView.setText(mUser.getUsername());
        mUserFeedbackTextView.setText(new DecimalFormat("#.##").format(mUser.getRank()));
        mUserDescriptionTextView.setText(mUser.getDescription());
        mUserDescriptionEditText.setText(mUser.getDescription());
        setLinkCountTextView(mUserReceivedTextView, mUser.getLinkReceivedCount());
        setLinkCountTextView(mUserGivenTextView,mUser.getLinkGivenCount());

        Log.d(TAG, "on init is editing = " + mIsEditingDescription);
        changeButtonAdDescriptionState();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsCurrentUser && !mIsEditingDescription) {
                    mIsEditingDescription = !mIsEditingDescription;
                } else if (mIsCurrentUser && mIsEditingDescription) {
                    updateDescription();
                } else {
                    try {
                        assignCurrentGeeft();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                changeButtonAdDescriptionState();
                Log.d(TAG, "onClick is editing = " + mIsEditingDescription);
            }
        });
    }

    private void updateDescription() {
        final ProgressDialog progressDialog = ProgressDialog
                .show(getContext(), "Attendere", "Salvataggio in corso...");
        BaasUser user;
        if(mIsCurrentUser){
            final String newDescrition = mUserDescriptionEditText.getText().toString();

            user =BaasUser.current();
            user.getScope(BaasUser.Scope.REGISTERED).put("user_description", newDescrition);
            user.save(new BaasHandler<BaasUser>() {
                @Override
                public void handle(BaasResult<BaasUser> baasResult) {
                    if (baasResult.isSuccess()) {
                        BaasUser.current().refresh(new BaasHandler<BaasUser>() {
                            @Override
                            public void handle(BaasResult<BaasUser> baasResult) {
                                if (progressDialog != null) {
                                    progressDialog.dismiss();
                                }
                                if (baasResult.isSuccess()) {
                                    mUserDescriptionTextView.setText(newDescrition);
                                    mUser.setDescription(newDescrition);
                                    mIsEditingDescription = !mIsEditingDescription;
                                    changeButtonAdDescriptionState();
                                    Log.d(TAG, BaasUser.current()
                                            .getScope(BaasUser.Scope.REGISTERED)
                                            .put("user_description", newDescrition).toString());
                                } else if (baasResult.isFailed()) {
                                    showDescriptionFailDailog();
                                }
                            }
                        });
                    } else {
                        showDescriptionFailDailog();
                    }
                }
            });
        }
    }

    private void showDescriptionFailDailog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Ooops...")
                .setMessage("Operazione non riuscita")
                .setPositiveButton("Riprova", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateDescription();
                    }
                })
                .setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    private void assignCurrentGeeft() throws MalformedURLException {
        if(BaasUser.current()!=null) {
            mProgressDialog = ProgressDialog.show(getContext(), "Attendere",
                    "Operazione in corso");

            mLinkCreateRequest = BaasBox.rest().async(Rest.Method.GET, "plugin/manual" +
                    ".geeftedChoose?" +
                    "s_id=" + mGeeft.getId() +
                    "&d_id=" + mUser.getDocId() +
                    "&label=" + TagsValue.LINK_NAME_ASSIGNED +
                    "&deleteLabel=" + TagsValue.LINK_NAME_RESERVE +
                    "&geeftedName=" + mUser.getID()+
                    "&geefterFbName=" + fillUser(BaasUser.current()).getUsername()
                    .replace(" ", "%20")
                    , new BaasHandler<JsonObject>() {
                @Override
                public void handle(BaasResult<JsonObject> result) {
                    mLinkCreateRequest = null;
                    if (result.isFailed()) {
                        handleFailureLink(result.error());
                        showFailureAlert();
                    } else if (result.isSuccess()) {
                        showSuccessAlert();
                        Log.d(TAG, "IN HANDLER SUCCES");
                    }
                }
        });
        }
    }

    private void showSuccessAlert() {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        final android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(getContext(),
                        R.style.AppCompatAlertDialogStyle); //Read Update
        builder.setTitle("Successo");
        builder.setMessage("Oggetto Assegnato. Verrai contattato dall'utente su Facebook.");
    }


    private void showFailureAlert() {
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
        final android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(getContext(),
                        R.style.AppCompatAlertDialogStyle); //Read Update
        builder.setTitle("Errore");
        builder.setMessage("Errore durante assegnazione.\nRiprovare ");
        builder.setPositiveButton("Riprovare", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    assignCurrentGeeft();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void changeButtonAdDescriptionState() {
        Log.d(TAG, "is editing = " + mIsEditingDescription);
        if(mIsCurrentUser && !mIsEditingDescription){
            mButton.setText("Modifica descrizione");
            mUserDescriptionEditText.setVisibility(View.GONE);
            mUserDescriptionTextView.setVisibility(View.VISIBLE);
            mButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }else if(mIsCurrentUser && mIsEditingDescription) {
            mButton.setText("Salva descrizione");
            mUserDescriptionEditText.setVisibility(View.VISIBLE);
            mUserDescriptionTextView.setVisibility(View.GONE);
            mButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }else{
            mButton.setText("Assegna il Geeft");
        }
    }

    private void initUi(View rootView) {
        mUsernameTextView = (TextView)rootView.findViewById(R.id.username_text_view);
        mUserGivenTextView = (TextView)rootView
                .findViewById(R.id.user_given_text_view);
        mUserReceivedTextView = (TextView)rootView
                .findViewById(R.id.user_received_text_view);
        mUserProfileImage = (ImageView) rootView.findViewById(R.id.user_profile_photo);
        mUserFeedbackTextView = (TextView) rootView.findViewById(R.id.user_feedback_text_view);
        mUserReceivedTextView = (TextView) rootView.findViewById(R.id.user_received_text_view);
        mUserGivenTextView = (TextView) rootView.findViewById(R.id.user_given_text_view);
        mUserDescriptionEditText =
                (EditText) rootView.findViewById(R.id.user_description_edit_text);
        mUserDescriptionTextView = (TextView)rootView.findViewById(R.id.user_description_text_view);
        mLayoutDonatedView = rootView.findViewById(R.id.layout_donated);
        mLayoutReceivedView = rootView.findViewById(R.id.layout_received);


        mUsernameTextView.setText("...");
        mUserGivenTextView.setText("...");
        mUserReceivedTextView.setText("...");
        mUserFeedbackTextView.setText("...");

        mButton = (Button)rootView.findViewById(R.id.user_profile_button);

        if(mIsCurrentUser) {
            mLayoutDonatedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(DonatedActivity.newIntent(getContext()
                            , TagsValue.COLLECTION_GEEFT, false));
                }
            });

            mLayoutReceivedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), ReceivedActivity.class);
                    startActivity(i);
                }
            });
        }
    }


    private void initSupportActionBar(View rootView) {
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity())
                .getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    public void getData() {

        BaasQuery.Criteria query =BaasQuery.builder()
                .where("in.id like '" + mUser.getDocId() + "'" ).criteria();
        countLinks(query, TagsValue.LINK_NAME_RECEIVED);
        countLinks(query, TagsValue.LINK_NAME_DONATED);
    }

    private void handleFailure(BaasException e){
        Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(TAG, e.getCause().toString() + " " + e.getMessage().toString() );
    }

    private void handleFailureLink(BaasException error) {
    }
}
