package samurai.geeft.android.geeft.adapter;

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
import samurai.geeft.android.geeft.model.NavigationDrawerItem;

/**
 * Created by ugookeadu on 25/01/16.
 */
public class NavigationDrawerItemAdapter extends
        RecyclerView.Adapter<NavigationDrawerItemAdapter.ViewHolder> {

    private final LayoutInflater inflater;
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
    public NavigationDrawerItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.navigation_drawer_row, parent,false);

        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NavigationDrawerItemAdapter.ViewHolder holder, int position) {
        NavigationDrawerItem navigationDrawerItem =
                mNavigationDrawerItems.get(position);
        holder.mIcon.setImageResource(navigationDrawerItem.getPicId());
        holder.mTitle.setText(navigationDrawerItem.getTitle());
        holder.mDescription.setText(navigationDrawerItem.getDetails());
    }


    @Override
    public int getItemCount() {
        return mNavigationDrawerItems.size();
    }
}
