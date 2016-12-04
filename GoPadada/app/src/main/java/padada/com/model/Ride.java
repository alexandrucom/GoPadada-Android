package padada.com.model;

public class Ride {

	private String objectId;
	private String vehicleName;
	private String vehicleType;
	private String routeName;
	private long startedAt;
	private long endedAt;

	public Ride(String objectId, String vehicleName, String vehicleType, String routeName, long startedAt, long endedAt) {
		this.objectId = objectId;
		this.vehicleName = vehicleName;
		this.vehicleType = vehicleType;
		this.routeName = routeName;
		this.startedAt = startedAt;
		this.endedAt = endedAt;
	}

	public String getObjectId() {
		return objectId;
	}

	public String getVehicleName() {
		return vehicleName;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public String getRouteName() {
		return routeName;
	}

	public long getStartedAt() {
		return startedAt;
	}

	public long getEndedAt() {
		return endedAt;
	}
}
