package samurai.geeft.android.geeft.adapter;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import samurai.geeft.android.geeft.util.ImageControllerGenerator;
import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.model.Geeft;

/**
 * Created by ugookeadu on 20/01/16.
 * Cleaned and merged with gabriel-dev version 24/01/16
 */
public class GeeftAdapter extends RecyclerView.Adapter<GeeftAdapter.ViewHolder>{
    //list containing the geefts
    private List<Geeft> mGeeftList;
    //card layout id
    private int layoutID;

    private Context mContext;
    //listen to click eventr

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTimeStampTextView;
        public TextView mUserLocationTextView;
        public TextView mUsernameTextView;
        public TextView mGeeftDescriptionTextView;
        public TextView mGeeftTitleTextView;
        public Context mViewHolderContext;

        public SimpleDraweeView mUserProfilePic;
        public SimpleDraweeView mGeeftImage;

        public ImageButton mGeeftReservationButton;
        public ImageButton mLocationButton;
        public ImageButton mShareButton;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mGeeftTitleTextView = (TextView) itemView.findViewById(R.id.geeft_name);
            mGeeftDescriptionTextView = (TextView) itemView.findViewById(R.id.geeft_description);
            mUserLocationTextView = (TextView) itemView.findViewById(R.id.location);
            mUsernameTextView = (TextView) itemView.findViewById(R.id.geefter_name);
            mTimeStampTextView = (TextView) itemView.findViewById(R.id.timestamp);

            mUserProfilePic = (SimpleDraweeView) itemView.findViewById(R.id.geefter_profile_image);
            mGeeftImage = (SimpleDraweeView) itemView.findViewById(R.id.geeft_image);

            mGeeftReservationButton = (ImageButton) itemView.findViewById(R.id.geeft_like_reservation_button);
            mLocationButton = (ImageButton) itemView.findViewById(R.id.geeft_info_button);
            mShareButton = (ImageButton) itemView.findViewById(R.id.geeft_share_button);



            //Every listened of the card , need to initialize here
            //Image Buttons///////////////
            mGeeftReservationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mViewHolderContext, "Reservation button to set", Toast.LENGTH_SHORT).show();
                    mGeeftReservationButton.setColorFilter(Color.parseColor("#4d00b9"));
                }
            });
            //////////////////////////////

            //Text Dialog/////////////////
            mGeeftTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mViewHolderContext); //Read Update
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //here you can add functions
                        }
                    });

                    //On click, the user visualize can visualize some infos about the geefter
                    AlertDialog dialog = alertDialog.create();
                    dialog.setTitle(mGeeftTitleTextView.getText());
                    dialog.setMessage("Some information that we can take from the facebook shared one");

                    dialog.show();  //<-- See This!
                }
            });
            //////////////////////////////

            //Text Expander///////////////
            mGeeftDescriptionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGeeftDescriptionTextView.setSingleLine(false);
                }
            });
            //////////////////////////////

        }


        @Override
        public void onClick(View v) {

        }
    }

    //costuctor
    public GeeftAdapter(List<Geeft> geeftList, int layoutID, Context context) {
        this.mGeeftList = geeftList;
        this.layoutID = layoutID;
        this.mContext = context;
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
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // - get element of the data model from list at this position
        Geeft item = mGeeftList.get(position);

        // - replace the contents of the view with that element
        viewHolder.mUsernameTextView.setText(item.getUsername());
        viewHolder.mGeeftDescriptionTextView.setText(item.getGeeftDescription());
        viewHolder.mGeeftTitleTextView.setText(item.getGeeftTitle());
        viewHolder.mTimeStampTextView.setText(item.getTimeStamp());
        viewHolder.mUserLocationTextView.setText(item.getUserLocation());
        viewHolder.mViewHolderContext=mContext;

        //Added for click listening/////
        viewHolder.mGeeftDescriptionTextView.setSingleLine(true);
        viewHolder.mGeeftDescriptionTextView.setEllipsize(TextUtils.TruncateAt.END);
        ////////////////////////////////

        ImageControllerGenerator.generateSimpleDrawee(viewHolder.mUserProfilePic,
                item.getUserProfilePic());
        ImageControllerGenerator.generateSimpleDrawee(viewHolder.mGeeftImage,
                item.getGeeftImage());


        // Chcek for empty geeft title
        if (!TextUtils.isEmpty(item.getGeeftTitle()))
            // status is empty, remove from view
            viewHolder.mGeeftTitleTextView.setVisibility(View.GONE);

        // Chcek for empty geeft description
        if (TextUtils.isEmpty(item.getGeeftDescription()))
            // description is empty, remove from view
            viewHolder.mGeeftDescriptionTextView.setVisibility(View.GONE);

        if (TextUtils.isEmpty(item.getUserLocation())) {
            // location is empty, remove from view location txt and button
            viewHolder.mUserLocationTextView.setVisibility(View.GONE);
            viewHolder.mLocationButton.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mGeeftList.size();
    }
}
