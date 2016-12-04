package padada.com.managers;

import android.content.Context;

import java.util.List;

import padada.com.dal.ApiResult;
import padada.com.dal.PadadaApiClient;
import padada.com.model.Ride;
import retrofit.Callback;

/**
 * Created by Alex on 04/12/2016.
 */

public class RideManager {

	private PadadaApiClient mPadadaApiClient;
	private AccountManager mAccountManager;

	public RideManager(Context context) {
		mPadadaApiClient = new PadadaApiClient(context);
		mAccountManager = new AccountManager(context);
	}

	public void startRide(Callback<ApiResult<Ride>> callback) {
		mPadadaApiClient.getApiService().startRide(mAccountManager.getCustomer().getObjectId(), callback);
	}

	public void endRide(Callback<ApiResult<Ride>> callback) {
		mPadadaApiClient.getApiService().endRide(mAccountManager.getCustomer().getObjectId(), callback);
	}

	public void getRideHistory(Callback<ApiResult<List<Ride>>> callback) {
		if(mAccountManager.getCustomer() != null) {
			mPadadaApiClient.getApiService().getRideHistory(mAccountManager.getCustomer().getObjectId(), callback);
		}
	}
}
