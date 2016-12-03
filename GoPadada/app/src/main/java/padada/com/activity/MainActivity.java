package padada.com.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.OnyxBeaconManager;
import com.onyxbeacon.rest.auth.util.AuthData;
import com.onyxbeacon.rest.auth.util.AuthenticationMode;
import com.onyxbeacon.service.logging.LoggingStrategy;

import java.util.ArrayList;
import java.util.List;

import padada.com.R;
import padada.com.fragments.BeaconFragment;
import padada.com.listener.BleStateListener;
import padada.com.receivers.BleStateReceiver;
import padada.com.receivers.ContentReceiver;




public class MainActivity extends AppCompatActivity implements
        BleStateListener, ActivityCompat.OnRequestPermissionsResultCallback {

public static final String EXTRA_COUPONS = "coupons";
    public static final String TAG = "MainActivity";
    private static final int REQUEST_FINE_LOCATION = 0;

    /**
     * Permissions required to read and write contacts. Used by the {@link com.onyxbeacon.service.location.LocationManager}.
     */
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.ACCESS_FINE_LOCATION};
    //Misc
    // This is the project number you got from the API Console
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //private MFPPush push;
    //private MFPPushNotificationListener notificationListener;

    /**
     * Root of the layout of this Activity.
     */
    private View mLayout;
    // OnyxBeacon SDK
    private OnyxBeaconManager mManager;
    private String CONTENT_INTENT_FILTER;
    private String BLE_INTENT_FILTER;
    private ContentReceiver mContentReceiver;
    private BleStateReceiver mBleReceiver;
    private boolean receiverRegistered = false;
    private boolean bleStateRegistered = false;

    /**
     * Called when the 'show camera' button is clicked.
     * Callback is defined in resource layout definition.
     */
    public void enableLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.util.Log.i(TAG, "Checking location permission.");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermission();
            }
        }
        mManager = OnyxBeaconApplication.getOnyxBeaconManager(this);
        mManager.setDebugMode(LoggingStrategy.DEBUG);
        mManager.setAPIEndpoint("https://connect.onyxbeacon.com");
        mManager.setCouponEnabled(true);
        mManager.setAPIContentEnabled(true);
        mManager.enableGeofencing(true);
        mManager.setLocationTrackingEnabled(true);
        AuthData authData = new AuthData();
        authData.setAuthenticationMode(AuthenticationMode.CLIENT_SECRET_BASED);
        authData.setClientId("9d0aea579c3ce646183df80d781e765d3d138261");
        authData.setSecret("81a60d745b82cae212c62aa308d294504a88fdd3");
        mManager.setAuthData(authData);
    }

    /**
     * Requests the Location permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */

    private void requestLocationPermission() {
        // BEGIN_INCLUDE(location_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            Snackbar.make(mLayout, "Location permission are needed to enable location",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_FINE_LOCATION);
                        }
                    })
                    .show();
        } else {

            // Location permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize UI tabs
        initUI();

        //mManager.sendGenericUserProfile(socialProfile);
        // Register for Onyx content
        registerForOnyxContent();

        // Initialize OnyxBeaconManager
        enableLocation();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BeaconFragment(), "Beacons");
        viewPager.setAdapter(adapter);
    }

    public void onResume() {
        super.onResume();

        if (mBleReceiver == null) mBleReceiver = BleStateReceiver.getInstance();

        if (mContentReceiver == null) mContentReceiver = ContentReceiver.getInstance();

        registerReceiver(mContentReceiver, new IntentFilter(CONTENT_INTENT_FILTER));
        receiverRegistered = true;


        if (BluetoothAdapter.getDefaultAdapter() == null) {
            Snackbar.make(mLayout, "Device does not support Bluetooth",
                    Snackbar.LENGTH_SHORT).show();
        } else {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                Snackbar.make(mLayout, "Please turn on bluetooth",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                // Enable scanner in foreground mode and register receiver
                mManager = OnyxBeaconApplication.getOnyxBeaconManager(this);
                mManager.setForegroundMode(true);
            }
        }
    }

    public void onPause() {
        super.onPause();
        // Set scanner in background mode
        mManager.setForegroundMode(false);

        if (bleStateRegistered) {
            unregisterReceiver(mBleReceiver);
            bleStateRegistered = false;
        }

        if (receiverRegistered) {
            unregisterReceiver(mContentReceiver);
            receiverRegistered = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /* Enable bluetooth button */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bluetooth:
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBleStackEvent(int event) {
        System.out.println(event);
        switch (event) {
            case 1:
                Snackbar snackBar = Snackbar.make(mLayout, "Probably your bluetooth stack has crashed. Please restart your bluetooth.", Snackbar.LENGTH_LONG);
                snackBar.setActionTextColor(Color.RED);
                snackBar.show();
                break;
            case 2:
                Snackbar snackBarrssi = Snackbar.make(mLayout, "Beacons with invalid RSSI detected. Please restart your bluetooth.", Snackbar.LENGTH_LONG);
                snackBarrssi.setActionTextColor(Color.GREEN);
                snackBarrssi.show();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // Check if the only required permission has been granted
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        // Location permission has been granted, preview can be displayed
                        android.util.Log.i(TAG, "LOCATION permission has now been granted.");
                        Snackbar.make(mLayout, "Location Permission has been granted.",
                                Snackbar.LENGTH_SHORT).show();
                    } else {
                        android.util.Log.i(TAG, "LOCATION permission was NOT granted.");
                        Snackbar.make(mLayout, "Permissions were not granted",
                                Snackbar.LENGTH_SHORT).show();

                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public void registerForOnyxContent() {

        //Register for BLE events
        mBleReceiver = BleStateReceiver.getInstance();
        mBleReceiver.setBleStateListener(this);


        BLE_INTENT_FILTER = getPackageName() + ".scan";
        registerReceiver(mBleReceiver, new IntentFilter(BLE_INTENT_FILTER));
        bleStateRegistered = true;

        CONTENT_INTENT_FILTER = getPackageName() + ".content";
        registerReceiver(mContentReceiver, new IntentFilter(CONTENT_INTENT_FILTER));
        receiverRegistered = true;

    }

    public void initUI() {
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
