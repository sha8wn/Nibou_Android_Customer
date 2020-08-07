package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.databinding.ActivityPaymentCardBinding;
import com.nibou.niboucustomer.databinding.ActivityPaymentInfoBinding;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;

public class PaymentInfoActivity extends BaseActivity {

    private ActivityPaymentInfoBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_info);
        context = this;

        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.app_black_color_code), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });

        if (getIntent().hasExtra("login")) {
            binding.toolbar.findViewById(R.id.back_arrow).setVisibility(View.GONE);
        }

        binding.btnAddCard.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            Intent intent = new Intent(context, PricingAndTermsActivity.class);
            intent.putExtras(getIntent().getExtras());
            intent.putExtra(AppConstant.NEW_TERM_CONDITIONS_SCREEN, true);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        if (getIntent().hasExtra("login")) {
        } else {
            super.onBackPressed();
        }
    }
}
