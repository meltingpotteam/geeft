package samurai.geeft.android.geeft.models;

import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by ugookeadu on 05/03/16.
 */
public class User implements Serializable{
    private final String TAG = getClass().getSimpleName();
    private String mUserID;
    private String mFbID;
    private String mUsername;
    private String mEmail;
    private String mDocId;
    private double mRank;
    private String mDescription;
    private int mLinkGivenCount;
    private int mLinkReceivedCount;

    public User(@Nullable String userID){
        mUserID = userID;
    }


    public User(@Nullable String userID, String username, String email, String fbID, String description,
                double rank){
        mUserID = userID;
        mUsername = username;
        mEmail = email;
        mFbID = fbID;
        mDescription = description;
        mRank = rank;
    }


    public String getProfilePic() {
        Log.d(TAG, mFbID);
        return "https://graph.facebook.com/"+mFbID+"/picture?type=large";
    }

    public void setProfilePic(String fbID) {
        mFbID = fbID;
    }

    public String getID(){
        return mUserID;
    }

    public void setUserID(String userID) {
        mUserID = userID;
    }

    public String getFbID(){
        return mFbID;
    }

    public void setFbID(String fbID) {
        mFbID = fbID;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getEmail() { return mEmail; }

    public void setEmail(String email) { mEmail = email; }

    public String getDocId() {
        return mDocId;
    }

    public void setDocId(String docId) {
        mDocId = docId;
    }

    public double getRank() {
        return mRank;
    }

    public void setRank(double rank) {
        mRank = rank;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setLinkGivenCount(int linkGivenCount) {
        mLinkGivenCount = linkGivenCount;
    }

    public void setLinkReceivedCount(int linkReceivedCount) {
        mLinkReceivedCount = linkReceivedCount;
    }

    public int getLinkReceivedCount() {
        return mLinkReceivedCount;
    }

    public int getLinkGivenCount() {
        return mLinkGivenCount;
    }
}
