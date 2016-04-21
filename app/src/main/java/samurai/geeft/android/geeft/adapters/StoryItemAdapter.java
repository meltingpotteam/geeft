package samurai.geeft.android.geeft.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nvanbenschoten.motion.ParallaxImageView;

import java.util.Collections;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.activities.FullScreenViewActivity;
import samurai.geeft.android.geeft.activities.LoginActivity;
import samurai.geeft.android.geeft.interfaces.TaskCallbackBooleanArrayToken;
import samurai.geeft.android.geeft.interfaces.TaskCallbackStoryItem;
import samurai.geeft.android.geeft.models.Geeft;

/**
 * Created by ugookeadu on 17/02/16.
 */
public class StoryItemAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        TaskCallbackStoryItem,TaskCallbackBooleanArrayToken {
    private final LayoutInflater inflater;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final String WEBSITE_URL = "http://geeft.tk/";
    private final static String TAG ="GeeftAdapter";

    //list containing the geefts and avoiding null pointer exception
    private List<Geeft> mGeeftList =
            Collections.emptyList();

    private int lastSize = 0;
    private Context mContext;

    private ProgressDialog mProgress;

    //info dialog attributes---------------------
    private TextView mProfileDialogUsername;
    private TextView mProfileDialogUserLocation;
    private ImageView mProfileDialogUserImage;
    private TextView mProfileDialogUserRank;
    private TextView mProfileDialogUserGiven;
    private TextView mProfileDialogUserReceived;
    private ParallaxImageView mProfileDialogBackground;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private long mLastClickTime;
    private int mLastSize;

    //costructor
    public StoryItemAdapter(Context context, List<Geeft> myDataSet, RecyclerView recyclerView) {
        inflater = LayoutInflater.from(context);
        this.mGeeftList = myDataSet;
        this.mContext = context;
        mLastSize = 0;
        mLastClickTime=0;
        mGeeftList = myDataSet;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = 
                    (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public void done(boolean result, StoryItemAdapter.ViewHolder myHolder, double[] userInformation) {

    }

    @Override
    public void done(boolean result, StoryItemAdapter.ViewHolder myHolder, Geeft item) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        //public TextView mTimeStampTextView;
        //public TextView mUserLocationTextView;
        //public TextView mUsernameTextView;
        //public TextView mGeeftDescriptionTextView;
        public TextView mGeeftTitleTextView;
        
        //public ImageView mUserProfilePic;
        public ImageView mGeeftImage;

        //public LinearLayout mProfileClickableArea;
        public LinearLayout mSignalisationButton;

        //-------------------------------------------
        public CardView mContainer;

        //public Button mMoreButton;

        public Geeft mGeeft;
        private String app_url ="http://geeft.tk"; //Replace with direct link to Geeft in Play Store

        public ViewHolder(View itemView) {
            super(itemView);
            mContainer = (CardView) itemView.findViewById(R.id.card_view);
            mGeeftTitleTextView = (TextView) itemView.findViewById(R.id.geeft_name);
            //mGeeftDescriptionTextView = (TextView) itemView.findViewById(R.id.geeft_description);
            //mUserLocationTextView = (TextView) itemView.findViewById(R.id.location);
            //mUsernameTextView = (TextView) itemView.findViewById(R.id.geefter_name);
            //mTimeStampTextView = (TextView) itemView.findViewById(R.id.timestamp);
            mSignalisationButton = (LinearLayout) itemView.findViewById(R.id.geeft_signalisation);
            //mProfileClickableArea = (LinearLayout) itemView.findViewById(R.id.geefter_info_area);

            //mUserProfilePic = (ImageView) itemView.findViewById(R.id.geefter_profile_image);
            mGeeftImage = (ImageView) itemView.findViewById(R.id.geeft_image);
            //mMoreButton = (Button)itemView.findViewById(R.id.see_more_button);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mGeeftList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.story_list_item, parent, false);

            vh = new StoryItemAdapter.ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ProgressViewHolder){
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }else {
            final StoryItemAdapter.ViewHolder myHolder = ((StoryItemAdapter.ViewHolder) holder);
            // - get element of the data model from list at this position
            final Geeft item = mGeeftList.get(position);

            if (item != null) {
                myHolder.mGeeftTitleTextView.setText(item.getGeeftTitle());
                //myHolder.mExpireTime.setText(item.getCreationTime()); //TODO: GESTIRE

                //TODO add the control of the cap matching in the city selected; sand in the maps tracking
                Glide.with(mContext).load(item.getGeeftImage()).fitCenter()
                        .centerCrop().into(myHolder.mGeeftImage);


                // Converting timestamp into x ago format
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(item.getCreationTime(),
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);

                //myHolder.mTimeStampTextView.setText(timeAgo);


                //Signalization button Implementation--------------
                myHolder.mSignalisationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO implement the behaviour of the signalization button
                        Toast.makeText(v.getContext(), "Segnalazione completata con successo",
                                Toast.LENGTH_LONG).show();
                    }
                });
                //-------------------------------------------------
                myHolder.mGeeftImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // launch full screen activity
                        Intent intent = FullScreenViewActivity.newIntent(mContext,
                                item.getId(), "story");
                        mContext.startActivity(intent);
                    }
                });
            }
        }
            
    }

    @Override
    public int getItemCount() {
        return mGeeftList.size();
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if ((mGeeftList.size()-lastSize)>0)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            animation.setDuration(350);
            viewToAnimate.startAnimation(animation);
            lastSize++;
        }
    }


    public void done(boolean result,double[] userInformation,int resultToken){
        //-------------------Macros
        final int RESULT_OK = 1;
        final int RESULT_FAILED = 0;
        final int RESULT_SESSION_EXPIRED = -1;
        //-------------------
        // userInformation order is : Feedback,Given,Received
        if(result){
            mProfileDialogUserRank.setText(String.valueOf(userInformation[0]) + "/5.0");
            mProfileDialogUserGiven.setText(String.valueOf((int)userInformation[1]));
            mProfileDialogUserReceived.setText(String.valueOf((int)userInformation[2]));

            //Log.d(TAG, "Ritornato AsyncTask con: " + userInformation[0] + "," + userInformation[1]
            //       + "," + userInformation[2]);

        }else {
            Toast toast;
            if (resultToken == RESULT_OK) {
                toast = Toast.makeText(mContext, "Nessuna nuova storia", Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.show();
            } else if (resultToken == RESULT_SESSION_EXPIRED) {
                toast = Toast.makeText(mContext, "Sessione scaduta,è necessario effettuare di nuovo" +
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

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }

}
