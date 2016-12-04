package padada.com.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.OnyxBeaconManager;
import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeacon.listeners.OnyxTagsListener;
import com.onyxbeacon.rest.model.content.Tag;
import com.onyxbeaconservice.Beacon;
import com.onyxbeaconservice.Eddystone;
import com.onyxbeaconservice.IBeacon;

import java.util.ArrayList;


public class ContentReceiver extends BroadcastReceiver {

	private OnyxBeaconsListener mOnyxBeaconListener;
	private OnyxTagsListener mOnyxTagsListener;
	private static ContentReceiver sInstance;
	private OnyxBeaconManager mManager;

	public ContentReceiver() {}

	public static ContentReceiver getInstance() {
		if (sInstance == null) {
			sInstance = new ContentReceiver();
			return sInstance;
		} else {
			return sInstance;
		}
	}

	public void setOnyxBeaconsListener(OnyxBeaconsListener onyxBeaconListener) {
		mOnyxBeaconListener = onyxBeaconListener;
	}

	public void onReceive(Context context, Intent intent) {
		String payloadType = intent.getStringExtra(OnyxBeaconApplication.PAYLOAD_TYPE);

		switch (payloadType) {
			case OnyxBeaconApplication.TAG_TYPE:
				ArrayList<Tag> tagsList = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_TAGS);
				if (mOnyxTagsListener != null) {
					mOnyxTagsListener.onTagsReceived(tagsList);
				}
				break;
			case OnyxBeaconApplication.BEACON_TYPE:
				ArrayList<Beacon> beacons = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_BEACONS);
				for (Beacon beacon : beacons) {
					if (beacon instanceof IBeacon) {
						IBeacon iBeacon = (IBeacon) beacon;
						mManager = OnyxBeaconApplication.getOnyxBeaconManager(context);
						android.util.Log.d("BeaconRec", "IBeacon toString " + iBeacon.toString());

					} else if (beacon instanceof Eddystone) {
						Eddystone eddystone = (Eddystone) beacon;
						android.util.Log.d("BeaconRec", "Eddystone toString " + eddystone.toString());
					}
				}
				if (mOnyxBeaconListener != null) {
					mOnyxBeaconListener.didRangeBeaconsInRegion(beacons);
				}
				break;
			case OnyxBeaconApplication.WEB_REQUEST_TYPE:
				String extraInfo = intent.getStringExtra(OnyxBeaconApplication.EXTRA_INFO);
				System.out.println("AUTH Web reguest info " + extraInfo);
				if (extraInfo.equals(OnyxBeaconApplication.REQUEST_UNAUTHORIZED)) {
					// Pin based session expired
				}
				break;
		}
	}
}
