package padada.com.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

	private RecyclerView mRVUsers;

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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRVUsers = (RecyclerView) view.findViewById(R.id.recycler_users);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPadadaApiClient = new PadadaApiClient(getActivity());

	}

	@Override
	public void onResume() {
		super.onResume();

		mPadadaApiClient.getApiService().getUsers("lkajdsf", new Callback<ApiResult<List<User>>>() {
			@Override
			public void success(ApiResult<List<User>> listApiResult, Response response) {
				mUserList = listApiResult.getResult();
				setupAdapter(mUserList);
			}

			@Override
			public void failure(RetrofitError error) {
				error.printStackTrace();
			}
		});
	}

	private void setupAdapter(List<User> users) {

		UserAdapter userAdapter = new UserAdapter(getActivity(), users);
		mRVUsers.setLayoutManager(new LinearLayoutManager(getContext()));
		mRVUsers.setAdapter(userAdapter);
	}
}
