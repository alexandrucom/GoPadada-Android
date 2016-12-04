package padada.com.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeacon.model.mapper.BeaconMapper;
import com.onyxbeaconservice.Beacon;
import com.onyxbeaconservice.IBeacon;

import java.util.ArrayList;
import java.util.List;

import padada.com.managers.BeaconManager;
import padada.com.model.PadaBeacon;


public class ContentReceiver extends BroadcastReceiver {

	private BeaconManager mBeaconManager;
	private static ContentReceiver sInstance;

	public ContentReceiver() {
	}

	public static ContentReceiver getInstance() {
		if (sInstance == null) {
			sInstance = new ContentReceiver();
			return sInstance;
		} else {
			return sInstance;
		}
	}

	public void setBeaconProximityListener(BeaconManager.BeaconProximityListener beaconProximityListener) {
		mBeaconManager = new BeaconManager(beaconProximityListener);
	}

	public void onReceive(Context context, Intent intent) {
		String payloadType = intent.getStringExtra(OnyxBeaconApplication.PAYLOAD_TYPE);

		List<PadaBeacon> eligibleBeacons = new ArrayList<>();

		if (payloadType.equals(OnyxBeaconApplication.BEACON_TYPE)) {
			ArrayList<Beacon> beacons = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_BEACONS);
			for (Beacon beacon : beacons) {
				if (beacon instanceof IBeacon && beacon.getProximity() < Beacon.PROXIMITY_FAR) {
					PadaBeacon padaBeacon = new PadaBeacon((IBeacon) beacon, System.currentTimeMillis());
					eligibleBeacons.add(padaBeacon);
				}
			}
			if (mBeaconManager != null) {
				mBeaconManager.processBeacons(eligibleBeacons);
			}
		}

	}

}
