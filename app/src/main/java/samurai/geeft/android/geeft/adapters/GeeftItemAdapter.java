package samurai.geeft.android.geeft.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasUser;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.FullScreenViewActivity;
import samurai.geeft.android.geeft.activities.MainActivity;
import samurai.geeft.android.geeft.database.BaaSReserveTask;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanHolder;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 20/01/16.
 * adapter for GeeftListFragment Recyclerview
 * Updated by danybr-dev on 2/02/16
 * Updated by gabriel-dev on 04/02/2016
 */
public class GeeftItemAdapter extends RecyclerView.Adapter<GeeftItemAdapter.ViewHolder> implements TaskCallbackBooleanHolder {

    private final LayoutInflater inflater;

    private final static String TAG ="GeeftAdapter";

    //list containing the geefts and avoiding null pointer exception
    private List<Geeft> mGeeftList =
            Collections.emptyList();

    private int lastSize = 0;
    private Context mContext;

    private ProgressDialog mProgress;
    private long mLastClickTime = 0;

    //costructor
    public GeeftItemAdapter(Context context, List<Geeft> geeftList) {
        inflater = LayoutInflater.from(context);
        this.mGeeftList = geeftList;
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final int MAX_SELECT = 5;//Max number of prenotation for each users
        
        public TextView mTimeStampTextView;
        public TextView mExpireTime;
        public TextView mUserLocationTextView;
        public TextView mUserCapTextView;
        public TextView mUsernameTextView;
        public TextView mGeeftDescriptionTextView;
        public TextView mGeeftTitleTextView;

        public ImageView mUserProfilePic;
        public ImageView mGeeftImage;

        public ImageButton mPrenoteButton;
        public ImageButton mLocationButton;
        public ImageButton mShareButton;
        public ImageButton mSignalisationButton;

        public CardView mContainer;

        public Uri mGeeftImageUri;
        public Geeft mGeeft;
        private String app_url ="http://geeft.tk"; //Replace with direct link to Geeft in Play Store

        public ViewHolder(View itemView) {
            super(itemView);
            mContainer = (CardView) itemView.findViewById(R.id.card_view);
            mGeeftTitleTextView = (TextView) itemView.findViewById(R.id.geeft_name);
            mGeeftDescriptionTextView = (TextView) itemView.findViewById(R.id.geeft_description);
            mUserLocationTextView = (TextView) itemView.findViewById(R.id.location);
            mUsernameTextView = (TextView) itemView.findViewById(R.id.geefter_name);
            mUserCapTextView = (TextView) itemView.findViewById(R.id.location_cap);
            mTimeStampTextView = (TextView) itemView.findViewById(R.id.timestamp);
            mExpireTime = (TextView) itemView.findViewById(R.id.expire_time);

            mUserProfilePic = (ImageView) itemView.findViewById(R.id.geefter_profile_image);
            mGeeftImage = (ImageView) itemView.findViewById(R.id.geeft_image);

            mPrenoteButton = (ImageButton) itemView.findViewById(R.id.geeft_like_reservation_button);

            mLocationButton = (ImageButton) itemView.findViewById(R.id.geeft_info_button);
            mShareButton = (ImageButton) itemView.findViewById(R.id.geeft_share_button);

            mSignalisationButton = (ImageButton) itemView.findViewById(R.id.geeft_signalisation);

            //Text Expander///////////////
            mGeeftDescriptionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGeeftDescriptionTextView.setSingleLine(false);
                }
            });
            //////////////////////////////
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public GeeftItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the custom layout
        View mGeeftView = inflater.inflate(R.layout.geeft_list_item, parent, false);

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
        holder.mUsernameTextView.setText(item.getUsername());

        holder.mGeeftDescriptionTextView.setText(item.getGeeftDescription());
        holder.mGeeftDescriptionTextView.setSingleLine(true);
        holder.mGeeftDescriptionTextView.setEllipsize(TextUtils.TruncateAt.END);

        holder.mGeeftTitleTextView.setText(item.getGeeftTitle());
        holder.mTimeStampTextView.setText(item.getTimeStamp());
        holder.mExpireTime.setText(item.getExpTime());

        holder.mUserLocationTextView.setText(item.getUserLocation());

        holder.mUserCapTextView.setText(item.getUserCap());
        //TODO add the control of the cap matching in the city selected; sand in the maps tracking
        Picasso.with(mContext).load(item.getGeeftImage()).fit()
                .centerCrop().placeholder(R.drawable.ic_image_multiple).into(holder.mGeeftImage);
        Log.d("IMAGE", item.getUserProfilePic());
        Picasso.with(mContext).load(item.getUserProfilePic()).fit()
                .centerInside().placeholder(R.drawable.ic_account_circle)
                .into(holder.mUserProfilePic);


        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        holder.mTimeStampTextView.setText(timeAgo);

        /**The User are obliged to set a title, a description, a position, a location and an image.
         * TODO verify this part if the design of the application will change
         * in any case is better to use "if(holder.mTimeStampTextView.getText != null)"
         * instead "!TextUtils.isEmpty(item.getGeeftTitle())". It could generate some
         * false positives and it is redundant with the previous declaration
         * [gabriel-dev]
         */
//        // Check for empty geeft title
//        if (TextUtils.isEmpty(item.getGeeftTitle()))
//            // status is empty, remove from view
//            holder.mGeeftTitleTextView.setVisibility(View.GONE);
//
//        // Check for empty geeft description
//        if (TextUtils.isEmpty(item.getGeeftDescription()))
//            // description is empty, remove from view
//            holder.mGeeftDescriptionTextView.setVisibility(View.GONE);

//        if (TextUtils.isEmpty(item.getUserLocation())) {
//            // location is empty, remove from view location txt and button
//            holder.mUserLocationTextView.setVisibility(View.GONE);
//            holder.mLocationButton.setImageResource(R.drawable.ic_location_off);
//            holder.mLocationButton.setClickable(false);
//        }
        if (holder.mUserLocationTextView.getText() == null) {
            // location is empty, remove from view location txt and button
            holder.mUserLocationTextView.setVisibility(View.GONE);
            holder.mLocationButton.setImageResource(R.drawable.ic_location_off);
            holder.mLocationButton.setClickable(false);
        }

        setAnimation(holder.mContainer);

        if(item.isSelected())
            holder.mPrenoteButton.setImageResource(R.drawable.ic_reserve_on_24dp);
        else
            holder.mPrenoteButton.setImageResource(R.drawable.ic_reserve_off_24dp);
        //--------------------------- Prenote button implementation
        // TODO: Use this Asyntask to check if is pressed or not,and create or delete link
        // TODO: Show Dialog to limit damage (ask at Daniele)

        holder.mPrenoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            mProgress = ProgressDialog.show(mContext, "Attendere...",
                    "Prenotazione in corso", true);
            String docUserId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("doc_id");
            Log.d(TAG, "Doc id of user is: " + docUserId + " and item id is: " + item.getId());
            item.setIsSelected(!item.isSelected());
            new BaaSReserveTask(mContext,docUserId,item,holder,GeeftItemAdapter.this).execute();

        }
        });

        //--------------------- Location Button implementation
        final String location = holder.mUserLocationTextView.getText().toString();
        holder.mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if( !location.equals("")) {
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
        final String title = holder.mGeeftTitleTextView.getText().toString();
        final Uri app_url = Uri.parse(holder.app_url);
        final Uri imageUrl = holder.mGeeftImageUri;

        //Share Button Implementation----------------------
        holder.mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "mShareButton onClickListener");
                //Log.d(TAG,"position = " + holder.mGeeftTitleTextView.getText().toString());
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(title)
                            .setContentDescription(
                                    "In questo momento è presente in regalo questo oggetto tramite Geeft,visita ora!")
                            .setContentUrl(app_url)
                            .setImageUrl(imageUrl)
                            .build();

                    MainActivity.getShareDialog().show(linkContent);
                }

            }
        });
        //-------------------------------------------------

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
    public void done(boolean result, GeeftItemAdapter.ViewHolder holder,Geeft item){
        //enables all social buttons
        mProgress.dismiss();
        if(!result){
            new AlertDialog.Builder(mContext)
                    .setTitle("Errore")
                    .setMessage("Operazione non possibile. Riprovare più tardi.").show();
        }
        else {
            new AlertDialog.Builder(mContext)
                    .setTitle("Successo")
                    .setMessage("Operazione completata con successo.").show();
            Log.d("NOTATO",""+item.isSelected());
            if(item.isSelected())
                holder.mPrenoteButton.setImageResource(R.drawable.ic_reserve_on_24dp);
            else
                holder.mPrenoteButton.setImageResource(R.drawable.ic_reserve_off_24dp);
        }
    }
    /*private void dialogShow(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext,
                R.style.AppCompatAlertDialogStyle)); //Read Update
       alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            //the positive button should call the "logout method"
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //here you can add functions
                LOGOUT_METHOD
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            //cancel the intent
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //here you can add functions
                dialog.dismiss();
            }
        });
        //On click, the user visualize can visualize some infos about the geefter
        AlertDialog dialog = alertDialog.create();
        //the context i had to use is the context of the dialog! not the context of the

        //set the title
        dialog.setTitle("TITLE OF THE DIALOG")
        //if you don't want the title
        //use this: dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.setMessage("MESSAGE YOU WANT TO RETURN TO THE USER");
        dialog.show();  //<-- See This!
    }*/
}
