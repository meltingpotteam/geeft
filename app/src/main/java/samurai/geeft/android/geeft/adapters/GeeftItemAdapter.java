package samurai.geeft.android.geeft.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasUser;
import com.bumptech.glide.Glide;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.nvanbenschoten.motion.ParallaxImageView;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.FullGeeftDetailsActivity;
import samurai.geeft.android.geeft.activities.LoginActivity;
import samurai.geeft.android.geeft.activities.MainActivity;
import samurai.geeft.android.geeft.database.BaaSGetGeefterInformation;
import samurai.geeft.android.geeft.database.BaaSReserveTask;
import samurai.geeft.android.geeft.interfaces.TaskCallBackBooleanInt;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanArrayToken;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanHolderToken;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by ugookeadu on 14/03/16.
 */
public class GeeftItemAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        TaskCallbackBooleanHolderToken,TaskCallbackBooleanArrayToken,TaskCallBackBooleanInt {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    /**
     * TAGS
     */
    private static final String TAG = "GeeftItemAdapter";
    private static final String WEBSITE_URL = TagsValue.WEBSITE_URL;

    /**
     *  BINDING
     */
    @Bind(R.id.dialog_geefter_name)TextView mProfileDialogUsername;
    @Bind(R.id.dialog_geefter_location)TextView mProfileDialogUserLocation;
    @Bind(R.id.dialog_geefter_profile_image)ImageView mProfileDialogUserImage;
    @Bind(R.id.dialog_ranking_score)TextView mProfileDialogUserRank;
    @Bind(R.id.dialog_given_geeft)TextView mProfileDialogUserGiven;
    @Bind(R.id.dialog_received_geeft)TextView mProfileDialogUserReceived;
    @Bind(R.id.dialog_geefter_background)ParallaxImageView mProfileDialogBackground;
    @Bind(R.id.dialog_geefter_facebook_button)ImageButton mProfileDialogFbButton;


    /**
     * VARIABLES
     */
    private final LayoutInflater inflater;
    private int mLastSize;
    private ProgressDialog mProgress;
    private long mLastClickTime;
    private Context mContext;
    private List<Geeft> mGeeftList;

    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    //-------------------

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public GeeftItemAdapter(Context context, List<Geeft> myDataSet, RecyclerView recyclerView) {
        mContext = context;
        mLastSize = 0;
        mLastClickTime=0;
        mGeeftList = myDataSet;
        inflater = LayoutInflater.from(context);

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }


    @Override
    public int getItemViewType(int position) {
        return mGeeftList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.geeft_list_item, parent, false);

            vh = new GeeftViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProgressViewHolder){
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }else{
            final GeeftViewHolder myHolder = ((GeeftViewHolder)holder);
            final Geeft item = mGeeftList.get(position);

            if(BaasUser.current()!=null) {
                if (item.getBaasboxUsername().equals(BaasUser.current().getName())) {
                    Log.d(TAG,"PRENOTE");
                    myHolder.mPrenoteButtonTab.setVisibility(View.GONE);
                }else{
                    myHolder.mPrenoteButtonTab.setVisibility(View.VISIBLE);
                }
            }

            /**
             * holder.mGeeftDescriptionTextView.setText(item.getGeeftDescription());
             */

            myHolder.mGeeftTitleTextView.setText(item.getGeeftTitle());
            // - replace the contents of the view with that element
            //holder.mUsernameTextView.setText(item.getUsername());

            //holder.mExpireTime.setText(item.getCreationTime()); //TODO: GESTIRE

            //holder.mUserLocationTextView.setText(item.getUserLocation());

            //holder.mUserCapTextView.setText(item.getUserCap());
            //TODO add the control of the cap matching in the city selected; sand in the maps tracking
            Glide.with(mContext).load(item.getGeeftImage()).fitCenter()
                    .centerCrop().into(myHolder.mGeeftImage);
            Log.d("IMAGE", item.getUserProfilePic());
            /**
             *  Picasso.with(mContext).load(item.getUserProfilePic()).fit().centerInside()
             .placeholder(R.drawable.ic_account_circle)
             .into(holder.mUserProfilePic);
             */



            // Converting timestamp into x ago format
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(item.getCreationTime(),
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

            //holder.mTimeStampTextView.setText(timeAgo);

            //--------------------- Display Time to GO (NOW is only days) TODO: show time to go
            Calendar c = Calendar.getInstance();
            c.setTime(new Date()); // Now use today date.
            long actualMillis = c.getTimeInMillis() / 1000; //get timestamp
            long deadlineMillis = item.getDeadLine();
            long remainingDays = (deadlineMillis - actualMillis) / 86400;
            long remainingHours = (deadlineMillis - actualMillis) % 86400 / 3600;

            if (remainingDays >0) {
                myHolder.mExpireTime.setText("Prenota entro: " + remainingDays + "g " + remainingHours + "ore");
            }
            else if(remainingHours>0) {
                myHolder.mExpireTime.setText("Prenota entro: "+remainingHours +"ore");
            }
            else {
                myHolder.mExpireTime.setText("Non più preontabile");
                myHolder.mPrenoteButtonTab.setVisibility(View.GONE);
            }

            /**The User are obliged to set a title, a description, a position, a location and an image.
             * TODO verify this part if the design of the application will change
             * in any case is better to use "if(holder.mTimeStampTextView.getText != null)"
             * instead "!TextUtils.isEmpty(item.getGeeftTitle())". It could generate some
             * false positives and it is redundant with the previous declaration
             * [gabriel-dev]
             */

        /*if (holder.mUserLocationTextView.getText() == null) {
            // location is empty, remove from view location txt and button
            holder.mUserLocationTextView.setVisibility(View.GONE);
            holder.mLocationButton.setImageResource(R.drawable.ic_location_off);
            holder.mLocationButton.setClickable(false);
        }*/

            setAnimation(myHolder.mContainer);

            if(item.isSelected())
                myHolder.mPrenoteButton.setImageResource(R.drawable.ic_reserve_on_24dp);
            else
                myHolder.mPrenoteButton.setImageResource(R.drawable.ic_reserve_off_24dp);
            //--------------------------- Prenote button implementation
            // TODO: Use this Asyntask to check if is pressed or not,and create or delete link

            myHolder.mPrenoteButtonTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    mProgress = new ProgressDialog(mContext);
                    try {
//                    mProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        mProgress.show();
                    } catch (WindowManager.BadTokenException e) {
                    }
                    mProgress.setCancelable(false);
                    mProgress.setIndeterminate(true);
                    mProgress.setMessage("Operazione in corso...");

//              mProgress = ProgressDialog.show(mContext, "Attendere...",
//                    "Prenotazione in corso", true);

                    String docUserId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id");
                    if(docUserId==null){
                        startLoginActivity();
                        ((Activity)mContext).finish();
                    }
                    Log.d(TAG, "Doc id of user is: " + docUserId + " and item id is: " + item.getId());
                    item.setIsSelected(!item.isSelected());
                    new BaaSReserveTask(mContext, docUserId, item, myHolder
                            , GeeftItemAdapter.this).execute();

                }
            });

            /**
             * when a User click on the NameText of the Geefter that Upload a geeft, this listener shows
             * a little card with some useful information about the Geefter.
             * It shows the name, the profile picture, his ranking, the number of the "Geeven" Geeft and
             * of the "Receeved" Geeft.
             * it also display the possibiliy to contact him with facebook.
             * **/
        /*holder.mProfileClickableArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileDialog(item);
            }
        });*/
            ////

            //--------------------- Location Button implementation
            final String location = item.getUserLocation().concat(","+ item.getUserCap());
            myHolder.mLocationButtonTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        if(!isGoogleMapsInstalled()) {
                            Toast.makeText(mContext,
                                    "Installa Google Maps per usare questa funzionalità", Toast.LENGTH_LONG).show();
                        }
                        else if( !location.equals("")) {
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                                    URLEncoder.encode(location, "UTF-8"));
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            mContext.startActivity(mapIntent);
                        }
                        else
                            Toast.makeText(mContext,
                                    "Non ha fornito indirizzo", Toast.LENGTH_LONG).show();
                    }catch (java.io.UnsupportedEncodingException e){
                        Toast.makeText(mContext, "Non ha fornito indirizzo", Toast.LENGTH_LONG).show();
                    }
                }
            });
            //-------------------------- ShareButton implementation
            final String title = item.getGeeftTitle();
            final Uri app_url = Uri.parse(WEBSITE_URL);
            final Uri imageUrl = Uri.parse(item.getGeeftImage());

            myHolder.mShareButtonTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d(TAG, "mShareButton onClickListener");
                    //Log.d(TAG,"position = " + holder.mGeeftTitleTextView.getText().toString());
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle(title)
                                .setContentDescription(
                                        "In questo momento è presente in regalo questo oggetto,cercalo su Geeft!")
                                .setContentUrl(app_url)
                                .setImageUrl(imageUrl)
                                .build();

                        MainActivity.getShareDialog().show(linkContent);
                    }

                }
            });

            //Signalization button Implementation--------------
        /*holder.mSignalisationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO implement the behaviour of the signalization button
                new BaaSSignalisationTask(mContext,item.getId(),GeeftItemAdapter.this).execute();
                //Toast.makeText(v.getContext(), "You have Signalate a Geeft", Toast.LENGTH_LONG).show();
            }
        });*/
            //-------------------------------------------------
            myHolder.mGeeftImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // launch full screen activity
                    Intent intent = FullGeeftDetailsActivity.newIntent(mContext,
                            item);
                    mContext.startActivity(intent);
                    Log.d(TAG,"ONCLICK");
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return mGeeftList.size();
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if ((mGeeftList.size()-mLastSize)>0)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            animation.setDuration(350);
            viewToAnimate.startAnimation(animation);mLastSize++;
        }
    }
    public static Intent getOpenFacebookProfileIntent(Context context,String userFacebookId) { // THIS
        // create a intent to user's facebook profile
        try {
            int versionCode = context.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            Log.d(TAG,"UserFacebookId is: " + userFacebookId);
            if(versionCode >= 3002850) {
                Uri uri = Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/" + userFacebookId);
                return  new Intent(Intent.ACTION_VIEW, uri);
            }
            else {
                Uri uri = Uri.parse("fb://page/" + userFacebookId);
                return  new Intent(Intent.ACTION_VIEW, uri);

            }
        } catch (Exception e) {
            Log.d(TAG,"profileDialogFbButton i'm in catch!!");
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + userFacebookId));
        }
    }



    public void done(boolean result, GeeftItemAdapter.GeeftViewHolder holder,Geeft item,int resultToken){
        //enables all social buttons
        mProgress.dismiss();
        if(!result){
            Toast toast;
            if (resultToken == RESULT_OK) {
                //DO SOMETHING
            } else if (resultToken == RESULT_SESSION_EXPIRED) {
                toast = Toast.makeText(mContext, "Sessione scaduta,è necessario effettuare di nuovo" +
                        " il login", Toast.LENGTH_LONG);
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                toast.show();
            }
            else {
                new AlertDialog.Builder(mContext)
                        .setTitle("Errore")
                        .setMessage("Operazione non possibile. Riprovare più tardi.").show();
            }
        }
        else {
            new AlertDialog.Builder(mContext)
                    .setTitle("Successo")
                    .setMessage("Operazione effettuata con successo.").show();
            Log.d("NOTATO", "" + item.isSelected());
            if(item.isSelected())
                holder.mPrenoteButton.setImageResource(R.drawable.ic_reserve_on_24dp);
            else
                holder.mPrenoteButton.setImageResource(R.drawable.ic_reserve_off_24dp);
        }
    }

    public void done(boolean result,double[] userInformation,int resultToken){
        // userInformation order is : Feedback,Given,Received
        if(result){
            mProfileDialogUserRank.setText(String.valueOf(new DecimalFormat("#.##").format(userInformation[0])) + "/5.0");
            mProfileDialogUserGiven.setText(String.valueOf((int)userInformation[1]));
            mProfileDialogUserReceived.setText(String.valueOf((int)userInformation[2]));

            //Log.d(TAG, "Ritornato AsyncTask con: " + userInformation[0] + "," + userInformation[1]
            //       + "," + userInformation[2]);

        }
        else{
            Toast toast;
            if (resultToken == RESULT_OK) {
                toast = Toast.makeText(mContext, "Nessuna nuova storia", Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            } else if (resultToken == RESULT_SESSION_EXPIRED) {
                toast = Toast.makeText(mContext, "Sessione scaduta,è necessario effettuare di nuovo" +
                        " il login", Toast.LENGTH_LONG);
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                toast.show();
            } else {
                new AlertDialog.Builder(mContext)
                        .setTitle("Errore")
                        .setMessage("Operazione non possibile. Riprovare più tardi.").show();
            }
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
                    Toast.makeText(mContext,"Documento eliminato con successo",Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(mContext,"C'è stato un errore nella segnalazione",Toast.LENGTH_LONG).show();
                    break;
            }
        }
        else{
            Toast.makeText(mContext,"C'è stato un errore nella segnalazione", Toast.LENGTH_LONG).show();
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
        mContext.startActivity(Intent.createChooser(emailIntent, "Invia mail..."));
    }

    private void showProfileDialog(@NonNull final Geeft item){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mContext); //Read Update
        View dialogLayout = inflater.inflate(R.layout.profile_dialog, null);
        alertDialog.setView(dialogLayout);
        //On click, the user visualize can visualize some infos about the geefter
        android.app.AlertDialog dialog = alertDialog.create();

        //profile dialog fields-----------------------
        mProfileDialogUsername = (TextView) dialogLayout.findViewById(R.id.dialog_geefter_name);
        mProfileDialogUserLocation = (TextView) dialogLayout.findViewById(R.id.dialog_geefter_location);
        mProfileDialogUserImage = (ImageView) dialogLayout.findViewById(R.id.dialog_geefter_profile_image);

        mProfileDialogUserRank = (TextView) dialogLayout.findViewById(R.id.dialog_ranking_score);
        mProfileDialogUserGiven = (TextView) dialogLayout.findViewById(R.id.dialog_given_geeft);
        mProfileDialogUserReceived = (TextView) dialogLayout.findViewById(R.id.dialog_received_geeft);
        mProfileDialogFbButton = (ImageButton) dialogLayout.findViewById(R.id.dialog_geefter_facebook_button);

        //--------------------------------------------
        mProfileDialogUsername
                .setText(item
                        .getUsername());
        mProfileDialogBackground = (ParallaxImageView) dialogLayout.findViewById
                (R.id.dialog_geefter_background);
        //--------------------------------------------
        mProfileDialogUsername
                .setText(item
                        .getUsername());
        mProfileDialogUserLocation.setText(item.getUserLocation());
        Picasso.with(mContext).load(Uri.parse(item.getUserProfilePic())).fit()
                .centerInside()
                .into(mProfileDialogUserImage);

        //Show Facebook profile of geefter------------------------
        if(item.isAllowCommunication()){
            mProfileDialogFbButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent facebookIntent = getOpenFacebookProfileIntent(mContext,item.getUserFbId());
                    mContext.startActivity(facebookIntent);
                }
            });
        }
        else{
            mProfileDialogFbButton.setVisibility(View.GONE);
        }

        //Parallax background -------------------------------------
        mProfileDialogBackground.setTiltSensitivity(5);
        mProfileDialogBackground.registerSensorManager();
        mProfileDialogBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse(WEBSITE_URL);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                mContext.startActivity(launchBrowser);
            }
        });
        //TODO tenere la parallasse?!
        //---------------------------------------------------------

        //TODO: fill the fields "rank" , "geeven", "receeved"-------
        //----------------------------------------------------------
        //Relaunch AsyncTask anytime is needed for give information updated
        new BaaSGetGeefterInformation(mContext,GeeftItemAdapter.this).execute();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.profile_info_dialog_animation;
        //                dialog.setMessage("Some information that we can take from the facebook shared one");
        dialog.show();  //<-- See This!
        //
    }
    public boolean isGoogleMapsInstalled()
    {
        try
        {
            ApplicationInfo info = mContext.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }



    private void startLoginActivity() {
        mContext.startActivity(new Intent(mContext, LoginActivity.class));
    }

    public static class GeeftViewHolder extends RecyclerView.ViewHolder {

        /**
         * BINDING
         */
        @Bind(R.id.geeft_name) TextView mGeeftTitleTextView;
        //@Bind(R.id.geeft_description)ExpandableTextView mGeeftDescriptionTextView;
        //@Bind(R.id.location) TextView mUserLocationTextView;
        //@Bind(R.id.geefter_name) TextView mUsernameTextView;
        @Bind(R.id.expire_time) TextView mExpireTime;
        // @Bind(R.id.timestamp)TextView mTimeStampTextView;
        //@Bind(R.id.location_cap)TextView mUserCapTextView;
        @Bind(R.id.card_view)
        CardView mContainer;
        // @Bind(R.id.geefter_info_area) LinearLayout mProfileClickableArea;
        // @Bind(R.id.geefter_profile_image) ImageView mUserProfilePic;
        @Bind(R.id.geeft_image) ImageView mGeeftImage;
        @Bind(R.id.geeft_like_reservation_button) ImageButton mPrenoteButton;
        @Bind(R.id.geeft_info_button) ImageButton mLocationButton;
        @Bind(R.id.geeft_share_button) ImageButton mShareButton;

        @Bind(R.id.geeft_reservation_button_tab) LinearLayout mPrenoteButtonTab;
        @Bind(R.id.geeft_location_button_tab) LinearLayout mLocationButtonTab;
        @Bind(R.id.geeft_share_button_tab) LinearLayout mShareButtonTab;
        //@Bind(R.id.geeft_signalisation) ImageButton mSignalisationButton;

        //-------------------Macros
        private final int RESULT_OK = 1;
        private final int RESULT_FAILED = 0;
        private final int RESULT_SESSION_EXPIRED = -1;
        //-------------------

        public Uri mGeeftImageUri;
        public Geeft mGeeft;

        /**
         * CONSTRUCTOR
         * @param itemView parent view
         */
        public GeeftViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }



    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }
}