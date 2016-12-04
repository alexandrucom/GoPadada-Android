package padada.com.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeaconservice.Beacon;

import java.util.List;

import padada.com.receivers.ContentReceiver;


public class BeaconFragment extends Fragment implements OnyxBeaconsListener {

    // UI Elements
    private ListView mListView;
    private Activity mFragmentActivity;
    public BeaconAdapter mBeaconAdapter;
    private ContentReceiver mContentReceiver;
    private RideHandler mRideHandler;

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

        // Initialize content receiver. The SDK was initialized in MainActivity
        if (mContentReceiver == null) mContentReceiver = ContentReceiver.getInstance();
        mContentReceiver.setOnyxBeaconsListener(this);
        mRideHandler = new RideHandler(getContext());
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

    @Override
    public void didRangeBeaconsInRegion(final List<Beacon> beacons) {
        mRideHandler.rideHandler(beacons);
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
