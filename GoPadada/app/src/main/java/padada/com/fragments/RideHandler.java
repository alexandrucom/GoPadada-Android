package padada.com.fragments;

import android.content.Context;
import android.util.Log;

import com.onyxbeaconservice.Beacon;
import com.onyxbeaconservice.IBeacon;

import java.util.ArrayList;
import java.util.List;

import padada.com.R;
import padada.com.dal.ApiResult;
import padada.com.managers.RideManager;
import padada.com.model.Ride;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Vlad on 04.12.2016.
 */

public class RideHandler {
    private static final int TRANSPORT = 64875;
    private static final int STATION = 30242;
    private int countTransport;
    private int countStation;
    private long tStart = 0;
    private List<IBeacon> beaconList;
    private boolean enterBus;
    private RideManager mRideManager;
    private Context mContext;

    public RideHandler(Context context) {
        beaconList = new ArrayList<>();
        mRideManager = new RideManager(context);
        mContext = context;
    }

    public void rideHandler(List<Beacon> beacons) {
        if (beacons.get(0) instanceof IBeacon) {
            if (beacons.size() >= 2) {
                beaconList.add(((IBeacon) beacons.get(0)));
                if (beacons.get(1) instanceof IBeacon) {
                    beaconList.add(((IBeacon) beacons.get(1)));
                }
            } else {
                beaconList.add(((IBeacon) beacons.get(0)));
            }

            if (beaconList.size() > 7) {
                for (IBeacon beacon : beaconList) {
                    if (TRANSPORT == beacon.getMinor()) {
                        countTransport += 1;
                    }
                    if (STATION == beacon.getMinor()) {
                        countStation += 1;
                    }
                }

                Log.d(TAG, "countTransport: " + countTransport);
                Log.d(TAG, "countStation: " + countStation);
                if (countStation == 0) {
                    mRideManager.startRide(new Callback<ApiResult<Ride>>() {
                        @Override
                        public void success(ApiResult<Ride> rideApiResult, Response response) {
                            Log.d(TAG, "success: enter" + rideApiResult.getResult().getStartedAt());
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d(TAG, "failure: enter");
                        }
                    });
                    Log.d(TAG, "enter bus: ");
                    RideNotification.rideNotification(mContext, mContext.getString(R.string.enter_bus));
                    enterBus = true;
                    tStart = System.currentTimeMillis();
                }
                if (enterBus && countStation > 0) {
                    enterBus =false;
                    Log.d(TAG, "exit bus: ");
                    RideNotification.rideNotification(mContext, mContext.getString(R.string.exit_bus));
                    mRideManager.endRide(new Callback<ApiResult<Ride>>() {
                        @Override
                        public void success(ApiResult<Ride> rideApiResult, Response response) {
                            Log.d(TAG, "success: exit " + rideApiResult.getResult().getEndedAt());
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d(TAG, "failure: exit");
                        }
                    });
                }
                if (countTransport > 6) {
                    int seconds = (int) ((tStart / 1000) % 60);
                    Log.d(TAG, "time: " + seconds);
                    tStart = 0;
                }

                beaconList = new ArrayList<>();
                countTransport = 0;
                countStation = 0;
            }
        }
    }
    private static final String TAG = "RideHandler";
}
