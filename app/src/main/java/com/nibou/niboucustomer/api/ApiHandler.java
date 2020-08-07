package com.nibou.niboucustomer.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.activitys.LoginActivity;
import com.nibou.niboucustomer.models.AccessTokenModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ApiHandler {

    public interface CallBack {
        void success(boolean isSucess, Object data);

        void failed();
    }

    public static <T> void requestService(Context context, final Call<T> requestCall, CallBack callBack) {
        requestCall.clone().enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response != null && (response.code() == 200 || response.code() == 201)) { //success
                    if (callBack != null) {
                        callBack.success(true, response.body());
                    }
                } else if (response != null && response.code() == 401) { // unauthorize or access token expire
                    if (LocalPrefences.getInstance().getLocalAccessTokenModel(context) != null && LocalPrefences.getInstance().getLocalAccessTokenModel(context).getRefreshToken() != null)
                        requestRefreshTokenService(context, callBack, requestCall, ApiClient.getClient().create(ApiEndPoint.class).getRefreshAccessToken(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.CLIENT_ID, AppConstant.CLIENT_SECRET, AppConstant.REFRESH_TOKEN, LocalPrefences.getInstance().getLocalAccessTokenModel(context).getRefreshToken()));
                    else {
                        if (callBack != null) {
                            AppUtil.showToast(context, context.getResources().getString(R.string.wrong_with_backend));
                            callBack.failed();
                        }
                    }
                } else {
                    if (callBack != null) {
                        try {
                            callBack.success(false, response.errorBody().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                            callBack.success(false, null);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                if (callBack != null) {
                    if (t instanceof TimeoutException) {
                        AppUtil.showToast(context, context.getResources().getString(R.string.timeout));
                    } else {
                        AppUtil.showToast(context, context.getResources().getString(R.string.server_faliure));
                    }
                    callBack.failed();
                }
            }
        });
    }

    public static <T> void requestRefreshTokenService(Context context, CallBack callBack, Call lastApiCall, Call<T> call) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response != null && (response.code() == 200 || response.code() == 201)) { //success
                    LocalPrefences.getInstance().saveLocalAccessTokenModel(context, (AccessTokenModel) response.body());
                    requestService(context, lastApiCall, callBack);
                } else if (response != null && response.code() == 400) { //success
                    AppUtil.showToast(context, context.getResources().getString(R.string.refresh_token_error_alert));
                    logout(context);
                } else {
                    if (callBack != null) {
                        AppUtil.showToast(context, context.getResources().getString(R.string.wrong_with_backend));
                        callBack.failed();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                if (t instanceof TimeoutException) {
                    AppUtil.showToast(context, context.getResources().getString(R.string.timeout));
                } else {
                    AppUtil.showToast(context, context.getResources().getString(R.string.server_faliure));
                }
                if (callBack != null) {
                    callBack.failed();
                }
            }
        });
    }

    private static void logout(Context context) {
        String language = LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE);
        boolean firstLaunch = LocalPrefences.getInstance().getBoolean(context, AppConstant.IS_FIRST_LAUNCH_SUCCESS);
        LocalPrefences.getInstance().clearSharePreference(context);
        LocalPrefences.getInstance().putString(context, AppConstant.APP_LANGUAGE, language);
        LocalPrefences.getInstance().putBoolean(context, AppConstant.IS_FIRST_LAUNCH_SUCCESS, firstLaunch);
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((Activity) context).finishAffinity();
    }

}