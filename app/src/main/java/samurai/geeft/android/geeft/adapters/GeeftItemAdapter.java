package samurai.geeft.android.geeft.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.FullScreenViewActivity;
import samurai.geeft.android.geeft.activities.MainActivity;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.ImageControllerGenerator;

/**
 * Created by ugookeadu on 20/01/16.
 * adapter for GeeftListFragment Recyclerview
 */
public class GeeftItemAdapter extends RecyclerView.Adapter<GeeftItemAdapter.ViewHolder>{

    private final LayoutInflater inflater;

    private final static String TAG ="GeeftAdapter";

    //list containing the geefts and avoiding null pointer exception
    private List<Geeft> mGeeftList =
            Collections.emptyList();

    private int lastSize = 0;
    private Context mContext;

    private boolean pressed;

    //costructor
    public GeeftItemAdapter(Context context, List<Geeft> geeftList) {
        inflater = LayoutInflater.from(context);
        this.mGeeftList = geeftList;
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTimeStampTextView;
        public TextView mUserLocationTextView;
        public TextView mUsernameTextView;
        public TextView mGeeftDescriptionTextView;
        public TextView mGeeftTitleTextView;

        public SimpleDraweeView mUserProfilePic;
        public SimpleDraweeView mGeeftImage;

        public ImageButton mPrenoteButton;
        public ImageButton mLocationButton;
        public ImageButton mShareButton;

        public CardView mContainer;

        public Uri mGeeftImageUri;
        public Geeft mGeeft;
        private String app_url ="http://geeft.tk"; //Replace with direct link to Geeft in Play Store

        //---------------
        //Max number of prenotation for each users
        private final int MAX_SELECT = 5;
        private boolean isSelected = false;
        //---------------


        public ViewHolder(View itemView) {
            super(itemView);
            mContainer = (CardView) itemView.findViewById(R.id.card_view);
            mGeeftTitleTextView = (TextView) itemView.findViewById(R.id.geeft_name);
            mGeeftDescriptionTextView = (TextView) itemView.findViewById(R.id.geeft_description);
            mUserLocationTextView = (TextView) itemView.findViewById(R.id.location);
            mUsernameTextView = (TextView) itemView.findViewById(R.id.geefter_name);
            mTimeStampTextView = (TextView) itemView.findViewById(R.id.timestamp);

            mUserProfilePic = (SimpleDraweeView) itemView.findViewById(R.id.geefter_profile_image);
            mGeeftImage = (SimpleDraweeView) itemView.findViewById(R.id.geeft_image);

            mPrenoteButton = (ImageButton) itemView.findViewById(R.id.geeft_like_reservation_button);

            mLocationButton = (ImageButton) itemView.findViewById(R.id.geeft_info_button);
            mShareButton = (ImageButton) itemView.findViewById(R.id.geeft_share_button);
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
        holder.mGeeftTitleTextView.setText(item.getGeeftTitle());
        holder.mTimeStampTextView.setText(item.getTimeStamp());
        holder.mUserLocationTextView.setText(item.getUserLocation());
        holder.mGeeftImageUri = Uri.parse(item.getGeeftImage());

        ImageControllerGenerator.generateSimpleDrawee(holder.mUserProfilePic,
                item.getUserProfilePic());
        ImageControllerGenerator.generateSimpleDrawee(holder.mGeeftImage,
                item.getGeeftImage());
        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        holder.mTimeStampTextView.setText(timeAgo);


        // Chcek for empty geeft title
        if (!TextUtils.isEmpty(item.getGeeftTitle()))
            // status is empty, remove from view
            holder.mGeeftTitleTextView.setVisibility(View.GONE);

        // Chcek for empty geeft description
        if (TextUtils.isEmpty(item.getGeeftDescription()))
            // description is empty, remove from view
            holder.mGeeftDescriptionTextView.setVisibility(View.GONE);

        if (TextUtils.isEmpty(item.getUserLocation())) {
            // location is empty, remove from view location txt and button
            holder.mUserLocationTextView.setVisibility(View.GONE);
            holder.mLocationButton.setVisibility(View.GONE);
        }
        setAnimation(holder.mContainer);
        //--------------------------- Prenote button implementation

        if(item.isSelected())
            holder.mPrenoteButton.setImageResource(R.drawable.ic_reserve_on_24dp);
        else
            holder.mPrenoteButton.setImageResource(R.drawable.ic_reserve_off_24dp);

        /* TODO: Use this Asyntask to check if is pressed or not,and create or delete link
                    String docId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("id");
                    Log.d(TAG,"Doc id of user is: " + docId + " and item id is: " + mGeeft.getId());
                    new BaaSRetrieveDoc(context,docId,mGeeft,GeeftAdapter.this).execute();*/
        holder.mPrenoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setIsSelected(!item.isSelected());
                if(item.isSelected())
                    holder.mPrenoteButton.setImageResource(R.drawable.ic_reserve_on_24dp);
                else
                    holder.mPrenoteButton.setImageResource(R.drawable.ic_reserve_off_24dp);

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
            animation.setDuration(370);
            viewToAnimate.startAnimation(animation);
            lastSize++;
        }
    }
}
