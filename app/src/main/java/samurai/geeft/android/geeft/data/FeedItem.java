package samurai.geeft.android.geeft.data;

public class FeedItem {
	private String id,name, status, image, profilePic, timeStamp, url, location,title;
	private boolean   isVisibleDesc;
	private boolean isVisibleLocation;
	private boolean isSelectedItem;
	public FeedItem() {
	}


	public FeedItem(String id, String name, String image, String status,
					String profilePic, String timeStamp, String url, String location, String title) {
		super();
		this.id = id;
		this.name = name;
		this.image = image;
		this.status = status;
		this.profilePic = profilePic;
		this.timeStamp = timeStamp;
		this.url = url;
		this.location = location;
		this.title = title;
		this.isSelectedItem = false;
		this.isVisibleDesc = false;
		this.isVisibleLocation = false;
	}

	public boolean isSelectedItem() {
		return isSelectedItem;
	}

	public void setIsSelectedItem(boolean isSelectedItem) {
		this.isSelectedItem = isSelectedItem;
	}

	public boolean isVisibleDesc() {
		return isVisibleDesc;
	}

	public void setIsVisibleDesc(boolean isVisibleDesc) {
		this.isVisibleDesc = isVisibleDesc;
	}

	public boolean isVisibleLocation() {
		return isVisibleLocation;
	}

	public void setIsVisibleLocation(boolean isVisibleLocation) {
		this.isVisibleLocation = isVisibleLocation;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImge() {
		return image;
	}

	public void setImge(String image) {
		this.image = image;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}
