package com.nibou.niboucustomer.activitys;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.models.ActiveChatSessionModel;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.models.SessionTimeoutModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.DateFormatUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class SplashActivity extends BaseActivity {

    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        if (getIntent() != null && getIntent().hasExtra(AppConstant.ROOM_ID)) {
            if (AppUtil.isInternetAvailable(this)) {
                sendMessageNetworkCall(this, getIntent().getStringExtra(AppConstant.ROOM_ID), RequestBody.create(MediaType.parse("text/plain"), "END_SESSION"), null);
            } else {
                AppUtil.showToast(this, getString(R.string.internet_error));
            }
        }

        if (AppUtil.isInternetAvailable(this) && LocalPrefences.getInstance().isUserLogin(this) && isTimeZoneChanged()) {
            updateProfileNetworkCall();
        }

        if (LocalPrefences.getInstance().isUserLogin(this)) {
            if (AppUtil.isInternetAvailable(this)) {
                checkUserData();
            } else {
                AppUtil.showToast(this, getString(R.string.internet_error));
            }
        } else {
            init();
        }
    }

    private void checkUserData() {

        new Thread(() -> {
            if (AppUtil.isInternetAvailable(SplashActivity.this)) {
                getCardListNetworkCall();
            }
        }).start();

    }

    private void showErrorAlert() {
        AppDialogs.getInstance().showInfoCustomDialog(SplashActivity.this, getString(R.string.system_error).toUpperCase(), getString(R.string.system_error_message), getString(R.string.try_again).toUpperCase(), status -> checkUserData());
    }

    private void getCardListNetworkCall() {
        ApiHandler.requestService(this, ApiClient.getClient().create(ApiEndPoint.class).getMyProfile(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(this).getAccessToken(), "user_credit_cards"), new ApiHandler.CallBack() {
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
                    getChatSessionNetworkCall(isCard);
                } else {
                    showErrorAlert();
                }
            }

            @Override
            public void failed() {
                showErrorAlert();
            }
        });
    }

    private void getChatSessionNetworkCall(boolean isCard) {
        ApiHandler.requestService(this, ApiClient.getClient().create(ApiEndPoint.class).getActiveChatSession(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(this).getAccessToken()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    ActiveChatSessionModel activeChatSessionModel = (ActiveChatSessionModel) data;
                    if (activeChatSessionModel.getData().size() > 0) {
                        checkDeviceLanguage(true, isCard);
                    } else {
                        checkDeviceLanguage(false, isCard);
                    }
                } else {
                    showErrorAlert();
                }
            }

            @Override
            public void failed() {
                showErrorAlert();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    private void init() {
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }

    private Runnable runnable = () -> checkDeviceLanguage(false, false);


    private void checkDeviceLanguage(boolean isSessionActive, boolean isCard) {
        if (LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE) == null) {
            if (Locale.getDefault().getLanguage().equals(AppConstant.ENGLISH)) {
                LocalPrefences.getInstance().putString(this, AppConstant.APP_LANGUAGE, AppConstant.ENGLISH);
                setLocale(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE));
                moveToNextScreen(isSessionActive, isCard);
            } else if (Locale.getDefault().getLanguage().equals(AppConstant.TURKISH)) {
                LocalPrefences.getInstance().putString(this, AppConstant.APP_LANGUAGE, AppConstant.TURKISH);
                setLocale(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE));
                moveToNextScreen(isSessionActive, isCard);
            } else if (Locale.getDefault().getLanguage().equals(AppConstant.ARABIC)) {
                LocalPrefences.getInstance().putString(this, AppConstant.APP_LANGUAGE, AppConstant.ARABIC);
                setLocale(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE));
                moveToNextScreen(isSessionActive, isCard);
            } else {
                Intent intent = new Intent(SplashActivity.this, LanguageActivity.class);
                intent.putExtra("isSessionActive", isSessionActive);
                intent.putExtra("isCard", isCard);
                startActivity(intent);
                finish();
            }
        } else if (LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE).equals(Locale.getDefault().getLanguage())) {
            //no need to show language screen
            setLocale(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE));
            moveToNextScreen(isSessionActive, isCard);
        } else {
            if (Locale.getDefault().getLanguage().equals(AppConstant.ENGLISH)) {
                LocalPrefences.getInstance().putString(this, AppConstant.APP_LANGUAGE, AppConstant.ENGLISH);
                setLocale(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE));
            } else if (Locale.getDefault().getLanguage().equals(AppConstant.TURKISH)) {
                LocalPrefences.getInstance().putString(this, AppConstant.APP_LANGUAGE, AppConstant.TURKISH);
                setLocale(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE));
            } else if (Locale.getDefault().getLanguage().equals(AppConstant.ARABIC)) {
                LocalPrefences.getInstance().putString(this, AppConstant.APP_LANGUAGE, AppConstant.ARABIC);
                setLocale(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE));
            } else {
                setLocale(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE));
            }
            moveToNextScreen(isSessionActive, isCard);
        }
    }


    private void moveToNextScreen(boolean isSessionActive, boolean isCard) {
        if (LocalPrefences.getInstance().isUserLogin(this)) {
            if (isCard) {
                if (isSessionActive) {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashActivity.this, SurveyActivity.class);
                    intent.putExtra("login", true);
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(SplashActivity.this, PaymentInfoActivity.class);
                intent.putExtra("login", true);
                intent.putExtra("isSessionActive", isSessionActive);
                startActivity(intent);
            }
        } else {
            if (LocalPrefences.getInstance().getBoolean(this, AppConstant.IS_FIRST_LAUNCH_SUCCESS)) {
                Intent intent = new Intent(SplashActivity.this, UserCheckActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(SplashActivity.this, SliderActivity.class);
                startActivity(intent);
            }
        }
        finish();
    }

    private boolean isTimeZoneChanged() {
        if (LocalPrefences.getInstance().getString(this, AppConstant.TIMEZONE) != null && TimeZone.getDefault().getID().equals(LocalPrefences.getInstance().getString(this, AppConstant.TIMEZONE))) {
            return false;
        } else {
            return true;
        }
    }

    private void updateProfileNetworkCall() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("timezone", TimeZone.getDefault().getID());
        ApiHandler.requestService(this, ApiClient.getClient().create(ApiEndPoint.class).updateMyProfile(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(this).getAccessToken(), parameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    LocalPrefences.getInstance().putString(SplashActivity.this, AppConstant.TIMEZONE, TimeZone.getDefault().getID());
                }
            }

            @Override
            public void failed() {
            }
        });
    }
}

