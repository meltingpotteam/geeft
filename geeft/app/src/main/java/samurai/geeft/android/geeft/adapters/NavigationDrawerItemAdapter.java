package samurai.geeft.android.geeft.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.NavigationDrawerItem;

/**
 * Created by ugookeadu on 25/01/16.
 * later navigation drawer adapter class
 */
public class NavigationDrawerItemAdapter extends
        RecyclerView.Adapter<NavigationDrawerItemAdapter.ViewHolder> {

    private final LayoutInflater inflater;

    //avoid null pointer exception
    private List<NavigationDrawerItem> mNavigationDrawerItems=
            Collections.emptyList();

    public NavigationDrawerItemAdapter(Context context, List<NavigationDrawerItem> itmes){
        inflater = LayoutInflater.from(context);
        this.mNavigationDrawerItems = itmes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public TextView mTitle;
        public TextView mDescription;
        public ViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView)itemView.findViewById(R.id.navigation_drawer_row_icon);
            mTitle = (TextView)itemView.findViewById(R.id.navigation_drawer_row_title);
            mDescription = (TextView)itemView.findViewById(R.id.navigation_drawer_row_description);
        }
    }

    @Override
    public NavigationDrawerItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        // inflate the custom layout
        View view = inflater.inflate(R.layout.navigation_drawer_item, parent,false);

        /** set the view's size, margins, paddings and layout parameters
         *
         */

        //Inflate a new view hierarchy from the specified xml resource.
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NavigationDrawerItemAdapter.ViewHolder holder, int position) {
        // - get element of the data model from list at this position
        NavigationDrawerItem navigationDrawerItem =
                mNavigationDrawerItems.get(position);

        // - replace the contents of the view with that element
        holder.mIcon.setImageResource(navigationDrawerItem.getPicId());
        holder.mTitle.setText(navigationDrawerItem.getTitle());
        holder.mDescription.setText(navigationDrawerItem.getDetails());
    }


    @Override
    public int getItemCount() {
        return mNavigationDrawerItems.size();
    }
}
