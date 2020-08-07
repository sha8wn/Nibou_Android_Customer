package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.*;
import android.widget.ImageView;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.databinding.ActivityPdfviewBinding;
import com.nibou.niboucustomer.databinding.ActivityPrivacyPolicyBinding;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;

public class PDFViewActivity extends BaseActivity {
    private ActivityPdfviewBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pdfview);
        context = this;
        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.app_black_color_code), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });

        setWebViewSetting();
        setWebViewContent();
        setWebViewListener();
    }

    private void setWebViewSetting() {
        binding.webview.setBackgroundColor(ContextCompat.getColor(context, R.color.webview_bg_color));
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.setHorizontalScrollBarEnabled(false);
        binding.webview.getSettings().setSupportZoom(true);
        binding.webview.getSettings().setBuiltInZoomControls(true);
        binding.webview.getSettings().setDisplayZoomControls(false);
        binding.webview.getSettings().setLoadWithOverviewMode(true);
        binding.webview.getSettings().setMediaPlaybackRequiresUserGesture(false);
        binding.webview.getSettings().setUseWideViewPort(true);
        binding.webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        binding.webview.clearCache(true);
        binding.webview.clearHistory();
    }

    private void setWebViewContent() {
        try {
            if (getIntent().hasExtra(AppConstant.URL))
                binding.webview.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + getIntent().getStringExtra(AppConstant.URL));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setWebViewListener() {
        binding.webview.setWebChromeClient(new WebChromeClient());
        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (binding.progressBar != null)
                    binding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (binding.webview != null)
                    binding.webview.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (binding.progressBar != null)
                    binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (binding.progressBar != null)
                    binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }
        });
    }
}
