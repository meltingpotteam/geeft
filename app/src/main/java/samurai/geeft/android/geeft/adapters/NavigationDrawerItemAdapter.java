package samurai.geeft.android.geeft.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.LoginActivity;
import samurai.geeft.android.geeft.database.BaaSFillNavigationDrawerCount;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanIntHolder;
import samurai.geeft.android.geeft.models.NavigationDrawerItem;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by ugookeadu on 25/01/16.
 * later navigation drawer adapter class
 */
public class NavigationDrawerItemAdapter extends
        RecyclerView.Adapter<NavigationDrawerItemAdapter.ViewHolder> implements TaskCallbackBooleanIntHolder{

    private static final String TAG = "NaviDrawerItemAdapter";
    private final LayoutInflater inflater;
    private Context mContext;

    //-------------------Macros
    private final int RESULT_OK = 1;
    private final int RESULT_FAILED = 0;
    private final int RESULT_SESSION_EXPIRED = -1;
    //-------------------

    //avoid null pointer exception
    private List<NavigationDrawerItem> mNavigationDrawerItems=
            Collections.emptyList();

    public NavigationDrawerItemAdapter(Context context, List<NavigationDrawerItem> itmes){
        inflater = LayoutInflater.from(context);
        mContext = context;
        this.mNavigationDrawerItems = itmes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public TextView mTitle;
        public TextView mCountIcon;
        public TextView mDescription;
        public int mItemCount;


        public ViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView)itemView.findViewById(R.id.navigation_drawer_row_icon);
            mTitle = (TextView)itemView.findViewById(R.id.navigation_drawer_row_title);
            mCountIcon = (TextView) itemView.findViewById(R.id.navigation_drawer_row_count);
            mDescription = (TextView)itemView.findViewById(R.id.navigation_drawer_row_description);
        }
    }

    @Override
    public NavigationDrawerItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType)  {
        // inflate the custom layout
        View view = inflater.inflate(R.layout.navigation_drawer_item, parent, false);

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

        if(position == 1) {
            new BaaSFillNavigationDrawerCount(mContext,TagsValue.LINK_NAME_DONATED,
                    holder,position,NavigationDrawerItemAdapter.this).execute();
        }
        else if (position == 2){
            new BaaSFillNavigationDrawerCount(mContext,TagsValue.LINK_NAME_RESERVE,
                    holder,position,NavigationDrawerItemAdapter.this).execute();
        }
        else if (position == 3){
            new BaaSFillNavigationDrawerCount(mContext,TagsValue.LINK_NAME_ASSIGNED,
                    holder,position,NavigationDrawerItemAdapter.this).execute();

        }
        else{
            holder.mCountIcon.setVisibility(View.GONE);
        }

    }

    public void done(boolean result,NavigationDrawerItemAdapter.ViewHolder holder,
              int count,int resultToken){
        if(result){
            if(count != 0)
                holder.mCountIcon.setText(count+"");
            else
                holder.mCountIcon.setVisibility(View.GONE);
        }
        else{
            if (resultToken == RESULT_SESSION_EXPIRED) {
                Toast toast = Toast.makeText(mContext, "Sessione scaduta,è necessario effettuare di nuovo" +
                        " il login", Toast.LENGTH_LONG);
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                toast.show();
            } else {
                new AlertDialog.Builder(mContext)
                        .setTitle("Errore")
                        .setMessage("Operazione non possibile. Riprovare più tardi.").show();
            }
        }
    }



    @Override
    public int getItemCount() {
        return mNavigationDrawerItems.size();
    }
}
