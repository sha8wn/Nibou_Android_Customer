package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.adapters.ExpertProfileAdapter;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityExpertBinding;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.models.ReviewModel;
import com.nibou.niboucustomer.models.SurveyModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

import java.util.ArrayList;

public class ExpertActivity extends BaseActivity {
    private ActivityExpertBinding binding;

    private ExpertProfileAdapter focusAdapter;
    private ExpertProfileAdapter languageAdapter;
    private ExpertProfileAdapter reviewAdapter;

    private Context context;
    private ProfileModel profileModel;
    public static final int LANGUAGE = 1, EXPERTIES = 2, TIMING = 3, REVIEW = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expert);
        context = this;
        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.app_black_color_code), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });

        if (getIntent().hasExtra(AppConstant.VIEW)) {
            binding.btnContinue.setVisibility(View.GONE);
        }

        binding.pdfView.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            if (getIntent().hasExtra(AppConstant.PROFILE_MODEL) && getIntent().getSerializableExtra(AppConstant.PROFILE_MODEL) != null) {
                ProfileModel profileModel = (ProfileModel) getIntent().getSerializableExtra(AppConstant.PROFILE_MODEL);
                if (profileModel.getData().getAttributes().getPdf() != null && profileModel.getData().getAttributes().getPdf().getUrl() != null) {
                    Intent intent = new Intent(ExpertActivity.this, PDFViewActivity.class);
                    intent.putExtra(AppConstant.URL, AppConstant.FILE_BASE_URL + profileModel.getData().getAttributes().getPdf().getUrl());
                    startActivity(intent);
                } else {
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.alert).toUpperCase(), getString(R.string.no_pdf_alert), getString(R.string.OK), null);
                }
            }
        });

        binding.btnContinue.setOnClickListener(v -> {
            if (getIntent().hasExtra(AppConstant.NEW_SWITCH)) {
                Intent intent = new Intent(context, ChatMessageCopyActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
            } else {
                AppUtil.hideKeyBoard(context);
                if (LocalPrefences.getInstance().getBoolean(context, AppConstant.IS_FIRST_ROOM_CREATED)) {
                    Intent intent = new Intent(context, ChatMessageCopyActivity.class);
                    intent.putExtras(getIntent().getExtras());
                    startActivity(intent);
                } else {
                    if (AppUtil.isInternetAvailable(context)) {
                        createSessionNetworkCall();
                    } else {
                        AppUtil.showToast(context, getString(R.string.internet_error));
                    }
                }
            }
        });

        binding.viewAll.setOnClickListener(v -> {
            Intent intent = new Intent(this, RatingActivity.class);
            intent.putExtra("expert_id", profileModel.getData().getId());
            startActivity(intent);
        });

        binding.reviewRecyclerview.setOnClickListener(v -> {
            Intent intent = new Intent(this, RatingActivity.class);
            intent.putExtra("expert_id", profileModel.getData().getId());
            startActivity(intent);
        });

        showProfileData();

        if (AppUtil.isInternetAvailable(context)) {
            timingNetworkCall();
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void showProfileData() {
        if (getIntent().hasExtra(AppConstant.PROFILE_MODEL) && getIntent().getSerializableExtra(AppConstant.PROFILE_MODEL) != null) {
            profileModel = (ProfileModel) getIntent().getSerializableExtra(AppConstant.PROFILE_MODEL);
            binding.expertName.setText(profileModel.getData().getAttributes().getName());
            binding.expertDetails.setText(profileModel.getData().getAttributes().getShort_bio());
            binding.address.setText(getAddress());
            if (profileModel.getData().getAttributes().getAvatar() != null && profileModel.getData().getAttributes().getAvatar().getUrl() != null) {
                showImage(AppConstant.FILE_BASE_URL + profileModel.getData().getAttributes().getAvatar().getUrl());
            }

            if (profileModel.getData().getAttributes().getPdf() != null && profileModel.getData().getAttributes().getPdf().getUrl() != null) {
                binding.pdfView.setVisibility(View.VISIBLE);
            }
        }
    }

    private String getAddress() {
        return ((profileModel.getData().getAttributes().getCity() != null ? profileModel.getData().getAttributes().getCity() + ", " : "") + (profileModel.getData().getAttributes().getCountry() != null ? profileModel.getData().getAttributes().getCountry() + " - " : "") + (profileModel.getData().getAttributes().getTimezone() != null ? profileModel.getData().getAttributes().getTimezone() : ""));
    }


    private void showImage(String url) {
        Glide.with(context)
                .load(url)
                .dontAnimate()
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.default_placeholder)
                .into(binding.expertDp);
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


    private void createSessionNetworkCall() {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).createChatSession(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), profileModel.getData().getId(), getSelectedSurveyIds()), new ApiHandler.CallBack() {
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

    private void timingNetworkCall() {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getExpertDetails(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), profileModel.getData().getId(), "user_timings"), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    binding.timingRecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    binding.timingRecyclerview.setAdapter(new ExpertProfileAdapter(context, TIMING, (ProfileModel) data, null));
                }
                expertisesNetworkCall();
            }

            @Override
            public void failed() {
                expertisesNetworkCall();
            }
        });
    }

    private void expertisesNetworkCall() {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getExpertDetails(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), profileModel.getData().getId(), "expertises"), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    binding.focusRecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    focusAdapter = new ExpertProfileAdapter(context, EXPERTIES, (ProfileModel) data, null);
                    binding.focusRecyclerview.setAdapter(focusAdapter);
                }
                languageNetworkCall();
            }

            @Override
            public void failed() {
                languageNetworkCall();
            }
        });
    }

    private void languageNetworkCall() {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getExpertDetails(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), profileModel.getData().getId(), "languages"), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    binding.languageRecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    languageAdapter = new ExpertProfileAdapter(context, LANGUAGE, (ProfileModel) data, null);
                    binding.languageRecyclerview.setAdapter(languageAdapter);
                }
                reviewNetworkCall();
            }

            @Override
            public void failed() {
                reviewNetworkCall();
            }
        });
    }

    private void reviewNetworkCall() {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getReviewRequest(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE),
                AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), profileModel.getData().getId()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    binding.reviewRecyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    reviewAdapter = new ExpertProfileAdapter(context, REVIEW, null, (ReviewModel) data);
                    binding.reviewRecyclerview.setAdapter(reviewAdapter);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }
}
