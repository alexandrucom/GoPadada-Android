package padada.com.managers;

import android.util.Log;

import com.onyxbeaconservice.IBeacon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import padada.com.model.PadaBeacon;

public class BeaconManager {

	public enum CustomerState {
		NONE,
		IN_STATION,
		ENTERED_VEHICLE,
		LEFT_VEHICLE
	}

	private boolean mIsInStation = false;
	private boolean mIsInVehicle = false;

	private List<PadaBeacon> mBeaconsInProximity;
	private BeaconProximityListener mBeaconProximityListener;

	public BeaconManager(BeaconProximityListener beaconProximityListener) {
		mBeaconsInProximity = new ArrayList<>();
		mBeaconProximityListener = beaconProximityListener;
		startRecuringTask();
	}

	public void processBeacons(List<PadaBeacon> beacons) {
		for (PadaBeacon padaBeacon : beacons) {
			if (isAlreadyInProximity(padaBeacon)) {
				padaBeacon.setLastSeen(System.currentTimeMillis());
			} else {
				mBeaconsInProximity.add(padaBeacon);
			}
		}

		Log.e("bcs", "processBeacons: " + mBeaconsInProximity.size());
	}

	private boolean isAlreadyInProximity(IBeacon iBeacon) {
		for (IBeacon beacon : mBeaconsInProximity) {
			if (beacon.getMinor() == iBeacon.getMinor()) {
				return true;
			}
		}
		return false;
	}

	private boolean isStationInProximity() {
		for (PadaBeacon padaBeacon : mBeaconsInProximity) {
			if (padaBeacon.getType() == PadaBeacon.TYPE_STATION) {
				return true;
			}
		}

		return false;
	}

	private boolean isVehicleInProximity() {
		for (PadaBeacon padaBeacon : mBeaconsInProximity) {
			if (padaBeacon.getType() == PadaBeacon.TYPE_TRANSPORT) {
				return true;
			}
		}

		return false;
	}

	private void startRecuringTask() {
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				for (Iterator<PadaBeacon> iterator = mBeaconsInProximity.iterator(); iterator.hasNext(); ) {
					PadaBeacon padaBeacon = iterator.next();
					if (padaBeacon.getLastSeen() < System.currentTimeMillis() - 3 * 1000) {
						iterator.remove();
					}
				}

				if (!isVehicleInProximity() && isStationInProximity()) {

					if (mIsInVehicle) {
						if (mBeaconProximityListener != null) {
							mBeaconProximityListener.onStateChanged(CustomerState.LEFT_VEHICLE);
						}
					}

				} else if (isVehicleInProximity() && !isStationInProximity()) {

					if (mIsInStation) {
						if (mBeaconProximityListener != null) {
							mBeaconProximityListener.onStateChanged(CustomerState.ENTERED_VEHICLE);
						}
					}
				}

				mIsInStation = isStationInProximity();
				mIsInVehicle = isVehicleInProximity();
			}
		}, 0, 3 * 1000);
	}

	public interface BeaconProximityListener {
		void onStateChanged(CustomerState customerState);
	}

}
