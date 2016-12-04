package padada.com.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import padada.com.R;
import padada.com.dal.ApiResult;
import padada.com.managers.RideManager;
import padada.com.model.Ride;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HistoryFragment extends Fragment {
	private RecyclerView mRecyclerRides;
	private RideManager mRideManager;

	public HistoryFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_history, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initViews(view);
		mRideManager = new RideManager(getContext());
		setupAdapter();
	}

	private void initViews(View view) {
		mRecyclerRides = (RecyclerView) view.findViewById(R.id.recycler_rides);
	}

	private void setupAdapter() {
		mRideManager.getRideHistory(new Callback<ApiResult<List<Ride>>>() {
			@Override
			public void success(ApiResult<List<Ride>> listApiResult, Response response) {
				RideAdapter rideAdapter = new RideAdapter(listApiResult.getResult());
				mRecyclerRides.setLayoutManager(new LinearLayoutManager(getContext()));
				mRecyclerRides.setAdapter(rideAdapter);

				Log.d(TAG, "success: " + listApiResult.getResult().size());
			}

			@Override
			public void failure(RetrofitError error) {
				Log.d(TAG, "failure: " + error);
			}
		});
	}

	private static final String TAG = "HistoryFragment";
}
