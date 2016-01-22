package samurai.geeft.android.geeft.model;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class Geeft {
    private String timeStamp;
    private String userLocation;
    private String username;
    private String geeftDescription;
    private String geeftTitle;
    private String geeftImage;
    private String id;
    private boolean isSelected;
    private String userProfilePic;

    public Geeft(){};

    public Geeft(String id,String geeftDescription, String geeftImage, String geeftTitle,
                 boolean isSelected, String timeStamp, String userLocation,
                 String username, String userProfilePic) {
        this.geeftDescription = geeftDescription;
        this.geeftImage = geeftImage;
        this.geeftTitle = geeftTitle;
        this.isSelected = isSelected;
        this.timeStamp = timeStamp;
        this.userLocation = userLocation;
        this.username = username;
        this.userProfilePic = userProfilePic;
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGeeftDescription() {
        return geeftDescription;
    }

    public void setGeeftDescription(String geeftDescription) {
        this.geeftDescription = geeftDescription;
    }

    public String getGeeftImage() {
        return geeftImage;
    }

    public void setGeeftImage(String geeftImage) {
        this.geeftImage = geeftImage;
    }

    public String getGeeftTitle() {
        return geeftTitle;
    }

    public void setGeeftTitle(String geeftTitle) {
        this.geeftTitle = geeftTitle;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }
}
