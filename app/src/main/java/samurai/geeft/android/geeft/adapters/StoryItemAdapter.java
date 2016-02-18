package samurai.geeft.android.geeft.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nvanbenschoten.motion.ParallaxImageView;

import java.util.Collections;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.FullScreenViewActivity;
import samurai.geeft.android.geeft.activities.HowToDoActivity;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanArray;
import samurai.geeft.android.geeft.interfaces.TaskCallbackStoryItem;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 17/02/16.
 */
public class StoryItemAdapter extends RecyclerView.Adapter<StoryItemAdapter.ViewHolder> implements
        TaskCallbackStoryItem,TaskCallbackBooleanArray {
    private final LayoutInflater inflater;
    private final String WEBSITE_URL = "http://geeft.tk/";
    private final static String TAG ="GeeftAdapter";

    //list containing the geefts and avoiding null pointer exception
    private List<Geeft> mGeeftList =
            Collections.emptyList();

    private int lastSize = 0;
    private Context mContext;

    private ProgressDialog mProgress;

    //info dialog attributes---------------------
    private TextView mProfileDialogUsername;
    private TextView mProfileDialogUserLocation;
    private ImageView mProfileDialogUserImage;
    private TextView mProfileDialogUserRank;
    private TextView mProfileDialogUserGiven;
    private TextView mProfileDialogUserReceived;
    private ParallaxImageView mProfileDialogBackground;

    //costructor
    public StoryItemAdapter(Context context, List<Geeft> geeftList) {
        inflater = LayoutInflater.from(context);
        this.mGeeftList = geeftList;
        this.mContext = context;
    }

    @Override
    public void done(boolean result, ViewHolder holder, double[] userInformation) {

    }

    @Override
    public void done(boolean result, ViewHolder holder, Geeft item) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        //public TextView mTimeStampTextView;
        //public TextView mUserLocationTextView;
        //public TextView mUsernameTextView;
        //public TextView mGeeftDescriptionTextView;
        public TextView mGeeftTitleTextView;
        
        //public ImageView mUserProfilePic;
        public ImageView mGeeftImage;

        //public LinearLayout mProfileClickableArea;
        public ImageButton mSignalisationButton;

        //-------------------------------------------
        public CardView mContainer;

        public Button mMoreButton;

        public Geeft mGeeft;
        private String app_url ="http://geeft.tk"; //Replace with direct link to Geeft in Play Store

        public ViewHolder(View itemView) {
            super(itemView);
            mContainer = (CardView) itemView.findViewById(R.id.card_view);
            mGeeftTitleTextView = (TextView) itemView.findViewById(R.id.geeft_name);
            //mGeeftDescriptionTextView = (TextView) itemView.findViewById(R.id.geeft_description);
            //mUserLocationTextView = (TextView) itemView.findViewById(R.id.location);
            //mUsernameTextView = (TextView) itemView.findViewById(R.id.geefter_name);
            //mTimeStampTextView = (TextView) itemView.findViewById(R.id.timestamp);
            mSignalisationButton = (ImageButton) itemView.findViewById(R.id.geeft_signalisation);
            //mProfileClickableArea = (LinearLayout) itemView.findViewById(R.id.geefter_info_area);

            //mUserProfilePic = (ImageView) itemView.findViewById(R.id.geefter_profile_image);
            mGeeftImage = (ImageView) itemView.findViewById(R.id.geeft_image);
            mMoreButton = (Button)itemView.findViewById(R.id.see_more_button);
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public StoryItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the custom layout
        View mGeeftView = inflater.inflate(R.layout.story_list_item, parent, false);

        /** set the view's size, margins, paddings and layout parameters
         *
         */

        //Inflate a new view hierarchy from the specified xml resource.
        return new ViewHolder(mGeeftView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element of the data model from list at this position
        final Geeft item = mGeeftList.get(position);


        // - replace the contents of the view with that element
        //holder.mUsernameTextView.setText(item.getUsername());

        //holder.mGeeftDescriptionTextView.setText(item.getGeeftDescription());
        //holder.mGeeftDescriptionTextView.setSingleLine(true);
        //holder.mGeeftDescriptionTextView.setEllipsize(TextUtils.TruncateAt.END);

        holder.mGeeftTitleTextView.setText(item.getGeeftTitle());
        //holder.mExpireTime.setText(item.getCreationTime()); //TODO: GESTIRE

        //TODO add the control of the cap matching in the city selected; sand in the maps tracking
        Glide.with(mContext).load(item.getGeeftImage()).fitCenter()
                .centerCrop().placeholder(R.drawable.ic_image_multiple).into(holder.mGeeftImage);
        //Log.d("IMAGE", item.getUserProfilePic());
        /*Picasso.with(mContext).load(item.getUserProfilePic()).fit()
                .centerInside().placeholder(R.drawable.ic_account_circle)
                .into(holder.mUserProfilePic);*/


        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(item.getCreationTime(),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        //holder.mTimeStampTextView.setText(timeAgo);

        /**
         * when a User click on the NameText of the Geefter that Upload a geeft, this listener shows
         * a little card with some useful information about the Geefter.
         * It shows the name, the profile picture, his ranking, the number of the "Geeven" Geeft and
         * of the "Receeved" Geeft.
         * it also display the possibiliy to contact him with facebook.
         * **/
        /*
        holder.mProfileClickableArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(v.getContext()); //Read Update
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
                Picasso.with(mContext).load(item.getUserProfilePic()).fit()
                        .centerInside()
                        .into(mProfileDialogUserImage);
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
                new BaaSGetGeefterInformation(mContext,StoryItemAdapter.this).execute();

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().getAttributes().windowAnimations = R.style.profile_info_dialog_animation;
                //                dialog.setMessage("Some information that we can take from the facebook shared one");
                dialog.show();  //<-- See This!
                //

            }
        });
        */
        ////

        //Signalization button Implementation--------------
        holder.mSignalisationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO implement the behaviour of the signalization button
                Toast.makeText(v.getContext(), "You have Signalate a Geeft", Toast.LENGTH_LONG).show();
            }
        });
        //-------------------------------------------------
        holder.mGeeftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch full screen activity
                Intent intent = FullScreenViewActivity.newIntent(mContext,
                        item.getId(),"story");
                mContext.startActivity(intent);
            }
        });

        holder.mMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = HowToDoActivity.newIntent(mContext,
                        item.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mGeeftList.size();
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if ((mGeeftList.size()-lastSize)>0)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            animation.setDuration(350);
            viewToAnimate.startAnimation(animation);
            lastSize++;
        }
    }


    public void done(boolean result,double[] userInformation){
        // userInformation order is : Feedback,Given,Received
        if(result){
            mProfileDialogUserRank.setText(String.valueOf(userInformation[0]) + "/5.0");
            mProfileDialogUserGiven.setText(String.valueOf((int)userInformation[1]));
            mProfileDialogUserReceived.setText(String.valueOf((int)userInformation[2]));

            //Log.d(TAG, "Ritornato AsyncTask con: " + userInformation[0] + "," + userInformation[1]
            //       + "," + userInformation[2]);

        }
        else{
            Log.e(TAG,"ERROREEEEE!");
        }

    }

}
