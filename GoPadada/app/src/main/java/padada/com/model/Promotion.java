package padada.com.model;

/**
 * Created by Alex on 04/12/2016.
 */

public class Promotion {

	private String objectId;
	private String title;
	private String description;
	private int points;
	private String photoURL;

	public Promotion(String objectId, String title, String description, int points, String photoURL) {
		this.objectId = objectId;
		this.title = title;
		this.description = description;
		this.points = points;
		this.photoURL = photoURL;
	}

	public String getObjectId() {
		return objectId;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public int getPoints() {
		return points;
	}

	public String getPhotoURL() {
		return photoURL;
	}

}
