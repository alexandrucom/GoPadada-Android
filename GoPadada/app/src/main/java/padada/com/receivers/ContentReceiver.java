package padada.com.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.OnyxBeaconManager;
import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeacon.listeners.OnyxCouponsListener;
import com.onyxbeacon.listeners.OnyxPushListener;
import com.onyxbeacon.listeners.OnyxTagsListener;
import com.onyxbeacon.rest.model.account.BluemixApp;
import com.onyxbeacon.rest.model.content.Coupon;
import com.onyxbeacon.rest.model.content.Tag;
import com.onyxbeaconservice.Beacon;
import com.onyxbeaconservice.Eddystone;
import com.onyxbeaconservice.IBeacon;

import java.util.ArrayList;
import java.util.List;

import padada.com.activity.MainActivity;
import padada.com.R;


public class ContentReceiver extends BroadcastReceiver {

    private OnyxBeaconsListener mOnyxBeaconListener;
    private OnyxCouponsListener mOnyxCouponsListener;
    private OnyxTagsListener mOnyxTagsListener;
    private OnyxPushListener mOnyxPushListener;
    private static ContentReceiver sInstance;
    private OnyxBeaconManager mManager;

    /* Coupons */
    private static String COUPONS_TAG = "coupons_tag";
    private SharedPreferences mSharedPref;
    private Gson gson = new Gson();
    private static final String COUPONS_LIST_ENTRY = "couponsList";
    private static final String COUPONS_NEW_COUNTER = "couponsNewCounter";
    private static final String SHARED_PREF_NO_ENTRY = "noEntry";

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
        String payloadType = intent.getStringExtra(OnyxBeaconApplication.PAYLOAD_TYPE);

        switch (payloadType) {
            case OnyxBeaconApplication.TAG_TYPE:
                ArrayList<Tag> tagsList = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_TAGS);
                if (mOnyxTagsListener != null) {
                    mOnyxTagsListener.onTagsReceived(tagsList);
                } else {
                    // In background display notification
                }
                break;
            case OnyxBeaconApplication.BEACON_TYPE:
                ArrayList<Beacon> beacons = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_BEACONS);
                for (Beacon beacon : beacons) {
                    if (beacon instanceof IBeacon) {
                        IBeacon iBeacon = (IBeacon) beacon;
                        mManager = OnyxBeaconApplication.getOnyxBeaconManager(context);
                        android.util.Log.d("BeaconRec", "IBeacon toString " + iBeacon.toString());
//                        if(iBeacon.getMinor() == 22222) {
//                            SettingsFragment.setiBeacon(iBeacon);
//                        }
                    } else if (beacon instanceof Eddystone) {
                        Eddystone eddystone = (Eddystone) beacon;
                        android.util.Log.d("BeaconRec", "Eddystone toString " + eddystone.toString());
                    }
                }
                if (mOnyxBeaconListener != null) {
                    mOnyxBeaconListener.didRangeBeaconsInRegion(beacons);
                } else {
                    // In background display notification
                }
                break;
            case OnyxBeaconApplication.COUPON_TYPE:

                mSharedPref = context.getSharedPreferences("COUPONS_PREF",
                        Context.MODE_PRIVATE);
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Coupon coupon = intent.getParcelableExtra(OnyxBeaconApplication.EXTRA_COUPON);
                IBeacon beacon = intent.getParcelableExtra(OnyxBeaconApplication.EXTRA_BEACON);

                System.out.println("Coupon receiver - Received coupon " + gson.toJson(coupon));
                android.util.Log.d("ExpTrig", "Coupon exp date is " + coupon.expires);
                OnyxBeaconManager manager = OnyxBeaconApplication.getOnyxBeaconManager(context);
//                if(beacon.getMajor() == 556) {
//                    manager.buzz(beacon, new byte[]{1, 0, 1, 1, 0, 0, 1, 1});
//                }

                if (coupon != null) {
                    String couponsListAsString = mSharedPref.getString(COUPONS_LIST_ENTRY, SHARED_PREF_NO_ENTRY);
                    ArrayList<Coupon> couponsFromStorage = new ArrayList<Coupon>();
                    ArrayList<Coupon> newCoupons = new ArrayList<Coupon>();
                    if (!couponsListAsString.equals(SHARED_PREF_NO_ENTRY)) {
                        couponsFromStorage = (ArrayList<Coupon>)gson.fromJson(couponsListAsString, new TypeToken<List<Coupon>>() {
                        }.getType());
                    }

                    if (!couponsFromStorage.contains(coupon)) {
                        couponsFromStorage.add(coupon);
                    }

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra(MainActivity.EXTRA_COUPONS, (Parcelable) coupon);
                    stackBuilder.addNextIntent(i);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    long[] vibratePattern = {500, 500, 500, 500};
                    Notification.Builder builder =
                            new Notification.Builder(context)
                                        .setContentTitle(coupon.message)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setAutoCancel(true)
                                        .setVibrate(vibratePattern)
                                        .setLights(Color.BLACK, 500, 500)
                                        .setSound(notificationSound);

                    builder.setContentIntent(resultPendingIntent);
                    notificationManager.notify(COUPONS_TAG, 1, builder.build());


                    if (mOnyxCouponsListener != null) {
                        mOnyxCouponsListener.onCouponReceived(coupon, beacon);
                    } else {
                        SharedPreferences.Editor editor = mSharedPref.edit();
                        editor.putString(COUPONS_LIST_ENTRY, gson.toJson(couponsFromStorage));
                        editor.apply();
                    }
                }
                break;
            case OnyxBeaconApplication.PUSH_TYPE:
                BluemixApp bluemixApp = intent.getParcelableExtra(OnyxBeaconApplication.EXTRA_BLUEMIX);
                System.out.println("PUSH Received bluemix credentials " + gson.toJson(bluemixApp));
                if (mOnyxPushListener != null) {
                    mOnyxPushListener.onBluemixCredentialsReceived(bluemixApp);
                }
                break;
            case OnyxBeaconApplication.COUPONS_DELIVERED_TYPE:
                ArrayList<Coupon> deliveredCoupons = intent.getParcelableArrayListExtra(OnyxBeaconApplication.EXTRA_COUPONS);
                android.util.Log.d("Receiver", "No of delivered coupons " + deliveredCoupons.size());
                if (mOnyxCouponsListener != null) {
                    mOnyxCouponsListener.onDeliveredCouponsReceived(deliveredCoupons);
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
