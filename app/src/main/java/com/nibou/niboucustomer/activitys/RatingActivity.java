package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.adapters.ReviewListAdapter;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityRatingBinding;
import com.nibou.niboucustomer.models.ReviewModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

public class RatingActivity extends BaseActivity {

    private ActivityRatingBinding binding;
    private ReviewListAdapter reviewListAdapter;
    private Context context;
    private ReviewModel reviewModel;

    private void setToolbar() {
        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.screen_title_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rating);
        context = this;
        setToolbar();

        if (AppUtil.isInternetAvailable(context)) {
            if (LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken() != null)
                reviewNetworkCall();
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void showProfileData() {
        binding.reviewList.setLayoutManager(new LinearLayoutManager(context));
        reviewListAdapter = new ReviewListAdapter(context, reviewModel);
        binding.reviewList.setAdapter(reviewListAdapter);
    }

    private void reviewNetworkCall() {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getReviewRequest(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE),
                AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(),
                getIntent().getStringExtra("expert_id")),
                new ApiHandler.CallBack() {
                    @Override
                    public void success(boolean isSuccess, Object data) {
                        AppDialogs.getInstance().showProgressBar(context, null, false);
                        if (isSuccess) {
                            reviewModel = (ReviewModel) data;
                            if (reviewModel != null) {
                                reviewModel.reverseReviewList();
                                showProfileData();
                            }
                        }
                    }

                    @Override
                    public void failed() {
                        AppDialogs.getInstance().showProgressBar(context, null, false);
                    }
                });
    }
}
