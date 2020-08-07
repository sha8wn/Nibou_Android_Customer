package com.nibou.niboucustomer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.activitys.ExpertListActivity;
import com.nibou.niboucustomer.models.ActiveChatSessionModel;
import com.nibou.niboucustomer.models.PreviousExpertModel;
import com.nibou.niboucustomer.utils.AppConstant;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;


public class ExpertListAdapter extends RecyclerView.Adapter<ExpertListAdapter.MyViewHolder> {

    private Context context;
    private PreviousExpertModel previousExpertModel;

    public ExpertListAdapter(Context context, PreviousExpertModel previousExpertModel) {
        this.context = context;
        this.previousExpertModel = previousExpertModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_expert_list, viewGroup, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.item.setTag(position);
        myViewHolder.item.setOnClickListener(v -> {
            if (context instanceof ExpertListActivity) {
                ((ExpertListActivity) context).onAdapterClick(Integer.parseInt(v.getTag().toString()));
            }
        });

        myViewHolder.expert_name.setText(previousExpertModel.getData().get(position).getAttributes().getName());
        showImage(myViewHolder.circleImageView, AppConstant.FILE_BASE_URL + previousExpertModel.getData().get(position).getAttributes().getAvatar().getUrl());
    }

    private void showImage(ImageView imageView, String url) {
        Glide.with(context)
                .load(url)
                .dontAnimate()
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.default_placeholder)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return previousExpertModel.getData().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView expert_name;
        private CircleImageView circleImageView;
        private View item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            expert_name = itemView.findViewById(R.id.expert_name);
            circleImageView = itemView.findViewById(R.id.profile_image);
        }
    }
}
