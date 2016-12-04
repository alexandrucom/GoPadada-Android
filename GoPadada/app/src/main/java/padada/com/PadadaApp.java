package padada.com;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.parse.Parse;

/**
 * Created by Alex on 04/12/2016.
 */

public class PadadaApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		MultiDex.install(this);

		Parse.initialize(
				this,
				getResources().getString(R.string.parse_application_id),
				getResources().getString(R.string.parse_client_id)
		);

		FacebookSdk.sdkInitialize(getApplicationContext());
	}
}
