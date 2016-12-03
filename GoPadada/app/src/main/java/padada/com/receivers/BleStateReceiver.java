package padada.com.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.onyxbeacon.OnyxBeaconApplication;
import padada.com.listener.BleStateListener;


public class BleStateReceiver extends BroadcastReceiver {

    private static BleStateReceiver sInstance;
    private BleStateListener bleStateListener;

    public BleStateReceiver() {}

    public static BleStateReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new BleStateReceiver();
            return sInstance;
        } else {
            return sInstance;
        }
    }

    public BleStateListener getBleStateListener() {
        return bleStateListener;
    }

    public void setBleStateListener(BleStateListener bleStateListener) {
        this.bleStateListener = bleStateListener;
    }

    /**
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String payloadType = intent.getStringExtra(OnyxBeaconApplication.SCAN_EVENT);
        switch (payloadType) {
            case OnyxBeaconApplication.EMPTY_SCANS:
                break;
            case OnyxBeaconApplication.INVALID_RSSI:
                break;
        }
    }
}
