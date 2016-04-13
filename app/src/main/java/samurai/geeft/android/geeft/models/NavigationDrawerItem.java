package samurai.geeft.android.geeft.models;

/**
 * Created by ugookeadu on 25/01/16.
 */
public class NavigationDrawerItem {
    private int mTitle, mDetails, mCount, mPicId;

    public NavigationDrawerItem(){
    }

    public NavigationDrawerItem(int title, int details, int picId){
        this.mTitle = title;
        this.mDetails = details;
        this.mPicId = picId;
    }

    public NavigationDrawerItem(int title, int details, int count, int picId){
        this.mTitle = title;
        this.mDetails = details;
        this.mCount = count;
        this.mPicId = picId;
    }

    public int getDetails() {
        return mDetails;
    }

    public void setDetails(int details) {
        mDetails = details;
    }

    public int getCount(){
        return mCount;
    }

    public void setCount(int count){
        mCount = count;
    }

    public int getPicId() {
        return mPicId;
    }

    public void setPicId(int picId) {
        mPicId = picId;
    }

    public int getTitle() {
        return mTitle;
    }

    public void setTitle(int title) {
        mTitle = title;
    }

}
