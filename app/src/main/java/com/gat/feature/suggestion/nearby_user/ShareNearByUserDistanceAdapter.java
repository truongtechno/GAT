package com.gat.feature.suggestion.nearby_user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.gat.R;
import com.gat.repository.entity.UserNearByDistance;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mryit on 4/3/2017.
 */

public class ShareNearByUserDistanceAdapter
        extends RecyclerView.Adapter<ShareNearByUserDistanceAdapter.UserSharingNearViewHolder> {

    List<UserNearByDistance> listItems;
    private Context mContext;

    public ShareNearByUserDistanceAdapter (Context context, List<UserNearByDistance> list) {
        listItems = list;
        mContext = context;
    }

    @Override
    public UserSharingNearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.item_user_near_on_map, parent, false);
        UserSharingNearViewHolder holder = new UserSharingNearViewHolder(viewItem);
        return holder;
    }

    @Override
    public void onBindViewHolder(UserSharingNearViewHolder holder, int position) {
        UserNearByDistance user = getItem(position);
        Glide.with(mContext).
                load("http://gatbook-api-v1.azurewebsites.net/api/common/get_image/"
                        + user.getImageId() + "?size=t").into(holder.imageViewUserAvatar);
        holder.textViewDistance.setText(String.valueOf(user.getDistance()));
        holder.textViewFullName.setText(user.getName());
        holder.textViewSharingCount.setText(String.valueOf(user.getSharingCount()));
        holder.textViewReadingCount.setText(String.valueOf(user.getReadCount()));
    }


    @Override
    public int getItemCount() {
        return listItems.size();
    }

    private UserNearByDistance getItem (int position) {
        return listItems.get(position);
    }


    protected class UserSharingNearViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_user_avatar)
        ImageView imageViewUserAvatar;

        @BindView(R.id.tv_distance)
        TextView textViewDistance;

        @BindView(R.id.tv_full_name)
        TextView textViewFullName;

        @BindView(R.id.tv_lend_count)
        TextView textViewSharingCount;

        @BindView(R.id.tv_reading_count)
        TextView textViewReadingCount;

        public UserSharingNearViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
