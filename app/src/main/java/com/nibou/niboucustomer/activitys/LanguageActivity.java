package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.databinding.ActivityLanguageBinding;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

import java.util.Locale;

public class LanguageActivity extends BaseActivity {

    private ActivityLanguageBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> onBackPressed());
        context = this;
        if (getIntent().hasExtra(AppConstant.VIEW)) {
            binding.toolbar.findViewById(R.id.back_arrow).setVisibility(View.VISIBLE);
        } else {
            binding.toolbar.findViewById(R.id.back_arrow).setVisibility(View.INVISIBLE);
        }

        binding.english.setOnClickListener(v -> moveToNextScreen(AppConstant.ENGLISH));
        binding.turkish.setOnClickListener(v -> moveToNextScreen(AppConstant.TURKISH));
        binding.arabic.setOnClickListener(v -> moveToNextScreen(AppConstant.ARABIC));
    }

    private void moveToNextScreen(String lang) {
        LocalPrefences.getInstance().putString(context, AppConstant.APP_LANGUAGE, lang);
        setLocale(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE));

        if (getIntent().hasExtra(AppConstant.VIEW)) {
            Intent i = new Intent();
            setResult(RESULT_OK, i);
            onBackPressed();
        } else {
            if (LocalPrefences.getInstance().isUserLogin(context)) {
                if (getIntent().getBooleanExtra("isCard", false)) {
                    if (getIntent().getBooleanExtra("isSessionActive", false)) {
                        Intent intent = new Intent(context, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, SurveyActivity.class);
                        intent.putExtra("login", true);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(context, PaymentInfoActivity.class);
                    intent.putExtra("login", true);
                    startActivity(intent);
                }
            } else {
                if (LocalPrefences.getInstance().getBoolean(context, AppConstant.IS_FIRST_LAUNCH_SUCCESS)) {
                    Intent intent = new Intent(context, UserCheckActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, SliderActivity.class);
                    startActivity(intent);
                }
            }
            finish();
        }
    }
}
