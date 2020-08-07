package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;

import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityPrivacyPolicyBinding;
import com.nibou.niboucustomer.databinding.ActivityUserCheckBinding;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

public class PrivacyPolicyActivity extends BaseActivity {

    private ActivityPrivacyPolicyBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_privacy_policy);
        context = this;
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });

        binding.titleHeader.setVisibility(View.GONE);

        if (getIntent().getStringExtra("type").equals("privacy")) {
            binding.titleHeader.setText(context.getResources().getString(R.string.privacy_policy));
        } else if (getIntent().getStringExtra("type").equals("terms")) {
            binding.titleHeader.setText(context.getResources().getString(R.string.term_condition));
        }

        if (AppUtil.isInternetAvailable(context)) {
            getTextNetworkCall();
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void getTextNetworkCall() {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getTextRequest(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), getIntent().getStringExtra("type")), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    if (binding != null) {
                        ProfileModel profileModel = (ProfileModel) data;
                        binding.webView.getSettings();
                        binding.webView.setBackgroundColor(Color.TRANSPARENT);
                        binding.webView.loadData(profileModel.getData().getAttributes().getText(), "text/html", "UTF-8");
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
