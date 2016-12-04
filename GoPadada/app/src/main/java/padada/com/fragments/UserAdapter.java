package padada.com.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;

import padada.com.R;
import padada.com.model.Ride;
import padada.com.model.User;
import padada.com.utils.DateTimeUtils;

/**
 * Created by Vlad on 04.12.2016.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.RideViewHolder> {

    private List<User> mUserList;

    public UserAdapter(List<User> userList) {
        this.mUserList = userList;
    }

    @Override
    public RideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RideViewHolder holder, int position) {
        holder.bindUser(mUserList.get(position));
    }

    @Override
    public int getItemCount() {
        return mUserList == null ? 0 : mUserList.size();
    }

    protected class RideViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvVehicleType;
        private TextView tvRoute;

        public RideViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            tvVehicleType = (TextView) itemView.findViewById(R.id.tv_vehicle_type);
            tvRoute = (TextView) itemView.findViewById(R.id.tv_route);
        }

        private void bindUser(User user) {
//            try {
//                tvDate.setText(DateTimeUtils.getDayOfWeek(user.getStartedAt()));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            tvVehicleType.setText(user.getVehicleType());
//            tvRoute.setText(user.getRouteName());
        }
    }
}
