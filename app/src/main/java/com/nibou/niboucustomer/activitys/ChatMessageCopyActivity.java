package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityChatMessageCopyBinding;
import com.nibou.niboucustomer.models.ChatSessionModel;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.models.SurveyModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.ChatConstants;
import com.nibou.niboucustomer.utils.LocalPrefences;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatMessageCopyActivity extends BaseActivity {
    private ActivityChatMessageCopyBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_message_copy);
        context = this;
        binding.toolbar.findViewById(R.id.back_arrow).setVisibility(View.VISIBLE);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });

        binding.shareChat.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            if (AppUtil.isInternetAvailable(context)) {
                if (getIntent().hasExtra(AppConstant.NEW_SWITCH)) {
                    switchExpertNetworkCall(true);
                } else {
                    createChatWithHistorySessionNetworkCall(true);
                }
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        });

        binding.notShareChat.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            if (AppUtil.isInternetAvailable(context)) {
                if (getIntent().hasExtra(AppConstant.NEW_SWITCH)) {
                    switchExpertNetworkCall(false);
                } else {
                    createChatWithHistorySessionNetworkCall(false);
                }
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        });
    }


    private void switchExpertNetworkCall(boolean isPrivate) {
        HashMap<String, String> parameters = new HashMap<>();
        if (getIntent().getSerializableExtra(AppConstant.PROFILE_MODEL) instanceof ProfileModel)
            parameters.put("expert_id", ((ProfileModel) getIntent().getSerializableExtra(AppConstant.PROFILE_MODEL)).getData().getId());
        else
            parameters.put("expert_id", ((ProfileModel.Data) getIntent().getSerializableExtra(AppConstant.PROFILE_MODEL)).getId());

        parameters.put("is_private", String.valueOf(isPrivate));
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).switchExpert(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), getIntent().getStringExtra(ChatConstants.ROOM_ID), parameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object result) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    ChatSessionModel chatSessionModel = (ChatSessionModel) result;
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(AppConstant.SURVEY_MODEL, getIntent().getSerializableExtra(AppConstant.SURVEY_MODEL));
                    intent.putExtra(AppConstant.EXPERT_DETAILS_MODEL, chatSessionModel.getData());
                    intent.putExtra(ChatConstants.ROOM_ID, getIntent().getStringExtra(ChatConstants.ROOM_ID));
                    intent.putExtra(AppConstant.IS_SWITCH_DONE, true);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.error).toUpperCase(), context.getString(R.string.wrong_with_backend), context.getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }


    private void createChatWithHistorySessionNetworkCall(boolean isPrivate) {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        String expertId = "";
        if (getIntent().getSerializableExtra(AppConstant.PROFILE_MODEL) instanceof ProfileModel)
            expertId = ((ProfileModel) getIntent().getSerializableExtra(AppConstant.PROFILE_MODEL)).getData().getId();
        else
            expertId = ((ProfileModel.Data) getIntent().getSerializableExtra(AppConstant.PROFILE_MODEL)).getId();
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).createChatWithHistorySession(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), expertId, getSelectedSurveyIds(), String.valueOf(isPrivate)), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    Intent intent = new Intent(context, HomeActivity.class);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.error).toUpperCase(), context.getString(R.string.wrong_with_backend), context.getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }

    private ArrayList<String> getSelectedSurveyIds() {
        SurveyModel surveyModel = (SurveyModel) getIntent().getSerializableExtra(AppConstant.SURVEY_MODEL);
        if (getIntent().hasExtra(AppConstant.NEW_SWITCH)) {
            return surveyModel.getSelectedSurveyList();
        } else {
            ArrayList<String> arrayList = new ArrayList();
            for (int i = 0; i < surveyModel.getData().size(); i++) {
                if (surveyModel.getData().get(i).isSelected()) {
                    arrayList.add(surveyModel.getData().get(i).getId());
                }
            }
            return arrayList;
        }
    }

}
