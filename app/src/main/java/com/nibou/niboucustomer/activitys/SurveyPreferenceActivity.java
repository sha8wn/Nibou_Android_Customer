package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivitySurveyPreferenceBinding;
import com.nibou.niboucustomer.databinding.ActivityUserCheckBinding;
import com.nibou.niboucustomer.models.PreviousExpertModel;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.models.SurveyModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

import java.util.ArrayList;
import java.util.HashMap;

public class SurveyPreferenceActivity extends BaseActivity {
    private ActivitySurveyPreferenceBinding binding;
    private Context context;
    private ProfileModel profileModel;
    private String expert_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_survey_preference);
        context = this;
        binding.btnContinue.setVisibility(View.GONE);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });

        binding.anyPrefer.setOnClickListener(v -> {
            getExpertNetworkCall(null);
        });
        binding.femalePrefer.setOnClickListener(v -> {
            getExpertNetworkCall(AppConstant.FEMALE);
        });
        binding.malePrefer.setOnClickListener(v -> {
            getExpertNetworkCall(AppConstant.MALE);
        });

        if (getIntent().hasExtra(AppConstant.PROFILE_MODEL)) {
            expert_id = ((ProfileModel) getIntent().getSerializableExtra(AppConstant.PROFILE_MODEL)).getData().getId();
        }

    }

    private void getExpertNetworkCall(String gender) {
        if (AppUtil.isInternetAvailable(context)) {
            AppDialogs.getInstance().showFullScreenProgress(context, true, getString(R.string.please_wait), getString(R.string.please_wait_message));
            ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getExpert(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), gender, expert_id, getSelectedSurveyIds()), new ApiHandler.CallBack() {
                @Override
                public void success(boolean isSuccess, Object data) {
                    if (isSuccess) {
                        if (getIntent().hasExtra(AppConstant.SWITCH) || getIntent().hasExtra(AppConstant.NEW_SWITCH)) {
                            profileModel = (ProfileModel) data;
                            prevoiusSpokenExpertNetworkCall(gender);
                        } else {
                            if (data != null) {
                                moveToExpertScreen((ProfileModel) data);
                            } else {
                                AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.no_expert_alert_title).toUpperCase(), context.getString(R.string.no_expert_alert), context.getString(R.string.OK), status -> {
                                    AppDialogs.getInstance().hideFullScreenProgressWithoutDelay();
                                    finish();
                                });
                            }
                        }
                    } else {
                        AppDialogs.getInstance().hideFullScreenProgress();
                    }
                }

                @Override
                public void failed() {
                    AppDialogs.getInstance().hideFullScreenProgress();
                }
            });
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void prevoiusSpokenExpertNetworkCall(String gender) {
        if (AppUtil.isInternetAvailable(context)) {
            ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getPrevoiusSpokenExpert(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), gender, expert_id, getSelectedSurveyIds()), new ApiHandler.CallBack() {
                @Override
                public void success(boolean isSuccess, Object data) {
                    if (isSuccess) {
                        PreviousExpertModel previousExpertModel = (PreviousExpertModel) data;
                        if (previousExpertModel != null && previousExpertModel.getData() != null && previousExpertModel.getData().size() > 0) {
                            moveToExpertListScreen(previousExpertModel);
                        } else {
                            if (profileModel != null) {
                                moveToExpertScreen(profileModel);
                            } else {
                                AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.no_expert_alert_title).toUpperCase(), context.getString(R.string.no_expert_alert), context.getString(R.string.OK), status -> {
                                    AppDialogs.getInstance().hideFullScreenProgressWithoutDelay();
                                    finish();
                                });
                            }
                        }
                    } else {
                        AppDialogs.getInstance().hideFullScreenProgress();
                    }
                }

                @Override
                public void failed() {
                    AppDialogs.getInstance().hideFullScreenProgress();
                }
            });
        } else {
            AppDialogs.getInstance().hideFullScreenProgress();
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void moveToExpertScreen(ProfileModel profileModel) {
        Intent intent = new Intent(context, ExpertActivity.class);
        intent.putExtras(getIntent().getExtras());
        intent.putExtra(AppConstant.PROFILE_MODEL, profileModel);
        startActivity(intent);
        AppDialogs.getInstance().hideFullScreenProgress();
    }

    private void moveToExpertListScreen(PreviousExpertModel previousExpertModel) {
        Intent intent = new Intent(context, ExpertListActivity.class);
        intent.putExtras(getIntent().getExtras());
        intent.putExtra(AppConstant.PROFILE_MODEL, profileModel);
        intent.putExtra(AppConstant.PROFILE_MODEL_LIST, previousExpertModel);
        startActivity(intent);
        AppDialogs.getInstance().hideFullScreenProgress();
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
