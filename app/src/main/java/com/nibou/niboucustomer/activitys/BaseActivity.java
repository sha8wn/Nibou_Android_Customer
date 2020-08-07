package com.nibou.niboucustomer.activitys;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;


public class BaseActivity extends AppCompatActivity {

    String room_id;
    String name;
    String expert_id;
    String time = "5";

    private Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // LocalPrefences.getInstance().putString(this, AppConstant.APP_LANGUAGE, AppConstant.TURKISH);
        if (LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE) != null) {
            setLocale(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE));
        }
        super.onCreate(savedInstanceState);
    }

    public void setLocale(String localeString) {
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        Locale locale = new Locale(localeString);
        Locale.setDefault(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(locale);
            getApplicationContext().createConfigurationContext(conf);
        }
        DisplayMetrics dm = res.getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            conf.setLocales(new LocaleList(locale));
        } else {
            conf.locale = locale;
        }
        res.updateConfiguration(conf, dm);
    }


    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastOfflineReceiver, new IntentFilter(AppConstant.TIMEOUT_RECIEVER));
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(broadcastOfflineReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver broadcastOfflineReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent != null) {
                    name = intent.getStringExtra("name");
                    expert_id = intent.getStringExtra("expert_id");
                    room_id = intent.getStringExtra("room_id");
                    if (intent.hasExtra("time"))
                        time = intent.getStringExtra("time");
                    if (intent.hasExtra(AppConstant.SESSION_TIMEOUT_ACTION)) {
                        runOnUiThread(() -> {
                            if (!isFinishing()) {
                                try {
                                    if (LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE) != null && LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE).equals(AppConstant.TURKISH)) {
                                        showSessionCustomDialog(BaseActivity.this, getResources().getString(R.string.inactive_conversation).toUpperCase(), getResources().getString(R.string.inactive_conversation_message, name, time), room_id, null);
                                    } else {
                                        showSessionCustomDialog(BaseActivity.this, getResources().getString(R.string.inactive_conversation).toUpperCase(), getResources().getString(R.string.inactive_conversation_message, time, name), room_id, null);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                        Intent notificationIntent = new Intent(context, ReviewActivity.class);
                        notificationIntent.putExtra("room_id", room_id);
                        notificationIntent.putExtra("expert_id", expert_id);
                        notificationIntent.putExtra("expert_name", name);
                        startActivity(notificationIntent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void showSessionCustomDialog(final Context context, String title, String message, String roomID, final AppDialogs.DialogCallback dialogCallback) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        AppUtil.showLog("Push", "Inactive2");
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_session);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.findViewById(R.id.message).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.edittext).setVisibility(View.GONE);
        dialog.findViewById(R.id.button1).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.button2).setVisibility(View.VISIBLE);

        TextView titletext = dialog.findViewById(R.id.title);
        titletext.setText(title);
        TextView messagetext = dialog.findViewById(R.id.message);
        messagetext.setText(message);
        TextView end = dialog.findViewById(R.id.button1);
        TextView continu = dialog.findViewById(R.id.button2);
        end.setOnClickListener(v -> {
            dialog.dismiss();
            try {
                if (AppUtil.isInternetAvailable(context)) {
                    callEndApi(context, roomID);
                } else {
                    AppUtil.showToast(context, context.getString(R.string.internet_error));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        continu.setOnClickListener(v -> {
            dialog.dismiss();
            try {
                if (AppUtil.isInternetAvailable(context)) {
                    sendMessageNetworkCall(context, roomID, RequestBody.create(MediaType.parse("text/plain"), "END_SESSION"), null);
                } else {
                    AppUtil.showToast(context, context.getString(R.string.internet_error));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        dialog.show();
    }

    public void callEndApi(Context context, String roomID) {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).endSession(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), roomID), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object result) {
                if (isSuccess) {
                    Log.e("end", "success");
                    try {
                        Intent intent = new Intent(AppConstant.NEW_MESSAGE_RECIEVER);
                        context.sendBroadcast(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("end", "fail");
                }
            }

            @Override
            public void failed() {
                Log.e("end", "onFailure");
            }
        });

    }

    public void sendMessageNetworkCall(Context context, String room_id, RequestBody text, List<MultipartBody.Part> images) {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).sendMessage(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), room_id, text, null), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    Log.e("continue", "success");
                } else {
                    Log.e("continue", "fail");
                }
            }

            @Override
            public void failed() {
                Log.e("continue", "onFailure");
            }
        });
    }

    public boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;
    }

}
