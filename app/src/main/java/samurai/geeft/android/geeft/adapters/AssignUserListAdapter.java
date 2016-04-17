package samurai.geeft.android.geeft.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.models.User;
import samurai.geeft.android.geeft.utilities.RoundedTransformation;

/**
 * Created by ugookeadu on 07/03/16.
 */
public class AssignUserListAdapter extends RecyclerView.Adapter<AssignUserListAdapter.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private List<User> mUserList;
    private int itemsCount = 0;
    private int lastAnimatedPosition = -1;
    private int avatarSize;
    private LayoutInflater inflater;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    public AssignUserListAdapter(Context context, List<User> UserList) {
        inflater = LayoutInflater.from(context);
        this.mContext = context;
        mUserList = UserList;
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.comment_avatar_size);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mUserPicImageView;
        public TextView mUsernameTextView;
        public TextView mUserDescriptionTextView;

        public ViewHolder(View view) {
            super(view);

            mUserPicImageView = (ImageView)view.findViewById(R.id.item_assign_user_pic);
            mUsernameTextView = (TextView)view.findViewById(R.id.item_assign_username);
            mUserDescriptionTextView = (TextView)view
                    .findViewById(R.id.item_assign_user_description);
        }
    }

    @Override
    public AssignUserListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "ON CREATE");
        View view = inflater.inflate(R.layout.item_assign_list_user
                , parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "ON BIND");
        runEnterAnimation(holder.itemView, position);
        User user = mUserList.get(position);
        Log.d(TAG, "Profile pic= " + user.getProfilePic());

        Uri profilePic = Uri.parse(user.getProfilePic());
        Picasso.with(mContext)
                .load(profilePic)
                .centerCrop()
                .resize(avatarSize, avatarSize)
                .transform(new RoundedTransformation())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_account_circle_black_24dp)
                .into(holder.mUserPicImageView);

        holder.mUsernameTextView.setText(user.getUsername());
        holder.mUserDescriptionTextView.setText(user.getDescription());

    }


    private void runEnterAnimation(View view, int position) {
        if (animationsLocked) return;

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(100);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    })
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void updateItems() {
        itemsCount = 10;
        notifyDataSetChanged();
    }

    public void addItem() {
        itemsCount++;
        notifyItemInserted(itemsCount - 1);
    }

    public void setAnimationsLocked(boolean animationsLocked) {
        this.animationsLocked = animationsLocked;
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        this.delayEnterAnimation = delayEnterAnimation;
    }
}
