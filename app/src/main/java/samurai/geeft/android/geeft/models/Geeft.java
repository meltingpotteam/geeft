package samurai.geeft.android.geeft.models;

import java.io.Serializable;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class Geeft implements Serializable {
    private long creationTime;
    private long deadLine;
    private String userLocation;
    private String userCap;
    private String username;
    private String userFbId;
    private String geeftDescription;
    private String geeftTitle;
    private String geeftImage;
    private String[] geeftArrayImage;
    private boolean automaticSelection;
    private boolean allowCommunication;
    private boolean dimensionRead;
    private int geeftHeight;
    private int geeftWidth;
    private int geeftDepth;

    private String id;
    private boolean isSelected;
    private String userProfilePic;

    private String reservedLinkId;
    private String donatedLinkId;


    private boolean assigned;
    private boolean taken;

    public Geeft(){
    }

    public Geeft(String id, String geeftDescription, String geeftImage,String[] geeftArrayImage, String geeftTitle,
                 boolean isSelected, long creationTime, boolean automaticSelection, boolean allowCommunication, boolean dimensionRead, long deadLine, String userLocation, String userCap,
                 String username,String userFbId, String userProfilePic, String reservedLinkId,String donatedLinkId,int geeftHeight, int geeftWidth,int geeftDepth,boolean assigned,boolean taken) {
        this.geeftDescription = geeftDescription;
        this.geeftImage = geeftImage;
        this.geeftArrayImage = geeftArrayImage;
        this.geeftTitle = geeftTitle;
        this.isSelected = isSelected;
        this.creationTime = creationTime;
        this.automaticSelection = automaticSelection;
        this.allowCommunication = allowCommunication;
        this.dimensionRead = dimensionRead;
        this.userLocation = userLocation;
        this.userCap = userCap;
        this.username = username;
        this.userFbId = userFbId;
        this.userProfilePic = userProfilePic;
        this.id=id;
        this.deadLine = deadLine;
        this.reservedLinkId = reservedLinkId;
        this.donatedLinkId = donatedLinkId;
        this.geeftHeight = geeftHeight;
        this.geeftWidth = geeftWidth;
        this.geeftDepth = geeftDepth;
        this.assigned = assigned;
        this.taken = taken;

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

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isAllowCommunication() {
        return this.allowCommunication;
    }

    public void setAllowCommunication(boolean allowCommunication) {
        this.allowCommunication = allowCommunication;
    }

    public boolean isAutomaticSelection() {
        return this.automaticSelection;
    }
    public void setAutomaticSelection(boolean automaticSelection) {
        this.automaticSelection = automaticSelection;
    }

    public void setDimensionRead(boolean dimensionRead) {
        this.dimensionRead = dimensionRead;
    }
    public boolean isDimensionRead() { return this.dimensionRead;
    }


    public String getUserLocation() {
        return userLocation;
    }


    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getUserCap() {
        return userCap;
    }

    public void setUserCap(String userCap) {
        this.userCap = userCap;
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

    public long getDeadLine(){return deadLine;}

    public void setDeadLine(long deadLine){this.deadLine = deadLine;}

    public String getReservedLinkId(){ return reservedLinkId;}

    public void setReservedLinkId(String reservedLinkId){ this.reservedLinkId = reservedLinkId;}

    public String getDonatedLinkId() {
        return donatedLinkId;
    }

    public void setDonatedLinkId(String donatedLinkId) {
        this.donatedLinkId = donatedLinkId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserFbId() {
        return userFbId;
    }

    public void setUserFbId(String userFbId) {
        this.userFbId = userFbId;
    }

    public int getGeeftHeight() {
        return geeftHeight;
    }

    public void setGeeftHeight(int geeftHeight) {
        this.geeftHeight = geeftHeight;
    }

    public int getGeeftWidth() {
        return geeftWidth;
    }

    public void setGeeftWidth(int geeftWidth) {
        this.geeftWidth = geeftWidth;
    }

    public int getGeeftDepth() {
        return geeftDepth;
    }

    public void setGeeftDepth(int geeftDepth) {
        this.geeftDepth = geeftDepth;
    }

    public String[] getGeeftArrayImage() {
        return geeftArrayImage;
    }

    public void setGeeftArrayImage(String[] geeftArrayImage) {
        this.geeftArrayImage = geeftArrayImage;
    }

    public byte[] getStreamImage() {
        return streamImage;
    }

    public void setStreamImage(byte[] streamImage) {
        this.streamImage = streamImage;
    }

    private byte[] streamImage;

    private String category;

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }


}