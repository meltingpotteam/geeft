package samurai.geeft.android.geeft.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import samurai.geeft.android.geeft.interfaces.ClickListener;

/**
 * Created by ugookeadu on 26/01/16.
 * class that work as listener for recyclerviews touch event
 */
public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{
    private GestureDetector mGestureDetector;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ClickListener mClickListener;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView,
                                 final ClickListener clickListener){
        this.mContext = context;
        this.mRecyclerView= recyclerView;
        this.mClickListener = clickListener;
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        View child = recyclerView.findChildViewUnder(e.getX(),e.getY());

                        if(child!=null && clickListener!=null)
                            clickListener.onLongClick(child,
                                    recyclerView.getChildLayoutPosition(child));
                    }
                });

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(),e.getY());
        if(child!=null && mClickListener!=null && mGestureDetector.onTouchEvent(e))
            mClickListener.onClick(child,rv.getChildLayoutPosition(child));
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}

