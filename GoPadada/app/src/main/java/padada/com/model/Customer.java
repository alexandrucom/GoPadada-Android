package padada.com.model;

/**
 * Created by Alex on 04/12/2016.
 */

public class Customer {

	private String objectId;
	private int level;
	private int points;

	public Customer(String objectId, int level, int points) {
		this.objectId = objectId;
		this.level = level;
		this.points = points;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
}
