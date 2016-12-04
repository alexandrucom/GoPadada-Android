package padada.com.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.onyxbeacon.OnyxBeaconApplication;
import com.onyxbeacon.OnyxBeaconManager;
import com.onyxbeacon.listeners.OnyxBeaconsListener;
import com.onyxbeacon.rest.auth.util.AuthData;
import com.onyxbeacon.rest.auth.util.AuthenticationMode;
import com.onyxbeacon.service.logging.LoggingStrategy;
import com.onyxbeaconservice.Beacon;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import padada.com.R;
import padada.com.dal.ApiResult;
import padada.com.fragments.HistoryFragment;
import padada.com.fragments.LeaderboardsFragment;
import padada.com.fragments.MainFragment;
import padada.com.fragments.RideHandler;
import padada.com.fragments.RideNotification;
import padada.com.managers.AccountManager;
import padada.com.managers.SharedPrefsManager;
import padada.com.model.Customer;
import padada.com.receivers.ContentReceiver;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, OnyxBeaconsListener {

	public static final String TAG = "MainActivity";
	private static final int REQUEST_FINE_LOCATION = 0;

	private TabLayout tabLayout;
	private ViewPager viewPager;
	private View mLayout;

	private OnyxBeaconManager mManager;
	private String CONTENT_INTENT_FILTER;
	private ContentReceiver mContentReceiver;
	private RideHandler mRideHandler;
	private boolean receiverRegistered = false;

	private AccountManager mAccountManager;
	private SharedPrefsManager mSharedPrefsManager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAccountManager = new AccountManager(this);
		mSharedPrefsManager = new SharedPrefsManager(this);

		if (mAccountManager.getCustomer() == null) {
			try {
				mAccountManager.registerCustomer(new Callback<ApiResult<Customer>>() {
					@Override
					public void success(ApiResult<Customer> apiResult, Response response) {
						Log.i(TAG, "success: " + apiResult.getResult().getObjectId());
						mSharedPrefsManager.saveCustomer(apiResult.getResult());
					}

					@Override
					public void failure(RetrofitError error) {
						error.printStackTrace();
					}
				});
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		initUI();
		registerForOnyxContent();
		enableLocation();

		// Initialize content receiver. The SDK was initialized in MainActivity
		if (mContentReceiver == null) mContentReceiver = ContentReceiver.getInstance();
		mContentReceiver.setOnyxBeaconsListener(this);
		mRideHandler = new RideHandler(this);
		
		if (RideNotification.ACTION_PUSH.equals(getIntent().getAction())) {
			Intent intent = new Intent(this, PaimentActivity.class);
			startActivity(intent);
		}
	}

	public void onResume() {
		super.onResume();
		if (mContentReceiver == null) {
			mContentReceiver = ContentReceiver.getInstance();
		}

		registerReceiver(mContentReceiver, new IntentFilter(CONTENT_INTENT_FILTER));
		receiverRegistered = true;

		if (BluetoothAdapter.getDefaultAdapter() == null) {
			Snackbar.make(mLayout, "Device does not support Bluetooth", Snackbar.LENGTH_SHORT).show();
		} else {
			if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
				Snackbar.make(mLayout, "Please turn on bluetooth", Snackbar.LENGTH_SHORT).show();
			} else {
				mManager = OnyxBeaconApplication.getOnyxBeaconManager(this);
				mManager.setForegroundMode(true);
			}
		}
	}

	public void enableLocation() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			android.util.Log.i(TAG, "Checking location permission.");
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

	private void requestLocationPermission() {
		if (ActivityCompat.shouldShowRequestPermissionRationale(this,
				Manifest.permission.ACCESS_FINE_LOCATION)) {

			Snackbar.make(mLayout, "Location permission are needed to enable location",
					Snackbar.LENGTH_INDEFINITE)
					.setAction("OK", new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
						}
					})
					.show();
		} else {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
		}
	}

	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
		adapter.addFragment(new HistoryFragment(), "Rides");
		adapter.addFragment(new MainFragment(), "Profile");
		adapter.addFragment(new LeaderboardsFragment(), "Leaderboard");
		viewPager.setAdapter(adapter);
	}

	public void onPause() {
		super.onPause();
		mManager.setForegroundMode(false);

		if (receiverRegistered) {
			unregisterReceiver(mContentReceiver);
			receiverRegistered = false;
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

		switch (requestCode) {
			case REQUEST_FINE_LOCATION: {
				for (int i = 0; i < permissions.length; i++) {
					if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
						android.util.Log.i(TAG, "LOCATION permission has now been granted.");
						Snackbar.make(mLayout, "Location Permission has been granted.", Snackbar.LENGTH_SHORT).show();
					} else {
						android.util.Log.i(TAG, "LOCATION permission was NOT granted.");
						Snackbar.make(mLayout, "Permissions were not granted", Snackbar.LENGTH_SHORT).show();

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
		CONTENT_INTENT_FILTER = getPackageName() + ".content";
		registerReceiver(mContentReceiver, new IntentFilter(CONTENT_INTENT_FILTER));
		receiverRegistered = true;
	}

	public void initUI() {
		setContentView(R.layout.activity_main);
		mLayout = findViewById(R.id.activity_main);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setOffscreenPageLimit(1);
		setupViewPager(viewPager);

		tabLayout = (TabLayout) findViewById(R.id.tabs);
		tabLayout.setupWithViewPager(viewPager);
		viewPager.setCurrentItem(1);
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

	@Override
	public void didRangeBeaconsInRegion(final List<Beacon> beacons) {
		mRideHandler.rideHandler(beacons);
	}

	public void displayLeveUp(int newLevel) {

		new MaterialDialog.Builder(this)
				.title("Level up!")
				.content("Congrats! You leveled up, claim your prize!")
				.positiveText("Claim")
				.icon(getResources().getDrawable(R.drawable.medal))
				.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		viewPager.postDelayed(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						displayLeveUp(2);
					}
				});
			}
		}, 2000);
	}

}
