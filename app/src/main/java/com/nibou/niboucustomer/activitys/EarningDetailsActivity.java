package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.adapters.EarningDetailAdapter;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityEarningBinding;
import com.nibou.niboucustomer.models.PaymentListModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.DateFormatUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

public class EarningDetailsActivity extends BaseActivity implements EarningDetailAdapter.PaginationCallback {

    private ActivityEarningBinding binding;
    private Context context;
    private PaymentListModel paymentListModel;
    private EarningDetailAdapter earningListAdapter;


    private void setToolbar() {
        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.screen_title_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_earning);
        context = this;
        setToolbar();
        // binding.earnings.setText(DateFormatUtil.getRequiredDateFormat(DateFormatUtil.getServerMilliSeconds(getIntent().getStringExtra("date")), "dd MMMM , yyyy"));
        binding.earningList.setBackground(null);
        binding.earnings.setVisibility(View.GONE);
        if (AppUtil.isInternetAvailable(context)) {
            if (LocalPrefences.getInstance().getLocalAccessTokenModel(context) != null) {
                getDayEarningDetailNetworkCall(1, 10);
            }
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void showData() {
        binding.earningList.setLayoutManager(new LinearLayoutManager(context));
        earningListAdapter = new EarningDetailAdapter(context, paymentListModel);
        binding.earningList.setAdapter(earningListAdapter);
    }

    @Override
    public void OnPageClick() {
        if (paymentListModel != null && paymentListModel.getMeta() != null) {
            if (paymentListModel.getMeta().getCurrent_page() < paymentListModel.getMeta().getTotal_pages()) {
                if (AppUtil.isInternetAvailable(context)) {
                    if (LocalPrefences.getInstance().getLocalAccessTokenModel(context) != null) {
                        getDayEarningDetailNetworkCall(Integer.parseInt(paymentListModel.getMeta().getNext_page()), 10);
                    }
                } else {
                    AppUtil.showToast(context, getString(R.string.internet_error));
                }
            }
        }
    }

    private void getDayEarningDetailNetworkCall(int page_number, int page_size) {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getDayEarningDetailRequest(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), DateFormatUtil.getRequiredDateFormat(DateFormatUtil.getServerMilliSeconds(getIntent().getStringExtra("date")), "dd-MM-yyyy"), true, page_number, page_size), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess && binding != null) {
                    if (paymentListModel == null) {
                        paymentListModel = (PaymentListModel) data;
                        showData();
                    } else {
                        PaymentListModel paymentListModel = (PaymentListModel) data;
                        EarningDetailsActivity.this.paymentListModel.getData().addAll(paymentListModel.getData());
                        EarningDetailsActivity.this.paymentListModel.setMeta(paymentListModel.getMeta());
                        earningListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }
}
