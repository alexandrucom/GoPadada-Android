package padada.com.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeaconservice.Beacon;
import com.onyxbeaconservice.IBeacon;

import java.util.ArrayList;
import java.util.List;

import padada.com.dal.ApiResult;
import padada.com.managers.RideManager;
import padada.com.model.Ride;
import padada.com.receivers.ContentReceiver;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class BeaconFragment extends Fragment implements OnyxBeaconsListener {
    private static final int TRANSPORT = 64875;
    private static final int STATION = 30242;
    private int countTransport;
    private int countStation;
    // UI Elements
    private ListView mListView;
    private Activity mFragmentActivity;
    public BeaconAdapter mBeaconAdapter;
    private ContentReceiver mContentReceiver;
    private long tStart = 0;
    private long tEnd = 0;
    private List<IBeacon> beaconList;
    private boolean enterBus;
    private RideManager mRideManager;

    public BeaconFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize beacons list
        mFragmentActivity = this.getActivity();
        mListView = new ListView(mFragmentActivity);
        mBeaconAdapter = new BeaconAdapter(mFragmentActivity);
        mListView.setAdapter(mBeaconAdapter);
        return mListView;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        beaconList = new ArrayList<>();
        // Initialize content receiver. The SDK was initialized in MainActivity
        if (mContentReceiver == null) mContentReceiver = ContentReceiver.getInstance();
        mContentReceiver.setOnyxBeaconsListener(this);
        mRideManager = new RideManager(getContext());
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private static final String TAG = "BeaconFragment";

    @Override
    public void didRangeBeaconsInRegion(final List<Beacon> beacons) {
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
                    enterBus = true;
                    tStart = System.currentTimeMillis();
                }
                if (enterBus && countStation > 0) {
                    enterBus =false;
                    Log.d(TAG, "exit bus: ");
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
        Activity currentActivity = getActivity();
        if (currentActivity != null) {
            currentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBeaconAdapter.setBeaconList(beacons);
                }
            });
        }
    }
}
