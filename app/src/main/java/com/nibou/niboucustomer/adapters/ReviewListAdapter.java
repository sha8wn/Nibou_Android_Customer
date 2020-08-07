package com.nibou.niboucustomer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.models.ReviewModel;
import com.nibou.niboucustomer.utils.DateFormatUtil;
import com.willy.ratingbar.ScaleRatingBar;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.MyViewHolder> {

    private Context context;
    private ReviewModel reviewModel;

    public ReviewListAdapter(Context context, ReviewModel reviewModel) {
        this.context = context;
        this.reviewModel = reviewModel;
    }

    @NonNull
    @Override
    public ReviewListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_list_item, viewGroup, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewListAdapter.MyViewHolder myViewHolder, final int i) {
        myViewHolder.name.setText(getName(reviewModel.getData().get(i).getRelationships().getCustomer().getData().getId()));
        myViewHolder.description.setText(reviewModel.getData().get(i).getAttributes().getComment());
        myViewHolder.date.setText(DateFormatUtil.getRequiredDateFormat(DateFormatUtil.getServerMilliSeconds(reviewModel.getData().get(i).getAttributes().getCreated_at()), "MMM dd , yyyy"));
        myViewHolder.rating.setRating(reviewModel.getData().get(i).getAttributes().getValue());
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
        if (reviewModel.getData() != null)
            return reviewModel.getData().size();
        else
            return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView name, date, description;
        private ScaleRatingBar rating;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            rating = itemView.findViewById(R.id.rating);
            description = itemView.findViewById(R.id.description);
        }
    }
}
