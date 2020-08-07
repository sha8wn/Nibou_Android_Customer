package com.nibou.niboucustomer.activitys;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.actioncable.ActionSessionHandler;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivitySignupBinding;
import com.nibou.niboucustomer.databinding.ActivitySignupSuccessBinding;
import com.nibou.niboucustomer.models.AccessTokenModel;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.CustomTypefaceSpan;
import com.nibou.niboucustomer.utils.LocalPrefences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SignupSuccessActivity extends BaseActivity {

    private ActivitySignupSuccessBinding binding;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup_success);
        context = this;

        binding.btnContinue.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            if (AppUtil.isInternetAvailable(context)) {
                accessTokenNetworkCall();
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    private void showErrorAlert() {
        AppDialogs.getInstance().showConfirmCustomDialog(context, getString(R.string.system_error).toUpperCase(), getString(R.string.account_activate_error_message), getString(R.string.CANCEL).toUpperCase(), getString(R.string.try_again).toUpperCase(), status -> {
            if (status) {
                if (AppUtil.isInternetAvailable(context)) {
                    accessTokenNetworkCall();
                } else {
                    AppUtil.showToast(context, getString(R.string.internet_error));
                }
            }
        });
    }


    private void accessTokenNetworkCall() {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getAccessToken(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.CLIENT_ID, AppConstant.CLIENT_SECRET, AppConstant.PASSWORD, getIntent().getStringExtra(AppConstant.EMAIL), getIntent().getStringExtra(AppConstant.PASSWORD), "1"), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    profileNetworkCall((AccessTokenModel) data);
                    uploadDeviceToken(((AccessTokenModel) data).getAccessToken());
                } else {
                    showErrorAlert();
                    AppDialogs.getInstance().showProgressBar(context, null, false);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }

    private void profileNetworkCall(AccessTokenModel accessTokenModel) {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getMyProfile(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + accessTokenModel.getAccessToken(), "languages"), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    LocalPrefences.getInstance().saveLocalAccessTokenModel(context, accessTokenModel);
                    LocalPrefences.getInstance().saveLocalProfileModel(context, (ProfileModel) data);
                    ActionSessionHandler.getActionSessionHandler(context).connectWS();
                    Intent intent = new Intent(context, PaymentInfoActivity.class);
                    intent.putExtra("login", true);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    showErrorAlert();
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }

    private void uploadDeviceToken(String accessToken) {
        if (AppUtil.isInternetAvailable(context)) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                sendDeviceNetworkCall(accessToken, instanceIdResult.getToken());
            });
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    public void sendDeviceNetworkCall(String accessToken, String firebasToken) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("devise_id", AppUtil.getDeviceId(context));
        parameters.put("devise_description", "android");
        parameters.put("firebase_token", firebasToken);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).saveDevicesRequest(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + accessToken, parameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {

            }

            @Override
            public void failed() {
            }
        });
    }
}
