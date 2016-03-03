package samurai.geeft.android.geeft.models;

import java.io.Serializable;

/**
 * Created by ugookeadu on 02/03/16.
 */
public class Category implements Serializable{
    private String mImageUrl;
    private String mCategoryName;

    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(String categoryName) {
        mCategoryName = categoryName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public Category(String imageUrl, String categoryName){
        mImageUrl = imageUrl;
        mCategoryName = categoryName;
    }


}
