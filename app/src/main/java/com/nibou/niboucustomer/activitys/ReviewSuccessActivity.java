package com.nibou.niboucustomer.activitys;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.databinding.ActivityReviewBinding;
import com.nibou.niboucustomer.databinding.ActivityReviewSuccessBinding;
import com.nibou.niboucustomer.utils.AppConstant;

public class ReviewSuccessActivity extends BaseActivity {
    private ActivityReviewSuccessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review_success);

        binding.btnOk.setOnClickListener(v -> {
            startActivity(new Intent(ReviewSuccessActivity.this, HomeActivity.class));
            finishAffinity();
        });
    }

    @Override
    public void onBackPressed() {
    }
}
