package samurai.geeft.android.geeft.models;

import android.util.Log;

import com.baasbox.android.BaasLink;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import samurai.geeft.android.geeft.utilities.TagsValue;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class Geeft implements Serializable {
    private final String TAG = getClass().getSimpleName() ;
    private long creationTime;
    private long deadLine;
    private String userLocation;
    private String userCap;
    private String fullname;
    private String username;
    private String baasboxUsername;
    private String userFbId;
    private String geeftDescription;
    private String geeftTitle;
    private String geeftImage;
    private String[] geeftArrayImage;
    private String geeftLabels;
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
    private boolean given;
    private boolean isFeedbackLeftByGeefted;
    private boolean isFeedbackLeftByGeefter;


    public Geeft(){
    }

    public Geeft(String id, String geeftDescription, String geeftImage,String[] geeftArrayImage, String geeftTitle,
                 boolean isSelected, long creationTime, String geeftLabels, boolean automaticSelection, boolean allowCommunication, boolean dimensionRead, long deadLine, String userLocation, String userCap,
                 String fullname,String baasboxUsername,String username,String userFbId, String userProfilePic, String reservedLinkId,String donatedLinkId,int geeftHeight, int geeftWidth,int geeftDepth, boolean assigned,boolean taken,boolean given,boolean isFeedbackLeftByGeefted, boolean isFeedbackLeftByGeefter) {

        this.geeftDescription = geeftDescription;
        this.geeftImage = geeftImage;
        this.geeftArrayImage = geeftArrayImage;
        this.geeftTitle = geeftTitle;
        this.isSelected = isSelected;
        this.creationTime = creationTime;
        this.geeftLabels = geeftLabels;
        this.automaticSelection = automaticSelection;
        this.allowCommunication = allowCommunication;
        this.dimensionRead = dimensionRead;
        this.userLocation = userLocation;
        this.userCap = userCap;
        this.fullname = fullname;
        this.username = username;
        this.baasboxUsername = baasboxUsername;
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
        this.given = given;
        this.isFeedbackLeftByGeefted = isFeedbackLeftByGeefted;
        this.isFeedbackLeftByGeefter = isFeedbackLeftByGeefter;

    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }


    public String getBaasboxUsername() {
        return baasboxUsername;
    }

    public void setBaasboxUsername(String baasboxUsername) {
        this.baasboxUsername = baasboxUsername;
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

    public String getGeeftLabels() {
        return geeftLabels;
    }
    public void setGeeftArrayLabels(String geeftLabels) {
        this.geeftLabels = geeftLabels;
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


    public boolean isGiven() {
        return given;
    }

    public void setGiven(boolean given) {
        this.given = given;
    }

    public boolean isFeedbackLeftByGeefted() {
        return isFeedbackLeftByGeefted;
    }

    public void setIsFeedbackLeftByGeefted(boolean isFeedbackLeftByGeefted) {
        this.isFeedbackLeftByGeefted = isFeedbackLeftByGeefted;
    }

    public boolean isFeedbackLeftByGeefter() {
        return isFeedbackLeftByGeefter;
    }

    public void setIsFeedbackLeftByGeefter(boolean isFeedbackLeftByGeefter) {
        this.isFeedbackLeftByGeefter = isFeedbackLeftByGeefter;
    }

    public void fillGeeft(JsonObject doc, List<BaasLink> links) {
        this.setId(doc.getString("id"));
        this.setUsername(doc.getString("username"));
        this.setBaasboxUsername(doc.getString("baasboxUsername"));
        this.setGeeftImage(doc.getString("image") + BaasUser.current().getToken());
        //Append ad image url your session token!
        this.setGeeftDescription(doc.getString("description"));
        this.setUserProfilePic(doc.getString("profilePic"));
        this.setCreationTime(getCreationTimestamp(doc));
        this.setUserFbId(doc.getString("userFbId"));

        this.setUserLocation(doc.getString("location"));
        this.setUserCap(doc.getString("cap"));
        this.setGeeftTitle(doc.getString("title"));
        this.setDonatedLinkId(doc.getString("donatedLinkId"));

        if(doc.getBoolean("automaticSelection")!=null) {
            this.setDimensionRead(doc.getBoolean("allowDimension"));
            this.setAssigned(doc.getBoolean("assigned"));
            this.setTaken(doc.getBoolean("taken"));
            this.setGiven(doc.getBoolean("given"));
            this.setIsFeedbackLeftByGeefted(doc.getBoolean(TagsValue.FLAG_IS_FEEDBACK_LEFT_BY_GEEFTED));
            this.setIsFeedbackLeftByGeefter(doc.getBoolean(TagsValue.FLAG_IS_FEEDBACK_LEFT_BY_GEEFTER));

            this.setAutomaticSelection(doc.getBoolean("automaticSelection"));
            this.setAllowCommunication(doc.getBoolean("allowCommunication"));
            if (doc.getLong("deadline") != null) {
                this.setDeadLine(doc.getLong("deadline"));
            }
            if (doc.getInt("height") != null) {
                this.setGeeftHeight(doc.getInt("height"));
            }
            if (doc.getInt("width") != null) {
                this.setGeeftWidth(doc.getInt("width"));
            }
            if (doc.getInt("depth") != null) {
                this.setGeeftDepth(doc.getInt("depth"));
            }
        }
        for (BaasLink l : links) {
            //Log.d(TAG,"out: " + l.out().getId() + " in: " + l.in().getId());
            //Log.d(TAG, "e id: " + doc.getString("id") + " inId: " + l.in().getId());
            //if(l.out().getId().equals(e.getId())){ //TODO: LOGIC IS THIS,but BaasLink.create have a bug
            if (l.in().getId().equals(doc.getString("id"))) {
                this.setIsSelected(true);// set prenoteButton selected (I'm already
                // reserved)
                this.setReservedLinkId(l.getId());
                //Log.d(TAG, "link id is: " + l.getId());
            }
        }

    }

    private long getCreationTimestamp(JsonObject doc){ //return timestamp of _creation_date of document
        String date = doc.getString("_creation_date");
        //Log.doc(TAG,"_creation_date is:" + date);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
            Date creation_date = dateFormat.parse(date);
            return creation_date.getTime(); //Convert timestamp in string
        }catch (java.text.ParseException e){
            Log.e(TAG, "ERRORE FATALE : " + e.toString());
        }
        return -1;

    }
}