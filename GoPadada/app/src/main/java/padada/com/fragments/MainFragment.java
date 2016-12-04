package padada.com.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
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
	private RecyclerView mRecyclerView;

	private CircleImageView mCiProfile;

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
				PromotionAdapter promotionAdapter = new PromotionAdapter(getContext(), listApiResult.getResult());
				mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
				mRecyclerView.setAdapter(promotionAdapter);
			}

			@Override
			public void failure(RetrofitError error) {
				error.printStackTrace();
			}
		});
	}

	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initViews(view);
	}

	private void initViews(View view) {
		mCiProfile = (CircleImageView) view.findViewById(R.id.ci_profile);
		mRecyclerView = (RecyclerView)  view.findViewById(R.id.recycler_promotion);
	}
}
