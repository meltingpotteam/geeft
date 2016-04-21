package samurai.geeft.android.geeft.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.Category;

/**
 * Created by ugookeadu on 02/03/16.
 */
public class CategoriesListAdapter extends RecyclerView.Adapter<CategoriesListAdapter.ViewHolder>      {

    private final LayoutInflater inflater;
    private final static String TAG ="GeeftlandAdapter";

    //list containing the geefts and avoiding null pointer exception
    private List<Category> mCategoriesList =
            Collections.emptyList();

    private int lastSize = 0;
    private Context mContext;

    private ProgressDialog mProgress;
    private long mLastClickTime = 0;

    //costructor
    public CategoriesListAdapter(Context context, List<Category> categoriesList) {
        inflater = LayoutInflater.from(context);
        mCategoriesList =categoriesList;
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mCategoryName;

        public ImageView mCategoryImage;

        public FrameLayout mContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            mContainer = (FrameLayout) itemView.findViewById(R.id.container_view);
            mCategoryName = (TextView) itemView.findViewById(R.id.category_name);
            mCategoryImage = (ImageView) itemView.findViewById(R.id.category_image);

        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public CategoriesListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the custom layout
        View mGeeftView = inflater.inflate(R.layout.item_category_card, parent, false);

        /** set the view's size, margins, paddings and layout parameters
         *
         */

        //Inflate a new view hierarchy from the specified xml resource.
        return new ViewHolder(mGeeftView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element of the data model from list at this position
        final Category item = mCategoriesList.get(position);

        // - replace the contents of the view with that element
        holder.mCategoryName.setText(item.getCategoryName());
        Glide.with(mContext).load(item.getImageUrl()).centerCrop()
                .placeholder(R.drawable.ic_image_multiple)
                .fitCenter()
                .into(holder.mCategoryImage);
//        Log.d("IMAGE", item.getUserProfilePic());

        holder.mCategoryName.setOnClickListener(new View.OnClickListener() {
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
        return mCategoriesList.size();
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
//2
}