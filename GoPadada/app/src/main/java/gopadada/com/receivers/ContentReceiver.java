package gopadada.com.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeacon.listeners.OnyxCouponsListener;
import com.onyxbeacon.listeners.OnyxPushListener;
import com.onyxbeacon.listeners.OnyxTagsListener;

import java.util.ArrayList;

/**
 * Created by Vlad on 03.12.2016.
 */

public class ContentReceiver extends BroadcastReceiver {

    private OnyxBeaconsListener mOnyxBeaconListener;
    private OnyxCouponsListener mOnyxCouponsListener;
    private OnyxTagsListener mOnyxTagsListener;
    private OnyxPushListener mOnyxPushListener;

    private static ContentReceiver sInstance;


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

    public void setOnyxCouponsListener(OnyxCouponsListener onyxCouponsListener) {
        mOnyxCouponsListener = onyxCouponsListener;
    }

    public void setOnyxTagsListener(OnyxTagsListener onyxTagsListener){
        mOnyxTagsListener = onyxTagsListener;
    }

    public void setOnyxPushListener(OnyxPushListener onyxPushListener) {
        mOnyxPushListener = onyxPushListener;
    }

    private static final String TAG = "ContentReceiver";
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");

        String payloadType = intent.getStringExtra(OnyxBeaconApplication.PAYLOAD_TYPE);

        switch (payloadType) {
            case OnyxBeaconApplication.TAG_TYPE:
                ArrayList tagsList = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_TAGS);
                if (mOnyxTagsListener != null) {
                    mOnyxTagsListener.onTagsReceived(tagsList);
                } else {
                    // In background display notification
                }
                break;
            case OnyxBeaconApplication.BEACON_TYPE:
                ArrayList beacons = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_BEACONS);
                if (mOnyxBeaconListener != null) {
                    mOnyxBeaconListener.didRangeBeaconsInRegion(beacons);
                } else {
                    // In background display notification
                }
                break;
        }
    }
}
