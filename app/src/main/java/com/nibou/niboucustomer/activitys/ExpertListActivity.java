package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.adapters.ExpertListAdapter;
import com.nibou.niboucustomer.adapters.IssueHistoryAdapter;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityExpertListBinding;
import com.nibou.niboucustomer.databinding.ActivitySurveyPreferenceBinding;
import com.nibou.niboucustomer.models.PreviousExpertModel;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.models.SurveyModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

import java.util.ArrayList;

public class ExpertListActivity extends BaseActivity {
    private ActivityExpertListBinding binding;
    private Context context;
    private ExpertListAdapter expertListAdapter;
    private PreviousExpertModel previousExpertModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expert_list);
        context = this;
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });

        binding.newExpert.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            if (getIntent().getSerializableExtra(AppConstant.PROFILE_MODEL) != null) {
                Intent intent = new Intent(context, ExpertActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
            } else {
                AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.no_expert_alert_title).toUpperCase(), context.getString(R.string.no_expert_alert), context.getString(R.string.OK), null);
            }
        });

        previousExpertModel = (PreviousExpertModel) getIntent().getSerializableExtra(AppConstant.PROFILE_MODEL_LIST);

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(context));
        expertListAdapter = new ExpertListAdapter(context, previousExpertModel);
        binding.recyclerview.setAdapter(expertListAdapter);
    }

    public void onAdapterClick(int position) {
        if (getIntent().hasExtra(AppConstant.SWITCH)) {
            if (LocalPrefences.getInstance().getBoolean(context, AppConstant.IS_FIRST_ROOM_CREATED)) {
                Intent intent = new Intent(context, ChatMessageCopyActivity.class);
                intent.putExtras(getIntent().getExtras());
                intent.putExtra(AppConstant.PROFILE_MODEL, previousExpertModel.getData().get(position));
                startActivity(intent);
            } else {
                if (AppUtil.isInternetAvailable(context)) {
                    createSessionNetworkCall(previousExpertModel.getData().get(position).getId());
                } else {
                    AppUtil.showToast(context, getString(R.string.internet_error));
                }
            }
        } else if (getIntent().hasExtra(AppConstant.NEW_SWITCH)) {
            Intent intent = new Intent(context, ChatMessageCopyActivity.class);
            intent.putExtras(getIntent().getExtras());
            intent.putExtra(AppConstant.PROFILE_MODEL, previousExpertModel.getData().get(position));
            startActivity(intent);
        }
    }

    private void createSessionNetworkCall(String expertId) {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).createChatSession(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), expertId, getSelectedSurveyIds()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    LocalPrefences.getInstance().putBoolean(context, AppConstant.IS_FIRST_ROOM_CREATED, true);
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
