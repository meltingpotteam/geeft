package samurai.geeft.android.geeft.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import samurai.geeft.android.geeft.R;
import samurai.geeft.android.geeft.adapters.CategoriesListAdapter;
import samurai.geeft.android.geeft.interfaces.ClickListener;
import samurai.geeft.android.geeft.models.Category;
import samurai.geeft.android.geeft.utilities.RecyclerTouchListener;
import samurai.geeft.android.geeft.utilities.StatedFragment;
import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by ugookeadu on 02/03/16.
 */
public class ListCategoryFragment extends StatedFragment {

    private static final String KEY_LIST_STATE = "key_list_state" ;
    private final String TAG = getClass().getSimpleName();

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private List<Category> mCategoriesList;
    private CategoriesListAdapter mAdapter;
    private Category mCategory;
    private OnCategorySelectedListener mCallback;
    private Parcelable mCategoriesListState;

    public static ListCategoryFragment newInstance() {
        ListCategoryFragment fragment = new ListCategoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCategoriesList = new ArrayList<>();
        initList();
    }

    private void initList() {
        Category[] categories = {
                new Category(TagsValue.ASSETS_CLOTH, TagsValue.CATEGORY_CLOTH),
                new Category(TagsValue.ASSETS_HOUSE, TagsValue.CATEGORY_HOUSE),
                new Category(TagsValue.ASSETS_ELECTRONICS, TagsValue.CATEGORY_ELECTRONICS),
                new Category(TagsValue.ASSETS_FILMS, TagsValue.CATEGORY_FILMS),
                new Category(TagsValue.ASSETS_KIDS, TagsValue.CATEGORY_KIDS),
                new Category(TagsValue.ASSETS_SPORTS, TagsValue.CATEGORY_SPORTS),
                new Category(TagsValue.ASSETS_CARS, TagsValue.CATEGORY_CAR),
                new Category(TagsValue.ASSETS_SERVICES, TagsValue.CATEGORY_SERVICES),
                new Category(TagsValue.ASSETS_OTHER, TagsValue.CATEGORY_OTHER)
        };

        for(Category c : categories){
            mCategoriesList.add(c);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        Log.d(TAG,"savedInstanceState==null? "+(savedInstanceState==null));
        initUI(rootView);
        initSupportActionBar(rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnCategorySelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnCategorySelectedListener");
        }
    }

    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        // Save list state
        mCategoriesListState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(KEY_LIST_STATE, mCategoriesListState);
    }


    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        if (savedInstanceState != null) {
            mCategoriesList = new ArrayList<>();
            mCategoriesListState = savedInstanceState.getParcelable(KEY_LIST_STATE);
            initList();
        }
    }

    public interface OnCategorySelectedListener {
        void onCategorySelected(Category category);
    }

    private void initUI(View rootView) {
        Log.d(TAG,"initUI");
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recyclerview);
        mRecyclerView.setNestedScrollingEnabled(true);
//        mRecyclerView.setHasFixedSize(true);

    }

    private void initSupportActionBar(View rootView) {
        mToolbar = (Toolbar)rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity)getActivity())
                .getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


}
