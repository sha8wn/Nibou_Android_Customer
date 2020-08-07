package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityFeedbackBinding;
import com.nibou.niboucustomer.databinding.ActivityPaymentCardBinding;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

import java.util.HashMap;

public class FeedbackActivity extends BaseActivity {
    private ActivityFeedbackBinding binding;

    private Context context;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feedback);
        context = this;
        setToolbar();

        binding.addReview.setOnClickListener(v -> {
            if (AppUtil.isInternetAvailable(context)) {
                if (screenValidate()) {
                    if (LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken() != null)
                        sendFeedbackNetworkCall();
                }
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        });
    }

    public void sendFeedbackNetworkCall() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("subject", binding.subject.getText().toString().trim());
        parameters.put("message", binding.feedbackMsg.getText().toString().trim());
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).sendFeedback(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.success).toUpperCase(), getString(R.string.feedback_success_alert), getString(R.string.OK), status -> {
                        onBackPressed();
                    });
                } else {
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.error).toUpperCase(), getString(R.string.wrong_with_backend), getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }


    private boolean screenValidate() {
        if (TextUtils.isEmpty(binding.subject.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.subject_empty_alert));
            return false;
        } else if (TextUtils.isEmpty(binding.feedbackMsg.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.feedback_empty_alert));
            return false;
        }
        return true;
    }

}
