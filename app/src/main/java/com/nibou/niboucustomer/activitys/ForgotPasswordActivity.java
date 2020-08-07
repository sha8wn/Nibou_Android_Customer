package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityForgotPasswordBinding;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

public class ForgotPasswordActivity extends BaseActivity {

    private ActivityForgotPasswordBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        context = this;
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });

        binding.resetPwd.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            if (AppUtil.isInternetAvailable(context)) {
                if (screenValidate())
                    forgotPasswordNetworkCall();
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        });
    }

    private void forgotPasswordNetworkCall() {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).forgotPassword(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), binding.etEmail.getText().toString().trim()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {

                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.success).toUpperCase(), getString(R.string.dialog_reset_pwd_message), getString(R.string.OK), new AppDialogs.DialogCallback() {
                        @Override
                        public void response(boolean status) {
                            onBackPressed();
                        }
                    });
                } else {
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.error).toUpperCase(), getString(R.string.forgot_password_error_alert), getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }

    private boolean screenValidate() {
        if (TextUtils.isEmpty(binding.etEmail.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.email_empty_alert));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString().trim()).matches()) {
            AppUtil.showToast(this, getResources().getString(R.string.email_invalid_alert));
            return false;
        }
        return true;
    }

}
