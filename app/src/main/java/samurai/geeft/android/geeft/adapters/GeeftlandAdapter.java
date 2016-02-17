package samurai.geeft.android.geeft.adapters;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasUser;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.nvanbenschoten.motion.ParallaxImageView;
import com.squareup.picasso.Picasso;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by oldboy on 17/02/16.
 */
public class GeeftlandAdapter extends RecyclerView.Adapter<GeeftlandAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final static String TAG ="GeeftlandAdapter";

    //list containing the geefts and avoiding null pointer exception
    private List<Geeft> mGeeftList =
            Collections.emptyList();

    private int lastSize = 0;
    private Context mContext;

    private ProgressDialog mProgress;
    private long mLastClickTime = 0;

    //costructor
    public GeeftlandAdapter(Context context, List<Geeft> geeftList) {
        inflater = LayoutInflater.from(context);
        this.mGeeftList = geeftList;
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mTimeStampTextView;
        public TextView mExpireTime;
        public TextView mUserLocationTextView;
        public TextView mUserCapTextView;
        public TextView mUsernameTextView;
        public TextView mGeeftDescriptionTextView;
        public TextView mGeeftTitleTextView;
        public Boolean  mTextIsSingleLine;

        public ImageView mUserProfilePic;
        public ImageView mGeeftImage;

        public ImageButton mPrenoteButton;
        public ImageButton mLocationButton;
        public ImageButton mShareButton;
        public ImageButton mSignalisationButton;
        public LinearLayout mProfileClickableArea;

        public CardView mContainer;

        public Uri mGeeftImageUri;
        public Geeft mGeeft;
        private String app_url ="http://geeft.tk"; //Replace with direct link to Geeft in Play Store
        private String mUserId;


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

            mProfileClickableArea = (LinearLayout) itemView.findViewById(R.id.geefter_info_area);

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
                    if(mTextIsSingleLine) {
                        mGeeftDescriptionTextView.setSingleLine(false);
                        mTextIsSingleLine = false;
                    } else {
                        mGeeftDescriptionTextView.setSingleLine(true);
                        mTextIsSingleLine = true;
                    }

                }
            });
            //////////////////////////////
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public GeeftlandAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        holder.mTextIsSingleLine = true;

        holder.mGeeftTitleTextView.setText(item.getGeeftTitle());

        holder.mUserLocationTextView.setText(item.getUserLocation());

        holder.mUserCapTextView.setText(item.getUserCap());

        Picasso.with(mContext).load(item.getGeeftImage()).fit()
                .centerCrop().placeholder(R.drawable.ic_image_multiple).into(holder.mGeeftImage);
        Log.d("IMAGE", item.getUserProfilePic());
        Picasso.with(mContext).load(item.getUserProfilePic()).fit()
                .centerInside().placeholder(R.drawable.ic_account_circle)
                .into(holder.mUserProfilePic);


        if (holder.mUserLocationTextView.getText() == null) {
            // location is empty, remove from view location txt and button
            holder.mUserLocationTextView.setVisibility(View.GONE);
            holder.mLocationButton.setImageResource(R.drawable.ic_location_off);
            holder.mLocationButton.setClickable(false);
        }

        setAnimation(holder.mContainer);
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

}
