package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityPricingTermsBinding;
import com.nibou.niboucustomer.databinding.ActivityPrivacyPolicyBinding;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

public class PricingAndTermsActivity extends BaseActivity {

    private ActivityPricingTermsBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pricing_terms);
        context = this;

        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.screen_title_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });

        binding.titleHeader.setVisibility(View.GONE);

        if (getIntent().hasExtra(AppConstant.NEW_TERM_CONDITIONS_SCREEN)) {
            binding.toolbar.findViewById(R.id.logout_newbutton).setVisibility(View.VISIBLE);
            ((TextView) binding.toolbar.findViewById(R.id.logout_newbutton)).setTextColor(ContextCompat.getColor(context, R.color.screen_title_color));
            ((TextView) binding.toolbar.findViewById(R.id.logout_newbutton)).setText(context.getResources().getString(R.string.agree));
            binding.toolbar.findViewById(R.id.logout_newbutton).setOnClickListener(v -> {
                AppUtil.hideKeyBoard(context);
                Intent intent = new Intent(context, PaymentCardActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
            });

            if (AppUtil.isInternetAvailable(context)) {
                getTextNetworkCall("data");
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }

        } else {
            if (AppUtil.isInternetAvailable(context)) {
                getTextNetworkCall("terms");
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        }
    }

    private void getTextNetworkCall(String code) {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getTextRequest(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), code), new ApiHandler.CallBack() {
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

    @Override
    public void onBackPressed() {
        if (getIntent().hasExtra("login")) {
        } else {
            super.onBackPressed();
        }
    }

}
