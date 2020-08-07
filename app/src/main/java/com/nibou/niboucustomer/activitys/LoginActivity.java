package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;

import com.google.firebase.iid.FirebaseInstanceId;
import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.actioncable.ActionSessionHandler;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityLoginBinding;
import com.nibou.niboucustomer.models.AccessTokenModel;
import com.nibou.niboucustomer.models.ActiveChatSessionModel;
import com.nibou.niboucustomer.models.ErrorModel;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

import java.util.HashMap;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        context = this;
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });
        binding.forgotPassword.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        binding.login.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            if (AppUtil.isInternetAvailable(context)) {
                if (screenValidate()) {
                    accessTokenNetworkCall();
                }
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        });

        binding.eye.setOnClickListener(v -> {
            if (binding.etPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.eye.setImageResource(R.drawable.eye_icon);
            } else {
                binding.eye.setImageResource(R.drawable.invisible_eye);
                binding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });
    }

    private boolean screenValidate() {
        if (TextUtils.isEmpty(binding.etEmail.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.email_empty_alert));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()) {
            AppUtil.showToast(this, getResources().getString(R.string.email_invalid_alert));
            return false;
        } else if (TextUtils.isEmpty(binding.etPassword.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.password_empty_alert));
            return false;
        }
        return true;
    }

    private void accessTokenNetworkCall() {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getAccessToken(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.CLIENT_ID, AppConstant.CLIENT_SECRET, AppConstant.PASSWORD, binding.etEmail.getText().toString(), binding.etPassword.getText().toString(), "1"), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    profileNetworkCall((AccessTokenModel) data);
                    uploadDeviceToken(((AccessTokenModel) data).getAccessToken());
                } else {
                    AppDialogs.getInstance().showProgressBar(context, null, false);
                    AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.error), context.getString(R.string.error_message), context.getString(R.string.OK), null);
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
                if (isSuccess) {
                    getCardListNetworkCall((ProfileModel) data, accessTokenModel);
                } else {
                    AppDialogs.getInstance().showProgressBar(context, null, false);
                    AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.error), context.getString(R.string.wrong_with_backend), context.getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }

    private void getCardListNetworkCall(ProfileModel model, AccessTokenModel accessTokenModel) {
        ApiHandler.requestService(this, ApiClient.getClient().create(ApiEndPoint.class).getMyProfile(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER +accessTokenModel.getAccessToken(), "user_credit_cards"), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    boolean isCard = false;
                    ProfileModel profileModel = (ProfileModel) data;
                    if (profileModel.getIncluded() != null && profileModel.getIncluded().size() > 0) {
                        for (int i = 0; i < profileModel.getIncluded().size(); i++) {
                            if (profileModel.getIncluded().get(i).getAttributes().isIs_active()) {
                                isCard = true;
                                break;
                            }
                        }
                    }
                    getChatSessionNetworkCall(model, accessTokenModel, isCard);
                } else {
                    AppDialogs.getInstance().showProgressBar(context, null, false);
                    AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.error), context.getString(R.string.wrong_with_backend), context.getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }


    private void getChatSessionNetworkCall(ProfileModel profileModel, AccessTokenModel accessTokenModel, boolean isCard) {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getActiveChatSession(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + accessTokenModel.getAccessToken()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    LocalPrefences.getInstance().saveLocalAccessTokenModel(context, accessTokenModel);
                    LocalPrefences.getInstance().saveLocalProfileModel(context, profileModel);
                    ActionSessionHandler.getActionSessionHandler(context).connectWS();

                    ActiveChatSessionModel activeChatSessionModel = (ActiveChatSessionModel) data;
                    if (isCard) {
                        if (activeChatSessionModel.getData().size() > 0) {
                            Intent intent = new Intent(context, HomeActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            Intent intent = new Intent(context, SurveyActivity.class);
                            intent.putExtra("login", true);
                            startActivity(intent);
                            finishAffinity();
                        }
                    } else {
                        Intent intent = new Intent(context, PaymentInfoActivity.class);
                        intent.putExtra("login", true);
                        if (activeChatSessionModel.getData().size() > 0) {
                            intent.putExtra("isSessionActive", true);
                        } else {
                            intent.putExtra("isSessionActive", false);
                        }
                        startActivity(intent);
                    }
                } else {
                    AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.error), context.getString(R.string.wrong_with_backend), context.getString(R.string.OK), null);
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
