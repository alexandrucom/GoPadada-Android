package padada.com.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import padada.com.R;
import padada.com.dal.ApiResult;
import padada.com.dal.PadadaApiClient;
import padada.com.model.User;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LeaderboardsFragment extends Fragment {


	private PadadaApiClient mPadadaApiClient;
	private List<User> mUserList;

	public LeaderboardsFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_leaderboards, container, false);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPadadaApiClient = new PadadaApiClient(getActivity());

		mPadadaApiClient.getApiService().getUsers("lkajdsf", new Callback<ApiResult<List<User>>>() {
			@Override
			public void success(ApiResult<List<User>> listApiResult, Response response) {
				mUserList = listApiResult.getResult();
			}

			@Override
			public void failure(RetrofitError error) {
				error.printStackTrace();
			}
		});
	}
}
