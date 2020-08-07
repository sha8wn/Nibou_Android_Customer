package com.nibou.niboucustomer.activitys;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.*;
import android.widget.ImageView;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.databinding.ActivityProgressbarBinding;
import com.nibou.niboucustomer.utils.AppConstant;

public class ExpertSearchProgressActivity extends BaseActivity {
    private ActivityProgressbarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_progressbar);
    }

    @Override
    public void onBackPressed() {
    }
}
