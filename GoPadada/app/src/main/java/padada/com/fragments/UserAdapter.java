package padada.com.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import padada.com.R;
import padada.com.model.User;

/**
 * Created by Vlad on 04.12.2016.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.RideViewHolder> {

	private Context mContext;
	private List<User> mUserList;

	public UserAdapter(Context context, List<User> userList) {
		mContext = context;
		this.mUserList = userList;
	}

	@Override
	public RideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
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
		private TextView txtUsername;
		private TextView txtLevel;
		private TextView txtPoints;
		private CircleImageView imgAvatar;

		public RideViewHolder(View itemView) {
			super(itemView);
			txtUsername = (TextView) itemView.findViewById(R.id.txt_username);
			txtLevel = (TextView) itemView.findViewById(R.id.txt_level);
			txtPoints = (TextView) itemView.findViewById(R.id.txt_points);
			imgAvatar = (CircleImageView) itemView.findViewById(R.id.img_avatar);
		}

		private void bindUser(User user) {
			txtUsername.setText(user.getUsername());
			txtLevel.setText("Level: " + String.valueOf(user.getLevel()));
			txtPoints.setText("Points: " + String.valueOf(user.getPoints()));
			Picasso.with(mContext).load(user.getProfilePhotoUrl()).into(imgAvatar);
		}

	}

}
