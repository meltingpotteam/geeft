package samurai.geeft.android.geeft.models;

import android.support.annotation.Nullable;

import java.io.Serializable;

/**
 * Created by ugookeadu on 05/03/16.
 */
public class User implements Serializable{
    private final String TAG = getClass().getSimpleName();
    private String mEmail;
    private String mUserID;
    private String mFbID;
    private String mUsername;
    private String mDocId;
    private double mRank;
    private String mDescription;
    private int mLinkGivenCount;
    private int mLinkReceivedCount;
    private String mProfilePic;

    public User(@Nullable String userID){
        mUserID = userID;
    }


    public User(@Nullable String userID, String username, String profilePic, String description
            ,String email, double rank){
        mUserID = userID;
        mUsername = username;
        mEmail = email;
        mProfilePic = profilePic;
        mDescription = description;
        mRank = rank;
    }


    public String getProfilePic() {
        if(mProfilePic==null){
            return "";
        }
        return mProfilePic;
    }

    public void setProfilePic(String profilePic) {
        mProfilePic = profilePic;
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
