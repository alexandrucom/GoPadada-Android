package padada.com.dal;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

import padada.com.R;
import padada.com.model.Customer;
import padada.com.model.Promotion;
import padada.com.model.Ride;
import padada.com.model.User;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public class PadadaApiClient {

	private static final String BASE_URL = "https://api.parse.com";

	private final static String APP_ID = "X-Parse-Application-Id";
	private final static String REST_API_KEY = "X-Parse-REST-API-Key";

	private Context mContext;
	private static ApiService sApiService;
	private static final int TIME_TO_CONNECT_IN_SECOND = 60;

	public PadadaApiClient(Context context) {
		mContext = context;
	}

	public ApiService getApiService() {
		if (sApiService == null) {
			RestAdapter restAdapter = buildRestAdapter();
			sApiService = restAdapter.create(ApiService.class);
		}
		return sApiService;
	}

	private RestAdapter buildRestAdapter() {
		final OkHttpClient okHttpClient = new OkHttpClient();

		okHttpClient.setReadTimeout(TIME_TO_CONNECT_IN_SECOND, TimeUnit.SECONDS);
		okHttpClient.setConnectTimeout(TIME_TO_CONNECT_IN_SECOND, TimeUnit.SECONDS);
		RestAdapter.Builder restAdapter = new RestAdapter.Builder()
				.setEndpoint(BASE_URL)
				.setClient(new OkClient(okHttpClient))
				.setRequestInterceptor(new RequestInterceptor() {
					@Override
					public void intercept(RequestFacade request) {
						request.addHeader("Accept", "application/json");
						setParseAuth(request);
					}
				});

		restAdapter.setLogLevel(RestAdapter.LogLevel.FULL)
				.setLog(new RestAdapter.Log() {
					@Override
					public void log(String msg) {
						Log.i("Rest Client", msg);
					}
				});

		return restAdapter.build();
	}

	public void setParseAuth(RequestInterceptor.RequestFacade request) {
		request.addHeader(APP_ID, mContext.getString(R.string.parse_application_id));
		request.addHeader(REST_API_KEY, mContext.getString(R.string.parse_api_key));
	}

	public interface ApiService {

		@FormUrlEncoded
		@POST("/1/functions/registerCustomer")
		void register(
				@Field("installationId") String installationId,
				Callback<ApiResult<Customer>> callback
		);

		@FormUrlEncoded
		@POST("/1/functions/startRide")
		void startRide(
				@Field("customerId") String customerId,
				Callback<ApiResult<Ride>> callback
		);

		@FormUrlEncoded
		@POST("/1/functions/endRide")
		void endRide(
				@Field("customerId") String customerId,
				Callback<ApiResult<Ride>> callback
		);

		@FormUrlEncoded
		@POST("/1/functions/getRidesHistory")
		void getRideHistory(
				@Field("customerId") String customerId,
				Callback<ApiResult<List<Ride>>> callback
		);

		@FormUrlEncoded
		@POST("/1/functions/getPromotions")
		void getPromotions(
				@Field("customerId") String customerId,
				Callback<ApiResult<List<Promotion>>> callback
		);

		@FormUrlEncoded
		@POST("/1/functions/getUsers")
		void getUsers(
				@Field("customerId") String customerId,
				Callback<ApiResult<List<User>>> callback
		);

	}

}
