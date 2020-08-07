package com.nibou.niboucustomer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.activitys.*;
import com.nibou.niboucustomer.fragments.MessageHomeFragment;
import com.nibou.niboucustomer.models.ActiveChatSessionModel;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.models.SurveyModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.ChatConstants;
import com.nibou.niboucustomer.utils.LocalPrefences;

import java.util.ArrayList;


public class IssueHistoryAdapter extends RecyclerView.Adapter<IssueHistoryAdapter.MyViewHolder> {

    private Context context;
    private ActiveChatSessionModel activeChatSessionModel;
    private MessageHomeFragment messageHomeFragment;

    public IssueHistoryAdapter(Context context, MessageHomeFragment messageHomeFragment, ActiveChatSessionModel activeChatSessionModel) {
        this.context = context;
        this.messageHomeFragment = messageHomeFragment;
        this.activeChatSessionModel = activeChatSessionModel;
    }

    public void refereshList(ActiveChatSessionModel activeChatSessionModel) {
        this.activeChatSessionModel = activeChatSessionModel;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_issue_history, viewGroup, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.item.setTag(position);
        myViewHolder.item.setOnClickListener(v -> {
            ProfileModel profileModel = getProfileModel(activeChatSessionModel.getData().get(Integer.parseInt(v.getTag().toString())));
            if (profileModel != null && profileModel.getData().getRelationships().getUser_credit_cards() != null && profileModel.getData().getRelationships().getUser_credit_cards().getData() != null && profileModel.getData().getRelationships().getUser_credit_cards().getData().size() > 0) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(AppConstant.SURVEY_MODEL, getSurveyModel(Integer.parseInt(v.getTag().toString())));
                intent.putExtra(AppConstant.EXPERT_DETAILS_MODEL, activeChatSessionModel.getData().get(Integer.parseInt(v.getTag().toString())));
                intent.putExtra(ChatConstants.ROOM_ID, activeChatSessionModel.getData().get(Integer.parseInt(v.getTag().toString())).getId());
                messageHomeFragment.startActivityForResult(intent, 100);
            } else {
                Intent intent = new Intent(context, PaymentInfoActivity.class);
                intent.putExtra(AppConstant.SURVEY_MODEL, getSurveyModel(Integer.parseInt(v.getTag().toString())));
                intent.putExtra(AppConstant.EXPERT_DETAILS_MODEL, activeChatSessionModel.getData().get(Integer.parseInt(v.getTag().toString())));
                intent.putExtra(ChatConstants.ROOM_ID, activeChatSessionModel.getData().get(Integer.parseInt(v.getTag().toString())).getId());
                messageHomeFragment.startActivityForResult(intent, 100);
            }
        });

        if (activeChatSessionModel.getData().get(position).getAttributes().getExpertises() != null && activeChatSessionModel.getData().get(position).getAttributes().getExpertises().size() > 0) {
            myViewHolder.survey1.setText(activeChatSessionModel.getData().get(position).getAttributes().getExpertises().get(0).getData().getAttributes().getTitle());
            myViewHolder.survey1.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.survey1.setVisibility(View.GONE);
        }
        if (activeChatSessionModel.getData().get(position).getAttributes().getExpertises() != null && activeChatSessionModel.getData().get(position).getAttributes().getExpertises().size() > 1) {
            myViewHolder.survey2.setText(activeChatSessionModel.getData().get(position).getAttributes().getExpertises().get(1).getData().getAttributes().getTitle());
            myViewHolder.survey2.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.survey2.setVisibility(View.GONE);
        }
        if (activeChatSessionModel.getData().get(position).getAttributes().getExpertises() != null && activeChatSessionModel.getData().get(position).getAttributes().getExpertises().size() > 2) {
            myViewHolder.survey3.setText(activeChatSessionModel.getData().get(position).getAttributes().getExpertises().get(2).getData().getAttributes().getTitle());
            myViewHolder.survey3.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.survey3.setVisibility(View.GONE);
        }

        if (LocalPrefences.getInstance().getInt(context, activeChatSessionModel.getData().get(position).getId()) > 0) {
            myViewHolder.count.setVisibility(View.VISIBLE);
            myViewHolder.count.setText("" + LocalPrefences.getInstance().getInt(context, activeChatSessionModel.getData().get(position).getId()));
        } else {
            myViewHolder.count.setVisibility(View.GONE);
        }
    }

    private SurveyModel getSurveyModel(int position) {
        SurveyModel surveyModel = new SurveyModel();
        ArrayList<String> expertiesList = new ArrayList<>();
        for (int i = 0; i < activeChatSessionModel.getData().get(position).getAttributes().getExpertises().size(); i++) {
            expertiesList.add(activeChatSessionModel.getData().get(position).getAttributes().getExpertises().get(i).getData().getId());
        }
        surveyModel.setSelectedSurveyList(expertiesList);
        return surveyModel;
    }

    private ProfileModel getProfileModel(ActiveChatSessionModel.Data data) {
        for (int i = 0; i < data.getAttributes().getUsers().size(); i++) {
            if ((data.getAttributes().getUsers().get(i).getData().getId().equals(LocalPrefences.getInstance().getLocalProfileModel(context).getData().getId()))) {
                return data.getAttributes().getUsers().get(i);
            }
        }
        return null;
    }


    @Override
    public int getItemCount() {
        if (activeChatSessionModel.getData() != null)
            return activeChatSessionModel.getData().size();
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView survey1, survey2, survey3, count;
        private View chatIcon;
        private View item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            count = itemView.findViewById(R.id.message_count);
            survey1 = itemView.findViewById(R.id.survey1);
            survey2 = itemView.findViewById(R.id.survey2);
            survey3 = itemView.findViewById(R.id.survey3);
            chatIcon = itemView.findViewById(R.id.chat_icon);
        }
    }
}
