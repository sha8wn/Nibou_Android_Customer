package com.nibou.niboucustomer.activitys;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.databinding.ActivityUserCheckBinding;

public class UserCheckActivity extends BaseActivity {
    private ActivityUserCheckBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_check);
        binding.oldUser.setOnClickListener(v -> {
            Intent intent = new Intent(UserCheckActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        binding.newUser.setOnClickListener(v -> {
            Intent intent = new Intent(UserCheckActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }
}
