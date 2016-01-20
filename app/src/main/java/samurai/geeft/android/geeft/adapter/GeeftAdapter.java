package samurai.geeft.android.geeft.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.data.FeedItem;
import samurai.geeft.android.geeft.data.Geeft;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class GeeftAdapter extends RecyclerView.Adapter<GeeftAdapter.ViewHolder>{
    //list containing the geefts
    private List<Geeft> mGeeftList;
    //card layout id
    private int layoutID;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mGeeftTitleTextView = (TextView) itemView.findViewById(R.id.txtTitle);
            mGeeftDescriptionTextView = (TextView) itemView.findViewById(R.id.txtStatusMsg);
            mUserLocationTextView = (TextView) itemView.findViewById(R.id.location);
            mUsernameTextView = (TextView) itemView.findViewById(R.id.name);
            mTimeStampTextView = (TextView) itemView.findViewById(R.id.timestamp);

            mUserProfilePic = (SimpleDraweeView) itemView.findViewById(R.id.profilePic);
            mGeeftImage = (SimpleDraweeView) itemView.findViewById(R.id.feedImage1);

            mPrenoteButton = (ImageButton) itemView.findViewById(R.id.float_prenotation);
            mLocationButton = (ImageButton) itemView.findViewById(R.id.float_location);
            mShareButton = (ImageButton) itemView.findViewById(R.id.float_share);
        }

        @Override
        public void onClick(View view) {
            // This will execute when the the card is clicked
        }
    }

    //costuctor
    public GeeftAdapter(List<Geeft> geeftList, int layoutID) {
        this.mGeeftList = geeftList;
        this.layoutID = layoutID;
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
        holder.mTimeStampTextView.setText(item.getTimeStamp());
        holder.mUserLocationTextView.setText(item.getUserLocation());

        ImageControllerGenerator.generateSimpleDrawee(holder.mUserProfilePic,
                item.getUserProfilePic());
        ImageControllerGenerator.generateSimpleDrawee(holder.mGeeftImage,
                item.getGeeftImage());


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
    }

    @Override
    public int getItemCount() {
        return mGeeftList.size();
    }
       
    private void setTextVisibility(FeedItem item, TextView mStatus, TextView mTitle){
        item.setIsVisibleDesc(!item.isVisibleDesc());
        if(item.isVisibleDesc()) {
            mStatus.setSingleLine(false);
            mTitle.setSingleLine(false);
        }
        else {
            mStatus.setSingleLine(true);
            mTitle.setSingleLine(true);
        }
    }
}
