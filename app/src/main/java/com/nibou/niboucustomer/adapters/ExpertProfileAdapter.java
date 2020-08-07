package com.nibou.niboucustomer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.activitys.ExpertActivity;
import com.nibou.niboucustomer.activitys.RatingActivity;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.models.ReviewModel;
import com.nibou.niboucustomer.utils.DateFormatUtil;
import com.willy.ratingbar.ScaleRatingBar;


public class ExpertProfileAdapter extends RecyclerView.Adapter<ExpertProfileAdapter.MyViewHolder> {

    private Context context;
    private ReviewModel reviewModel;
    private ProfileModel profileModel;
    private int ADAPTER_TYPE;

    public ExpertProfileAdapter(Context context, int ADAPTER_TYPE, ProfileModel profileModel, ReviewModel reviewModel) {
        this.context = context;
        this.ADAPTER_TYPE = ADAPTER_TYPE;
        this.profileModel = profileModel;
        this.reviewModel = reviewModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_expert, viewGroup, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        if (profileModel != null) {
            myViewHolder.box_view.setVisibility(View.VISIBLE);
            myViewHolder.timing.setVisibility(View.GONE);
            myViewHolder.review_name.setVisibility(View.GONE);
            myViewHolder.review_details.setVisibility(View.GONE);
            myViewHolder.review_date.setVisibility(View.GONE);
            myViewHolder.scaleRatingBar.setVisibility(View.GONE);

            myViewHolder.survey_name.setText(profileModel.getIncluded().get(position).getAttributes().getTitle());

            if (ADAPTER_TYPE == ExpertActivity.TIMING) {
                myViewHolder.timing.setVisibility(View.VISIBLE);
                myViewHolder.survey_name.setText(DateFormatUtil.getDay(context, profileModel.getIncluded().get(position).getAttributes().getDay_number()));
                myViewHolder.timing.setText("(" + profileModel.getIncluded().get(position).getAttributes().getTime_from() + " - " + profileModel.getIncluded().get(position).getAttributes().getTime_to() + ")");
            }

        } else {
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) myViewHolder.item.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            myViewHolder.item.setLayoutParams(layoutParams);

            myViewHolder.box_view.setVisibility(View.GONE);
            myViewHolder.review_name.setVisibility(View.VISIBLE);
            myViewHolder.review_details.setVisibility(View.VISIBLE);
            myViewHolder.review_date.setVisibility(View.VISIBLE);
            myViewHolder.scaleRatingBar.setVisibility(View.VISIBLE);

            myViewHolder.box_view.setOnClickListener(v -> {
                Intent intent = new Intent(context, RatingActivity.class);
                intent.putExtra("expert_id", profileModel.getData().getId());
                context.startActivity(intent);
            });

            myViewHolder.review_name.setText(getName(reviewModel.getData().get(position).getRelationships().getCustomer().getData().getId()));
            myViewHolder.review_date.setText(DateFormatUtil.getRequiredDateFormat(DateFormatUtil.getServerMilliSeconds(reviewModel.getData().get(position).getAttributes().getCreated_at()), "MMM dd , yyyy"));
            myViewHolder.scaleRatingBar.setRating(reviewModel.getData().get(position).getAttributes().getValue());
            myViewHolder.review_details.setText(reviewModel.getData().get(position).getAttributes().getComment());
        }
    }

    private String getName(String userId) {
        for (int i = 0; i < reviewModel.getIncluded().size(); i++) {
            if (reviewModel.getIncluded().get(i).getId().equals(userId)) {
                return reviewModel.getIncluded().get(i).getAttributes().getUsername();
            }
        }
        return "";
    }


    @Override
    public int getItemCount() {
        if (profileModel != null) {
            if (profileModel.getIncluded() != null)
                return profileModel.getIncluded().size();
            else
                return 0;
        } else {
            if (reviewModel.getData() != null)
                return reviewModel.getData().size();
            else
                return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView survey_name, timing;
        private TextView review_name;
        private TextView review_details, review_date;
        private View item, box_view;
        private ScaleRatingBar scaleRatingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            survey_name = itemView.findViewById(R.id.survey_name);
            review_name = itemView.findViewById(R.id.review_name);
            review_details = itemView.findViewById(R.id.review_details);
            item = itemView.findViewById(R.id.item);
            box_view = itemView.findViewById(R.id.box_view);
            timing = itemView.findViewById(R.id.timing);
            review_date = itemView.findViewById(R.id.review_date);
            scaleRatingBar = itemView.findViewById(R.id.review_star);
        }
    }
}
