package com.nibou.niboucustomer.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
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
import com.nibou.niboucustomer.models.AccessTokenModel;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.utils.*;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class SignupActivity extends BaseActivity {
    private ActivitySignupBinding binding;
    private Context context;
    private String selctedDOB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });
        context = this;

        binding.aliasInfo.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.alias), getString(R.string.alias_message), getString(R.string.OK), null);
        });

        binding.eyePassword.setOnClickListener(v -> {
            if (binding.etPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.eyePassword.setImageResource(R.drawable.eye_icon);
            } else {
                binding.eyePassword.setImageResource(R.drawable.invisible_eye);
                binding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });

        binding.countryInfo.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.country), getString(R.string.country_message), getString(R.string.OK), null);
        });

//        binding.etCountry.setOnClickListener(v -> {
//            AppUtil.hideKeyBoard(context);
//            CountryPicker picker = CountryPicker.newInstance("Select Country");// dialog title
//            picker.setListener((name, code, dialCode, flagDrawableResID) -> {
//                ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//                binding.etCountry.setText(name);
//                picker.dismiss();
//            });
//            picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
//        });

        binding.etDob.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            showDatePicker(binding.etDob);
        });

        binding.btnSignup.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(SignupActivity.this);
            if (AppUtil.isInternetAvailable(context)) {
                if (screenValidate()) {
                    AppDialogs.getInstance().showConfirmCustomDialog(context, null, getString(R.string.sign_up_message), getString(R.string.CANCEL), getString(R.string.sign_up).toUpperCase(), new AppDialogs.DialogCallback() {
                        @Override
                        public void response(boolean status) {
                            if (status) {
                                if (AppUtil.isInternetAvailable(context)) {
                                    signupNetworkCall();
                                } else {
                                    AppUtil.showToast(context, getString(R.string.internet_error));
                                }
                            }
                        }
                    });
                }
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        });

        if (LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE).equals(AppConstant.ENGLISH)) {
            Typeface boldFont = ResourcesCompat.getFont(this, R.font.opensans_r);
            SpannableStringBuilder ss = new SpannableStringBuilder(getString(R.string.term_privacy_old));
            ss.setSpan(privacyclick, 102, 116, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new CustomTypefaceSpan("", boldFont), 102, 116, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.WHITE), 102, 116, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(termiclick, 120, getString(R.string.term_privacy_old).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(new CustomTypefaceSpan("", boldFont), 120, getString(R.string.term_privacy_old).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.WHITE), 120, getString(R.string.term_privacy_old).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            binding.privacy.setText(ss);
            binding.privacy.setMovementMethod(LinkMovementMethod.getInstance());
        } else if (LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE).equals(AppConstant.TURKISH)) {
            Typeface boldFont = ResourcesCompat.getFont(this, R.font.opensans_r);
            SpannableStringBuilder ss = new SpannableStringBuilder(getString(R.string.term_privacy_old));
            ss.setSpan(privacyclick, 72, 93, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new CustomTypefaceSpan("", boldFont), 72, 93, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.WHITE), 72, 93, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(termiclick, 96, getString(R.string.term_privacy_old).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(new CustomTypefaceSpan("", boldFont), 96, getString(R.string.term_privacy_old).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.WHITE), 96, getString(R.string.term_privacy_old).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            binding.privacy.setText(ss);
            binding.privacy.setMovementMethod(LinkMovementMethod.getInstance());
        } else if (LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE).equals(AppConstant.ARABIC)) {
            Typeface boldFont = ResourcesCompat.getFont(this, R.font.opensans_r);
            SpannableStringBuilder ss = new SpannableStringBuilder(getString(R.string.term_privacy_old));
            ss.setSpan(privacyclick, 79, 94, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new CustomTypefaceSpan("", boldFont), 79, 94, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.WHITE), 79, 94, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(termiclick, 97, getString(R.string.term_privacy_old).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(new CustomTypefaceSpan("", boldFont), 97, getString(R.string.term_privacy_old).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.WHITE), 97, getString(R.string.term_privacy_old).length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            binding.privacy.setText(ss);
            binding.privacy.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }


    private void signupNetworkCall() {
        HashMap<String, Object> mainParameters = new HashMap<>();
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("name", binding.etName.getText().toString());
        parameters.put("username", binding.etAlias.getText().toString());
        parameters.put("email", binding.etEmail.getText().toString());
        parameters.put("password", binding.etPassword.getText().toString());
        parameters.put("country", binding.etCountry.getText().toString());
        parameters.put("dob", selctedDOB);
        parameters.put("account_type", "1");
        parameters.put("lang", LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE));
        mainParameters.put("user", parameters);


        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).signup(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), mainParameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    // accessTokenNetworkCall();
                    Intent intent = new Intent(context, SignupSuccessActivity.class);
                    intent.putExtra(AppConstant.EMAIL, binding.etEmail.getText().toString());
                    intent.putExtra(AppConstant.PASSWORD, binding.etPassword.getText().toString());
                    startActivity(intent);
                    finishAffinity();
                } else {
                    AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.alert), context.getString(R.string.already_registered_message), context.getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }

    private void accessTokenNetworkCall() {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getAccessToken(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.CLIENT_ID, AppConstant.CLIENT_SECRET, AppConstant.PASSWORD, binding.etEmail.getText().toString(), binding.etPassword.getText().toString(), "1"), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    profileNetworkCall((AccessTokenModel) data);
                    uploadDeviceToken(((AccessTokenModel) data).getAccessToken());
                } else {
                    AppUtil.showToast(context, context.getResources().getString(R.string.wrong_with_backend));
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
                    Intent intent = new Intent(context, SignupSuccessActivity.class);
                    intent.putExtra(AppConstant.EMAIL, binding.etEmail.getText().toString());
                    intent.putExtra(AppConstant.PASSWORD, binding.etPassword.getText().toString());
                    startActivity(intent);
                    finishAffinity();
                } else {
                    AppUtil.showToast(context, context.getResources().getString(R.string.wrong_with_backend));
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


    private void showDatePicker(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, R.style.CustomDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                editText.setText(DateFormatUtil.getRequiredDOBFormat(calendar.getTime()));
                selctedDOB = DateFormatUtil.getRequiredFormatDate(calendar.getTime());
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dialog.getDatePicker().setMaxDate(get18yearsBackDate());
        dialog.show();
    }

    private long get18yearsBackDate() {
        final Calendar calendar = Calendar.getInstance();

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        int minYear = currentYear - 18;
        int minMonth = currentMonth;
        int minDay = currentDay;

        calendar.set(minYear, minMonth, minDay);
        return calendar.getTimeInMillis();
    }

    ClickableSpan privacyclick = new ClickableSpan() {
        @Override
        public void onClick(View textView) {
            AppUtil.hideKeyBoard(context);
            Intent intent = new Intent(context, PrivacyPolicyActivity.class);
            intent.putExtra("type", "privacy");
            startActivity(intent);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    };

    ClickableSpan termiclick = new ClickableSpan() {
        @Override
        public void onClick(View textView) {
            AppUtil.hideKeyBoard(context);
            Intent intent = new Intent(context, PrivacyPolicyActivity.class);
            intent.putExtra("type", "terms");
            startActivity(intent);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    };

    private boolean screenValidate() {
        if (TextUtils.isEmpty(binding.etName.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.name_empty_alert));
            return false;
        } else if (TextUtils.isEmpty(binding.etAlias.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.alias_empty_alert));
            return false;
        } else if (TextUtils.isEmpty(binding.etEmail.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.email_empty_alert));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.getText().toString()).matches()) {
            AppUtil.showToast(this, getResources().getString(R.string.email_invalid_alert));
            return false;
        } else if (TextUtils.isEmpty(binding.etPassword.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.password_empty_alert));
            return false;
        } else if (binding.etPassword.getText().toString().length() < 6) {
            AppUtil.showToast(this, getResources().getString(R.string.pwd_length_alert));
            return false;
        } else if (TextUtils.isEmpty(binding.etCountry.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.country_empty_alert));
            return false;
        } else if (TextUtils.isEmpty(binding.etDob.getText())) {
            AppUtil.showToast(this, getResources().getString(R.string.dob_empty_alert));
            return false;
        }
        return true;
    }
}
