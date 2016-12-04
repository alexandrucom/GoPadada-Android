package padada.com.model;

import com.onyxbeaconservice.IBeacon;

/**
 * Created by Alex on 04/12/2016.
 */

public class PadaBeacon extends IBeacon {

	public static final int TYPE_TRANSPORT = 64875;
	public static final int TYPE_STATION = 30242;

	private long lastSeen;

	public PadaBeacon(IBeacon otherIBeacon, long lastSeen) {
		super(otherIBeacon);
		this.lastSeen = lastSeen;
	}

	public long getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(long lastSeen) {
		this.lastSeen = lastSeen;
	}

	public int getType() {
		if(getMinor() == TYPE_TRANSPORT) {
			return TYPE_TRANSPORT;
		} else {
			return TYPE_STATION;
		}
	}

}
