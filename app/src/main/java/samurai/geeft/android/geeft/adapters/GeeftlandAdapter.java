package samurai.geeft.android.geeft.adapters;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.Collections;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by gabriel-dev on 25/02/16.
 */
public class GeeftlandAdapter extends RecyclerView.Adapter<GeeftlandAdapter.ViewHolder>      {

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

        public TextView mGeeftlandGeeftName;

        public ImageView mGeeftlandGeeftImage;

        public FrameLayout mContainer;

        public Geeft mGeeft;

        private String app_url ="http://geeft.tk"; //Replace with direct link to Geeft in Play Store
        private String mUserId;


        public ViewHolder(View itemView) {
            super(itemView);
            mContainer = (FrameLayout) itemView.findViewById(R.id.container_view);
            mGeeftlandGeeftName = (TextView) itemView.findViewById(R.id.geeftland_geeft_name);
            mGeeftlandGeeftImage = (ImageView) itemView.findViewById(R.id.geeftland_geeft_image);

        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public GeeftlandAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the custom layout
        View mGeeftView = inflater.inflate(R.layout.geeftland_item, parent, false);

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
        holder.mGeeftlandGeeftName.setText(item.getGeeftTitle());
        Picasso.with(mContext).load(item.getGeeftImage()).fit()
                .centerCrop().placeholder(R.drawable.ic_image_multiple).into(holder.mGeeftlandGeeftImage);
//        Log.d("IMAGE", item.getUserProfilePic());

        holder.mGeeftlandGeeftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Apre immagine allargata", Toast.LENGTH_SHORT).show();
            }
        });
        //--------------------------------------------------------------


//        setAnimation(holder.mContainer);
    }

    @Override
    public int getItemCount() {
        return mGeeftList.size();
    }

    /**
     * Here is the key method to apply the animation
     */
//    private void setAnimation(View viewToAnimate)
//    {
//        // If the bound view wasn't previously displayed on screen, it's animated
//        if ((mGeeftList.size()-lastSize)>0)
//        {
//            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
//            animation.setDuration(350);
//            viewToAnimate.startAnimation(animation);
//            lastSize++;
//        }
//    }

    public void done(boolean result){
        // userInformation order is : Feedback,Given,Received
        if(result){
            Log.e(TAG,"CORRETTOOOOO!");
        }
        else{
            Log.e(TAG,"ERROREEEEE!");
        }
    }

}

