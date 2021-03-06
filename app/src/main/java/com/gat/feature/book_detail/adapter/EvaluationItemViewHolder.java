package com.gat.feature.book_detail.adapter;

import android.support.annotation.LayoutRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gat.R;
import com.gat.common.adapter.ItemViewHolder;
import com.gat.common.customview.ViewMoreTextView;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.DateTimeUtil;
import com.gat.common.util.MZDebug;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mryit on 4/20/2017.
 */

public class EvaluationItemViewHolder extends ItemViewHolder<EvaluationItem> {

    @BindView(R.id.image_view_avatar)
    ImageView imageViewAvatar;

    @BindView(R.id.text_view_time_ago)
    TextView textViewTimeAgo;

    @BindView(R.id.text_view_name)
    TextView textViewName;

    @BindView(R.id.rating_bar_comment)
    RatingBar ratingBarComment;

    @BindView(R.id.text_view_comment_evaluation)
    TextView textViewCommentEvaluation;

    public EvaluationItemViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        super(parent, layoutId);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onBindItem(EvaluationItem item) {
        super.onBindItem(item);

        MZDebug.e("EvaluationItemViewHolder", "onBindItem");

        textViewTimeAgo.setText(String.valueOf(DateTimeUtil.calculateTimeAgo(item.evaluation().getEvaluationTime())));
        textViewName.setText(TextUtils.isEmpty(item.evaluation().getName()) ? "" : item.evaluation().getName());
        ratingBarComment.setRating(item.evaluation().getValue());
        textViewCommentEvaluation.setText( TextUtils.isEmpty(item.evaluation().getReview()) ? "" : item.evaluation().getReview() );
        ViewMoreTextView.makeTextViewResizable(textViewCommentEvaluation, 3, mContext.getString(R.string.view_more), false);

        if ( ! TextUtils.isEmpty(item.evaluation().getImageId())) {
            ClientUtils.setImage(mContext, imageViewAvatar, R.drawable.default_user_icon,
                    ClientUtils.getUrlImage(item.evaluation().getImageId(), ClientUtils.SIZE_SMALL));
        }
    }
}
