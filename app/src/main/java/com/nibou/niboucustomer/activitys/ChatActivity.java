package com.nibou.niboucustomer.activitys;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.actioncable.ActionCableHandler;
import com.nibou.niboucustomer.adapters.ExpertProfileAdapter;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityChatBinding;
import com.nibou.niboucustomer.databinding.ActivityPrivacyPolicyBinding;
import com.nibou.niboucustomer.fragments.ChatFragment;
import com.nibou.niboucustomer.fragments.MessageHomeFragment;
import com.nibou.niboucustomer.models.ActiveChatSessionModel;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.utils.*;

import java.util.Collections;
import java.util.HashSet;

public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;
    private Context context;

    private Dialog dialog;
    private Dialog inactiveDialog;
    private ProfileModel profileModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(broadcastOfflineReceiver, new IntentFilter(AppConstant.ONLINE_OFFLINE_RECIEVER));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        context = this;
        binding.toolbar.findViewById(R.id.title).setVisibility(View.VISIBLE);
        binding.toolbar.findViewById(R.id.menu_option).setVisibility(View.VISIBLE);
        binding.toolbar.findViewById(R.id.image_parent).setVisibility(View.VISIBLE);
        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.app_black_color_code), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });

        if (getIntent().hasExtra(AppConstant.EXPERT_DETAILS_MODEL)) {
            ActiveChatSessionModel.Data data = (ActiveChatSessionModel.Data) getIntent().getSerializableExtra(AppConstant.EXPERT_DETAILS_MODEL);
            for (int i = 0; i < data.getAttributes().getUsers().size(); i++) {
                if (!(data.getAttributes().getUsers().get(i).getData().getId().equals(LocalPrefences.getInstance().getLocalProfileModel(context).getData().getId()))) {
                    profileModel = data.getAttributes().getUsers().get(i);
                    break;
                }
            }
            if (profileModel.getData().getAttributes().getAvatar() != null && profileModel.getData().getAttributes().getAvatar().getUrl() != null)
                showImage(AppConstant.FILE_BASE_URL + profileModel.getData().getAttributes().getAvatar().getUrl());
            ((TextView) binding.toolbar.findViewById(R.id.title)).setText(profileModel.getData().getAttributes().getName());
        }

        binding.toolbar.findViewById(R.id.menu_option).setOnClickListener(v -> showMenuDialog());
        binding.toolbar.findViewById(R.id.title).setOnClickListener(v -> moveToExpertScreen());
        binding.toolbar.findViewById(R.id.profile_image).setOnClickListener(v -> moveToExpertScreen());

        AppUtil.setFragment(R.id.frameLayout_container, getSupportFragmentManager(), new ChatFragment(), "chatFragment", false, true);


        //show inactive dialog
        HashSet<String> hashSet = LocalPrefences.getInstance().getLocalStatusOfExpert(context);
        if (hashSet != null && hashSet.contains(profileModel.getData().getId())) {
            showInactiveDialog();
        }
    }

    private void showImage(String url) {
        Glide.with(context)
                .load(url)
                .dontAnimate()
                .placeholder(R.drawable.default_placeholder)
                .error(R.drawable.default_placeholder)
                .into((de.hdodenhof.circleimageview.CircleImageView) binding.toolbar.findViewById(R.id.profile_image));
    }

    private void moveToExpertScreen() {
        Intent intent = new Intent(context, ExpertActivity.class);
        intent.putExtra(AppConstant.VIEW, true);
        if (profileModel != null)
            intent.putExtra(AppConstant.PROFILE_MODEL, profileModel);
        startActivity(intent);
    }

    public void showMenuDialog() {
        dialog = new Dialog(context, R.style.WhiteAppTheme);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_menu_custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();

        dialog.findViewById(R.id.view_expert).setOnClickListener(v -> {
            moveToExpertScreen();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.switch_expert).setOnClickListener(v -> {
            Intent intent = new Intent(context, SurveyPreferenceActivity.class);
            intent.putExtra(AppConstant.NEW_SWITCH, true);
            if (profileModel != null) {
                intent.putExtra(ChatConstants.ROOM_ID, getIntent().getStringExtra(ChatConstants.ROOM_ID));
                intent.putExtra(AppConstant.PROFILE_MODEL, profileModel);
                intent.putExtra(AppConstant.SURVEY_MODEL, getIntent().getSerializableExtra(AppConstant.SURVEY_MODEL));
            }
            startActivity(intent);
            dialog.dismiss();
        });

        dialog.findViewById(R.id.end_conversation).setOnClickListener(v -> {
            dialog.dismiss();
            if (AppUtil.isInternetAvailable(context)) {
                if (getIntent().getStringExtra(ChatConstants.ROOM_ID) != null)
                    endConversationNetworkCall(context, getIntent().getStringExtra(ChatConstants.ROOM_ID));
            } else {
                AppUtil.showToast(context, context.getString(R.string.internet_error));
            }
        });

        dialog.findViewById(R.id.bg).setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Override
    public void onBackPressed() {
        if (getIntent().hasExtra(AppConstant.IS_SWITCH_DONE)) {
            Intent intent = new Intent(context, HomeActivity.class);
            startActivity(intent);
            finishAffinity();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                if (binding != null) {
                    if (intent.getStringExtra("id").equals(profileModel.getData().getId())) {
                        HashSet<String> hashSet = LocalPrefences.getInstance().getLocalStatusOfExpert(context);
                        if (hashSet != null && hashSet.contains(profileModel.getData().getId())) {
                            showInactiveDialog();
                        } else {
                            binding.toolbar.findViewById(R.id.online_sign).setBackground(ContextCompat.getDrawable(context, R.drawable.online_small_circle_drawble));
                            if (inactiveDialog != null && inactiveDialog.isShowing()) {
                                inactiveDialog.dismiss();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void showInactiveDialog() {
        if (binding != null)
            binding.toolbar.findViewById(R.id.online_sign).setBackground(ContextCompat.getDrawable(context, R.drawable.online_small_grey_circle_drawble));
        if (AppUtil.isInternetAvailable(context)) {
            if (LocalPrefences.getInstance().getLocalAccessTokenModel(context) != null)
                timingNetworkCall();
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void timingNetworkCall() {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getExpertDetails(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), profileModel.getData().getId(), "user_timings"), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess && !ChatActivity.this.isFinishing() && binding != null) {
                    ProfileModel profileModel = (ProfileModel) data;
                    if (profileModel.getIncluded() != null && profileModel.getIncluded().size() > 0) {
                        Collections.sort(profileModel.getIncluded());
                        ProfileModel.Data profileData = getNextDayTimingModel(profileModel);
                        showAlertDialog(DateFormatUtil.getDay(context, profileData.getAttributes().getDay_number()), profileData.getAttributes().getTime_from(), profileData.getAttributes().getTime_to());
                    }
                }
            }

            @Override
            public void failed() {
            }
        });
    }

    public void endConversationNetworkCall(Context context, String roomID) {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).endSession(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), roomID), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object result) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    try {
                        Intent intent = new Intent(AppConstant.NEW_MESSAGE_RECIEVER);
                        context.sendBroadcast(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    private ProfileModel.Data getNextDayTimingModel(ProfileModel profileModel) {
        int todayNumber = DateFormatUtil.getCurrentDayNumber();
        for (int i = 0; i < profileModel.getIncluded().size(); i++) {
            if (profileModel.getIncluded().get(i).getAttributes().getDay_number() > todayNumber) {
                return profileModel.getIncluded().get(i);
            }
        }
        return profileModel.getIncluded().get(0);
    }

    private void showAlertDialog(String day, String startTime, String endTime) {
        showInactiveDialog(context, getString(R.string.expert_offline).toUpperCase(), profileModel.getData().getAttributes().getName() + " " + getString(R.string.expert_offline_message1) + " " + day + " " + getString(R.string.between) + " " + startTime + " " + getString(R.string.to) + " " + endTime + ". " + getString(R.string.expert_offline_message2), getString(R.string.switch_expert).toUpperCase(), getString(R.string.leave_message).toUpperCase(), status -> {
            if (!status) {
                Intent intent = new Intent(context, SurveyPreferenceActivity.class);
                intent.putExtra(AppConstant.NEW_SWITCH, true);
                if (profileModel != null) {
                    intent.putExtra(ChatConstants.ROOM_ID, getIntent().getStringExtra(ChatConstants.ROOM_ID));
                    intent.putExtra(AppConstant.PROFILE_MODEL, profileModel);
                    intent.putExtra(AppConstant.SURVEY_MODEL, getIntent().getSerializableExtra(AppConstant.SURVEY_MODEL));
                }
                startActivity(intent);
            }
        });
    }

    public void showInactiveDialog(final Context context, String title, String message, String b1, String b2, final AppDialogs.DialogCallback dialogCallback) {
        if (inactiveDialog != null && inactiveDialog.isShowing()) {
            return;
        }
        inactiveDialog = new Dialog(context);
        inactiveDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        inactiveDialog.setContentView(R.layout.dialog_custom);
        inactiveDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        inactiveDialog.setCancelable(false);
        inactiveDialog.setCanceledOnTouchOutside(false);
        Window window = inactiveDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        inactiveDialog.findViewById(R.id.message).setVisibility(View.VISIBLE);
        inactiveDialog.findViewById(R.id.edittext).setVisibility(View.GONE);
        inactiveDialog.findViewById(R.id.button).setVisibility(View.GONE);
        inactiveDialog.findViewById(R.id.button1).setVisibility(View.VISIBLE);
        inactiveDialog.findViewById(R.id.button2).setVisibility(View.VISIBLE);

        TextView titletext = inactiveDialog.findViewById(R.id.title);
        titletext.setText(title);
        if (title == null)
            titletext.setVisibility(View.INVISIBLE);

        TextView messagetext = inactiveDialog.findViewById(R.id.message);
        messagetext.setText(message);

        TextView button1 = inactiveDialog.findViewById(R.id.button1);
        button1.setText(b1);
        button1.setOnClickListener(v -> {
            inactiveDialog.dismiss();
            if (dialogCallback != null) {
                dialogCallback.response(false);
            }
        });

        TextView button2 = inactiveDialog.findViewById(R.id.button2);
        button2.setText(b2);
        button2.setOnClickListener(v -> {
            inactiveDialog.dismiss();
            if (dialogCallback != null) {
                dialogCallback.response(true);
            }
        });
        inactiveDialog.show();
    }
}
