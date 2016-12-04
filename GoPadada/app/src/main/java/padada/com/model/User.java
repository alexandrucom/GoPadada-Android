package padada.com.model;

/**
 * Created by Alex on 04/12/2016.
 */

public class User {

	private String objectId;
	private String username;
	private String profilePhotoUrl;
	private int points;
	private int level;

	public User(String objectId, String username, String profilePhotoUrl, int points, int level) {
		this.objectId = objectId;
		this.username = username;
		this.profilePhotoUrl = profilePhotoUrl;
		this.points = points;
		this.level = level;
	}

	public String getObjectId() {
		return objectId;
	}

	public String getUsername() {
		return username;
	}

	public String getProfilePhotoUrl() {
		return profilePhotoUrl;
	}

	public int getPoints() {
		return points;
	}

	public int getLevel() {
		return level;
	}
}
