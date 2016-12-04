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
import padada.com.model.Promotion;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainFragment extends Fragment {


	private PadadaApiClient mPadadaApiClient;
	private List<Promotion> mPromotionList;

	public MainFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_main, container, false);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mPadadaApiClient = new PadadaApiClient(getActivity());

		mPadadaApiClient.getApiService().getPromotions("3fg3k4", new Callback<ApiResult<List<Promotion>>>() {
			@Override
			public void success(ApiResult<List<Promotion>> listApiResult, Response response) {
				mPromotionList = listApiResult.getResult();
			}

			@Override
			public void failure(RetrofitError error) {
				error.printStackTrace();
			}
		});
	}
}
