package samurai.geeft.android.geeft.utilities;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;

import samurai.geeft.android.geeft.R;

/**
 * Created by ugookeadu on 20/02/16.
 */
public class ScrollingFABBehavior extends  CoordinatorLayout.Behavior<FloatingActionMenu> {
    private final String TAG = getClass().getSimpleName();

    private int toolbarHeight;
    private int fabBottomMargin;
    private int distanceToScroll;
    private float ratio;

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.toolbarHeight = getToolbarHeight(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionMenu fab, View dependency) {
        return true;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionMenu fab, View dependency) {
        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            fabBottomMargin = lp.bottomMargin;
            distanceToScroll = fab.getHeight() + fabBottomMargin;
            ratio = dependency.getY()/(float)toolbarHeight;
            fab.setTranslationY(-distanceToScroll * ratio/5);
        }
        if (dependency instanceof Snackbar.SnackbarLayout){
            float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
            fab.setTranslationY(translationY);
        }
        return true;
    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }
}
