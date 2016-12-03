package gopadada.com;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.OnyxBeaconManager;
import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeacon.rest.auth.util.AuthData;
import com.onyxbeacon.rest.auth.util.AuthenticationMode;
import com.onyxbeacon.service.logging.LoggingStrategy;
import com.onyxbeaconservice.Beacon;

import java.util.List;

import gopadada.com.receivers.ContentReceiver;

public class MainActivity extends AppCompatActivity implements OnyxBeaconsListener {
    // OnyxBeacon SDK
    private OnyxBeaconManager mManager;
    private ContentReceiver mContentReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableLocation();

        if (mContentReceiver == null) mContentReceiver = ContentReceiver.getInstance();
        mContentReceiver.setOnyxBeaconsListener(this);
    }

    public void enableLocation() {
        mManager = OnyxBeaconApplication.getOnyxBeaconManager(this);
        mManager.setDebugMode(LoggingStrategy.DEBUG);
        mManager.setAPIEndpoint("https://connect.onyxbeacon.com");
        mManager.setCouponEnabled(true);
        mManager.setAPIContentEnabled(true);
        mManager.enableGeofencing(true);
        mManager.setLocationTrackingEnabled(true);
        AuthData authData = new AuthData();
        authData.setAuthenticationMode(AuthenticationMode.CLIENT_SECRET_BASED);
        authData.setClientId("44250bab7ce4b8466681ee186f911bcb46e6164c");
        authData.setSecret("70dd88b78f6e57f90714a7ee391060945ef36b7a");
        mManager.setAuthData(authData);
    }

    @Override
    public void didRangeBeaconsInRegion(List<Beacon> list) {
        Log.d(TAG, "didRangeBeaconsInRegion: ");
    }

    private static final String TAG = "MainActivity";
}
