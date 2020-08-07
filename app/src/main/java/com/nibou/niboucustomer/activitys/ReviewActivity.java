package com.nibou.niboucustomer.activitys;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityPdfviewBinding;
import com.nibou.niboucustomer.databinding.ActivityReviewBinding;
import com.nibou.niboucustomer.models.ActiveChatSessionModel;
import com.nibou.niboucustomer.models.AppSessionModel;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.models.User;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.ChatConstants;
import com.nibou.niboucustomer.utils.LocalPrefences;

import java.util.HashMap;

public class ReviewActivity extends BaseActivity {
    private ActivityReviewBinding binding;
    private Context context;
    private ProfileModel profileModel;
    private AppSessionModel appSessionModel;
    private User userModel;
    private String room_id;
    private String expert_id;
    private String expert_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review);
        context = this;

        Intent intent1 = getIntent();
        room_id = intent1.getStringExtra("room_id");
        expert_id = intent1.getStringExtra("expert_id");
        expert_name = intent1.getStringExtra("expert_name");
        binding.subTitle.setText(expert_name);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Integer.parseInt(expert_id));

        binding.skip.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            startActivity(new Intent(context, HomeActivity.class));
            finishAffinity();
        });

        binding.btnAdd.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            if (AppUtil.isInternetAvailable(context)) {
                if (screenValidate()) {
                    sendReviewNetworkCall();
                }
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        });
    }

    private void sendReviewNetworkCall() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("value", String.valueOf((int) binding.star.getRating()));
        parameters.put("room_id", room_id);
        parameters.put("expert_id", expert_id);
        parameters.put("comment", binding.etDescription.getText().toString().trim());

        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).sendReview(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    Intent intent = new Intent(context, ReviewSuccessActivity.class);
                    if (getIntent().getExtras() != null) {
                        intent.putExtras(getIntent().getExtras());
                    }
                    startActivity(intent);
                    finish();
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
        if (binding.star.getRating() == 0) {
            AppUtil.showToast(this, getResources().getString(R.string.feedback_star_alert));
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

    }
}
