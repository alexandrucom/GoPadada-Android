package padada.com.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import padada.com.R;
import padada.com.model.Promotion;

/**
 * Created by Vlad on 04.12.2016.
 */

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder> {
    private List<Promotion> mPromotionList;
    private Context mContext;

    public PromotionAdapter(Context context, List<Promotion> promotions) {
        mContext = context;
        this.mPromotionList = promotions;
    }

    @Override
    public PromotionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promotion, parent, false);
        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PromotionViewHolder holder, int position) {
        holder.bindPromotion(mPromotionList.get(position));
    }

    @Override
    public int getItemCount() {
        return mPromotionList == null ? 0 : mPromotionList.size();
    }

    protected class PromotionViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvPromotion;
        private TextView mTvName;
        private TextView mTvDescription;
        private TextView mTvPoint;
        private TextView mTvPurchase;

        public PromotionViewHolder(View itemView) {
            super(itemView);
            mIvPromotion = (ImageView) itemView.findViewById(R.id.iv_promotion);
            mTvName = (TextView) itemView.findViewById(R.id.tv_promotion_name);
            mTvDescription = (TextView) itemView.findViewById(R.id.tv_promotion_description);
            mTvPoint = (TextView) itemView.findViewById(R.id.tv_point_profile);
            mTvPurchase = (TextView) itemView.findViewById(R.id.tv_purchase);
        }

        private void bindPromotion(Promotion promotion) {
            Picasso.with(mContext).load(promotion.getPhotoURL()).into(mIvPromotion);
            mTvName.setText(promotion.getTitle());
            mTvDescription.setText(promotion.getDescription());
            mTvPoint.setText(promotion.getPoints() + " Points");
        }
    }
}
