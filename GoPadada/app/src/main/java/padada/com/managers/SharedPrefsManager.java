package padada.com.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import padada.com.model.Customer;

public class SharedPrefsManager {

	private static final String KEY_CUSTOMER_ID = "keyCustomerId";

	private SharedPreferences mSharedPreferences;

	public SharedPrefsManager(Context context) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void saveCustomer(Customer customer) {
		mSharedPreferences.edit().putString(KEY_CUSTOMER_ID, customer.getObjectId()).commit();
	}

	public Customer getCustomer() {
		if(mSharedPreferences.contains(KEY_CUSTOMER_ID)) {
			return new Customer(
					mSharedPreferences.getString(KEY_CUSTOMER_ID, null)
			);
		} else {
			return null;
		}
	}

}
