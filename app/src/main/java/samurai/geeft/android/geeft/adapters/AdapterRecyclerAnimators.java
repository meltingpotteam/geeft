package samurai.geeft.android.geeft.adapters;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import samurai.geeft.android.geeft.R;

/**
 * Created by ugookeadu on 15/03/16.
 */
public class AdapterRecyclerAnimators extends RecyclerView.Adapter<AdapterRecyclerAnimators.Holder> {
    private ArrayList<String> mListData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private long mLastClickTime;


    public AdapterRecyclerAnimators(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = mLayoutInflater.inflate(R.layout.custom_row_item_animations, parent, false);
        Holder holder = new Holder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        String data = mListData.get(position);
        holder.textDataItem.setText(data);
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(position);
            }
        });
    }

    public void addItem(String item) {
        mListData.add(item);
        notifyItemInserted(mListData.size());
    }

    public void removeItem(String item) {
        int position = mListData.indexOf(item);
        if (position != -1) {
            mListData.remove(item);
            notifyItemRemoved(position);
        }
    }

    public void removeItem(int position) {
        mListData.remove(position);
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView textDataItem;
        ImageButton buttonDelete;

        public Holder(View itemView) {
            super(itemView);
            textDataItem = (TextView) itemView.findViewById(R.id.text_item);
            buttonDelete = (ImageButton) itemView.findViewById(R.id.button_delete);

        }
    }

}
