package samurai.geeft.android.geeft.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.net.URLEncoder;
import java.util.List;

import samurai.geeft.android.geeft.activity.MainActivity;
import samurai.geeft.android.geeft.util.ImageControllerGenerator;
import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.model.Geeft;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class GeeftAdapter extends RecyclerView.Adapter<GeeftAdapter.ViewHolder>{

    private final static String TAG ="GeeftAdapter";
    //list containing the geefts
    private List<Geeft> mGeeftList;
    //card layout id
    private int layoutID;
    //the cntext using the list
    private static Context context;

    //---------------
    //Max number of prenotation for each users
    private final int MAX_SELECT = 5;

    //---------------

    //OLD: public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTimeStampTextView;
        public TextView mUserLocationTextView;
        public TextView mUsernameTextView;
        public TextView mGeeftDescriptionTextView;
        public TextView mGeeftTitleTextView;

        public Uri mUserProfilePicUri;
        public Uri mGeeftImageUri;
        public SimpleDraweeView mUserProfilePic;
        public SimpleDraweeView mGeeftImage;

        public ImageButton mPrenoteButton;
        public ImageButton mLocationButton;
        public ImageButton mShareButton;

        private String app_url ="http://geeft.tk"; //Replace with direct link to Geeft in Play Store

        public ViewHolder(View itemView) {
            super(itemView);
           // itemView.setOnClickListener(this);
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


            mLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        String position = mUserLocationTextView.getText().toString();
                        if( !position.equals("")) {
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                                    URLEncoder.encode(position, "UTF-8"));
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            context.startActivity(mapIntent);
                        }
                        else
                            Toast.makeText(context,
                                    "Non ha fornito indirizzo",Toast.LENGTH_LONG);
                    }catch (java.io.UnsupportedEncodingException e){
                        Toast.makeText(context, "Non ha fornito indirizzo", Toast.LENGTH_LONG);
                    }
                }
            });

            mShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d(TAG,"mShareButton onClickListener");
                    Log.d(TAG,"position = " + getAdapterPosition());
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle(mGeeftTitleTextView.getText().toString())
                                .setContentDescription(
                                        "In questo momento è presente in regalo questo oggetto tramite Geeft,visita ora!")
                                .setContentUrl(Uri.parse(app_url))
                                .setImageUrl(mGeeftImageUri)
                                .build();

                        MainActivity.getShareDialog().show(linkContent);
                    }

                }
            });
        }

    }

    //costuctor
    public GeeftAdapter(List<Geeft> geeftList, int layoutID, Context context) {
        this.mGeeftList = geeftList;
        this.layoutID = layoutID;
        this.context = context;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public GeeftAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // get the context calling the adapter
        Context context = parent.getContext();

        // create an LayoutInflater for the context
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate the custom layout
        View mGeeftView = inflater.inflate(layoutID, parent, false);

        /** set the view's size, margins, paddings and layout parameters
         *
         */

        //Inflate a new view hierarchy from the specified xml resource.
        ViewHolder mViewHolder = new ViewHolder(mGeeftView);

        return mViewHolder;
    }

    //i need to specify the target because "getDrawable" is for lollipop build
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element of the data model from list at this position
        Geeft item = mGeeftList.get(position);

        // - replace the contents of the view with that element
        holder.mUsernameTextView.setText(item.getUsername());
        holder.mGeeftDescriptionTextView.setText(item.getGeeftDescription());
        holder.mGeeftTitleTextView.setText(item.getGeeftTitle());
        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

        holder.mTimeStampTextView.setText(timeAgo);
        holder.mUserLocationTextView.setText(item.getUserLocation());

        holder.mUserProfilePicUri = Uri.parse(item.getUserProfilePic());
        holder.mGeeftImageUri = Uri.parse(item.getGeeftImage());

        ImageControllerGenerator.generateSimpleDrawee(holder.mUserProfilePic,
                item.getUserProfilePic());
        ImageControllerGenerator.generateSimpleDrawee(holder.mGeeftImage,
                item.getGeeftImage());


        // Check for empty geeft title
        if (!TextUtils.isEmpty(item.getGeeftTitle()))
            // status is empty, remove from view
            holder.mGeeftTitleTextView.setVisibility(View.GONE);

        // Check for empty geeft description
        if (TextUtils.isEmpty(item.getGeeftDescription()))
            // description is empty, remove from view
            holder.mGeeftDescriptionTextView.setVisibility(View.GONE);

        if (TextUtils.isEmpty(item.getUserLocation())) {
            // location is empty, remove from view location txt and button
            holder.mUserLocationTextView.setVisibility(View.GONE);
            holder.mLocationButton.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mGeeftList.size();
    }


}
