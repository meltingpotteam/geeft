package samurai.geeft.android.geeft.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonArray;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.MainActivity;
import samurai.geeft.android.geeft.database.BaaSRetrieveDoc;
import samurai.geeft.android.geeft.models.Geeft;
import samurai.geeft.android.geeft.utilities.ImageControllerGenerator;

/**
 * Created by ugookeadu on 20/01/16.
 * adaptef for GeeftListFragment Recyclerview
 */
public class GeeftItemAdapter extends RecyclerView.Adapter<GeeftItemAdapter.ViewHolder>{

    private final LayoutInflater inflater;

    private final static String TAG ="GeeftAdapter";

    //list containing the geefts and avoiding null pointer exception
    private List<Geeft> mGeeftList =
            Collections.emptyList();

    private Context mContext;


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
            mGeeftTitleTextView = (TextView) itemView.findViewById(R.id.geeft_name);
            mGeeftDescriptionTextView = (TextView) itemView.findViewById(R.id.geeft_description);
            mUserLocationTextView = (TextView) itemView.findViewById(R.id.location);
            mUsernameTextView = (TextView) itemView.findViewById(R.id.geefter_name);
            mTimeStampTextView = (TextView) itemView.findViewById(R.id.timestamp);

            mUserProfilePic = (SimpleDraweeView) itemView.findViewById(R.id.geefter_profile_image);
            mGeeftImage = (SimpleDraweeView) itemView.findViewById(R.id.geeft_image);

            mPrenoteButton = (ImageButton) itemView.findViewById(R.id.geeft_like_reservation_button);
            mPrenoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element of the data model from list at this position
        Geeft item = mGeeftList.get(position);

        // - replace the contents of the view with that element
        holder.mUsernameTextView.setText(item.getUsername());
        holder.mGeeftDescriptionTextView.setText(item.getGeeftDescription());
        holder.mGeeftTitleTextView.setText(item.getGeeftTitle());
        //holder.mTimeStampTextView.setText(item.getTimeStamp());
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
        //TODO WAITING FOR LISTENER IMPLEMENTATION , ISSUE #10
        holder.mPrenoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    /*JsonArray value = Json.createArrayBuilder()
                            .add(Json.createObjectBuilder()
                                    .add("type", "home")
                                    .add("number", "212 555-1234"))
                            .add(Json.createObjectBuilder()
                                    .add("type", "fax")
                                    .add("number", "646 555-4567"))
                            .build();*/
                    JSONObject obj = new JSONObject();
                    obj.put("name", "foo");
                    obj.put("surname","ciao");
                    String jsonText =  obj.toString();
                    Log.d(TAG,jsonText);
                }
                catch(org.json.JSONException e){
                    Log.d(TAG,"error: " + e);
                }
            }
        });
           /* holder.mPrenoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isSelected){
                        String docId = BaasUser.current().getScope(BaasUser.Scope.PRIVATE).getString("id");
                        Log.d(TAG,"Doc id of user is: " + docId + " and item id is: " + mGeeft.getId());
                        new BaaSRetrieveDoc(context,docId,mGeeft,GeeftAdapter.this).execute();

                    }
                }
            });*/

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
                                "Non ha fornito indirizzo", Toast.LENGTH_LONG);
                }catch (java.io.UnsupportedEncodingException e){
                    Toast.makeText(mContext, "Non ha fornito indirizzo", Toast.LENGTH_LONG);
                }
            }
        });
        //----
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
    }

    @Override
    public int getItemCount() {
        return mGeeftList.size();
    }
}
