package samurai.geeft.android.geeft.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.net.URLEncoder;
import java.util.List;

import samurai.geeft.android.geeft.FeedImageView;
import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.app.AppController;
import samurai.geeft.android.geeft.data.FeedItem;

/**
 * Created by ugookeadu on 07/01/16.
 */
public class FeedListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item, null);
        final View cView = convertView;
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        final FeedItem item = feedItems.get(position);
        final TextView titleMsg = (TextView) convertView.findViewById(R.id.txtTitle);
        final TextView statusMsg = (TextView) convertView.findViewById(R.id.txtStatusMsg);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        final TextView location = (TextView) convertView.findViewById(R.id.location);
        NetworkImageView profilePic = (NetworkImageView) convertView
                .findViewById(R.id.profilePic);
        FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.feedImage1);
        final ImageButton prenote = (ImageButton) convertView.findViewById(R.id.float_prenotation);
        final ImageButton locationBtn = (ImageButton) convertView.findViewById(R.id.float_location);
        final ImageButton shareBtn = (ImageButton) convertView.findViewById(R.id.float_share);

        Rect r = new Rect();

        titleMsg.setSingleLine(!item.isVisibleLocation());
        titleMsg.setEllipsize(TextUtils.TruncateAt.END);

        statusMsg.setSingleLine(!item.isVisibleDesc());
        statusMsg.setEllipsize(TextUtils.TruncateAt.END);

        location.setSingleLine(!item.isVisibleLocation());
        location.setEllipsize(TextUtils.TruncateAt.END);

        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);

        extendTouch(prenote, 4, r);
        extendTouch(locationBtn, 4, r);
        extendTouch(shareBtn, 4, r);


        locationBtn.getHitRect(r);
        r.top -= 4;
        r.bottom += 4;
        r.left -=4;
        r.right +=4;
        locationBtn.setTouchDelegate(new TouchDelegate(r, locationBtn));

        shareBtn.getHitRect(r);
        r.top -= 4;
        r.bottom += 4;
        r.left -=4;
        r.right +=4;
        shareBtn.setTouchDelegate(new TouchDelegate(r, shareBtn));
        name.setText(item.getName());

        // Converting timestamp into x ago format
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(item.getTimeStamp()),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timestamp.setText(timeAgo);


        if (!TextUtils.isEmpty(item.getTitle())) {
            titleMsg.setText(item.getTitle());
            titleMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            titleMsg.setVisibility(View.GONE);
        }

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            statusMsg.setText(item.getStatus());
            statusMsg.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(item.getLocation())) {
            location.setText(item.getLocation());
            location.setVisibility(View.VISIBLE);
            locationBtn.setVisibility(View.VISIBLE);
        } else {
            // status is empty, remove from view
            location.setVisibility(View.GONE);
            locationBtn.setVisibility(View.GONE);
        }


        // Checking for null feed url
        if (item.getUrl() != null) {
            url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
                    + item.getUrl() + "</a> "));

            // Making url clickable
            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            url.setVisibility(View.GONE);
        }

        // user profile pic
        profilePic.setImageUrl(item.getProfilePic(), imageLoader);

        // Feed image
        if (item.getImge() != null) {
            feedImageView.setImageUrl(item.getImge(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            feedImageView.setVisibility(View.GONE);
        }

        statusMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               setTextVisibility(item,statusMsg, titleMsg );
            }
        });

        titleMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextVisibility(item,statusMsg, titleMsg);
            }
        });

        prenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               setPrenoteIsChecked(item, prenote);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocationVisibility(item,location);
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String position = location.getText().toString();
                    if( !position.equals("")) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" +
                                URLEncoder.encode(position, "UTF-8"));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        cView.getContext().startActivity(mapIntent);
                    }
                    else
                        Toast.makeText(cView.getContext(),
                                "Non ha fornito indirizzo",Toast.LENGTH_LONG);
                }catch (java.io.UnsupportedEncodingException e){
                    Toast.makeText(cView.getContext(), "Non ha fornito indirizzo",Toast.LENGTH_LONG);
                }
            }
        });
        return convertView;
    }

    private void setTextVisibility(FeedItem item, TextView statusMsg, TextView titleMsg){
        item.setIsVisibleDesc(!item.isVisibleDesc());
        if(item.isVisibleDesc()) {
            statusMsg.setSingleLine(false);
            titleMsg.setSingleLine(false);
        }
        else {
            statusMsg.setSingleLine(true);
            titleMsg.setSingleLine(true);
        }
    }

    private void setPrenoteIsChecked(FeedItem item, ImageButton prenote){
        item.setIsSelectedItem(!item.isSelectedItem());
        if(item.isSelectedItem())
            prenote.setBackgroundResource(R.drawable.check_on);
        else
            prenote.setBackgroundResource(R.drawable.check_off);
    }

    private void setLocationVisibility(FeedItem item,TextView location){
        item.setIsVisibleLocation(!item.isVisibleLocation());
        if(item.isVisibleLocation())
            location.setSingleLine(false);
        else
            location.setSingleLine(true);
    }

    private void extendTouch(ImageButton btn, int size, Rect r){
        btn.getHitRect(r);
        r.top -= size;
        r.bottom += size;
        r.left -= size;
        r.right += size;
        btn.setTouchDelegate(new TouchDelegate(r, btn));
    }
}