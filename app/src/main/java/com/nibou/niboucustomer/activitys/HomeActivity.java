package com.nibou.niboucustomer.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.databinding.ActivityHomeBinding;
import com.nibou.niboucustomer.databinding.ActivityLoginBinding;
import com.nibou.niboucustomer.fragments.MessageHomeFragment;
import com.nibou.niboucustomer.fragments.PagerFragment;
import com.nibou.niboucustomer.fragments.SettingFragment;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

import java.util.Locale;

public class HomeActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private long backPressedClickTime;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this);
        if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            binding.bottomNavigationView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        } else {
            binding.bottomNavigationView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        if (LocalPrefences.getInstance().getBoolean(this, AppConstant.MENU_LANGUAGE_CHANGE)) {
            LocalPrefences.getInstance().putBoolean(this, AppConstant.MENU_LANGUAGE_CHANGE, false);
            binding.bottomNavigationView.setSelectedItemId(R.id.action_setting);
        } else {
            binding.bottomNavigationView.setSelectedItemId(R.id.action_messgae);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_messgae:
                if (getSupportFragmentManager().findFragmentByTag("messageFragment") == null)
                    AppUtil.setFragment(R.id.frameLayout_container, getSupportFragmentManager(), new MessageHomeFragment(), "messageFragment", false, true);
                break;

            case R.id.action_setting:
                if (getSupportFragmentManager().findFragmentByTag("settingFragment") == null)
                    AppUtil.setFragment(R.id.frameLayout_container, getSupportFragmentManager(), new SettingFragment(), "settingFragment", false, true);
                break;

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.LANGUAGE_SCREEN_CODE && data != null) {
            LocalPrefences.getInstance().putBoolean(this, AppConstant.MENU_LANGUAGE_CHANGE, true);
            startActivity(new Intent(this, HomeActivity.class));
            finishAffinity();
        }
    }

    @Override
    public void onBackPressed() {
        checkForAppExit();
    }

    private void checkForAppExit() {
        if ((backPressedClickTime + 2000) > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, getResources().getString(R.string.press_back_message), Toast.LENGTH_SHORT).show();
        }
        backPressedClickTime = System.currentTimeMillis();
    }
}
