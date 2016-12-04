package padada.com.managers;

import android.content.Context;

import com.parse.ParseException;
import com.parse.ParseInstallation;

import padada.com.dal.ApiResult;
import padada.com.dal.PadadaApiClient;
import padada.com.model.Customer;
import retrofit.Callback;

public class AccountManager {

	private SharedPrefsManager mSharedPrefsManager;
	private PadadaApiClient mPadadaApiClient;

	public AccountManager(Context context) {
		mSharedPrefsManager = new SharedPrefsManager(context);
		mPadadaApiClient = new PadadaApiClient(context);
	}

	public Customer getCustomer() {
		return mSharedPrefsManager.getCustomer();
	}

	public void registerCustomer(Callback<ApiResult<Customer>> callback) throws ParseException {

		ParseInstallation.getCurrentInstallation().save();

		mPadadaApiClient.getApiService().register(
				ParseInstallation.getCurrentInstallation().getInstallationId(),
				callback
		);
	}

}
