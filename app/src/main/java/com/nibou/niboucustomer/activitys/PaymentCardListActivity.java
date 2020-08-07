package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.adapters.CardListAdapter;
import com.nibou.niboucustomer.adapters.EarningListAdapter;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityPaymentCardListBinding;
import com.nibou.niboucustomer.models.CardModel;
import com.nibou.niboucustomer.models.ProfileModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;
import com.stripe.android.model.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class PaymentCardListActivity extends BaseActivity {

    private ActivityPaymentCardListBinding binding;
    private CardListAdapter cardListAdapter;

    private int ADD_CARD_REQUEST = 121;
    private Context context;
    private ProfileModel profileModel;
    private int markDefaultCardPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_card_list);
        context = this;
        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.app_black_color_code), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });


        binding.btnAddCard.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            Intent intent = new Intent(context, PaymentCardActivity.class);
            intent.putExtra(AppConstant.MENU_CARD_LIST, true);
            startActivityForResult(intent, ADD_CARD_REQUEST);
        });

        binding.btnSelectCard.setVisibility(View.INVISIBLE);

        binding.btnSelectCard.setOnClickListener(v -> {
            if (getString(R.string.set_default_card).equals(binding.btnSelectCard.getText().toString())) {
                AppUtil.hideKeyBoard(context);
                if (AppUtil.isInternetAvailable(context)) {
                    if (LocalPrefences.getInstance().getLocalAccessTokenModel(context) != null) {
                        markDefaultCardNetworkCall(markDefaultCardPosition);
                    }
                } else {
                    AppUtil.showToast(context, getString(R.string.internet_error));
                }
            }
        });

        if (AppUtil.isInternetAvailable(context)) {
            if (LocalPrefences.getInstance().getLocalAccessTokenModel(context) != null) {
                cardListNetworkCall(true);
            }
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void showCardData() {
        if (profileModel != null && profileModel.getIncluded() != null) {
            for (ProfileModel.Data model : profileModel.getIncluded()) {
                if (model.getAttributes().isIs_default()) {
                    model.getAttributes().setIs_server_default(true);
                }
            }
            Collections.sort(profileModel.getIncluded(), new LastUpdatedCardFilter());
        }
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        cardListAdapter = new CardListAdapter(context, profileModel);
        binding.recyclerView.setAdapter(cardListAdapter);
    }

    public void cardSelected(boolean status, int markDefaultCardPosition) {
        if (binding != null) {
            if (status) {
                this.markDefaultCardPosition = markDefaultCardPosition;
                binding.btnSelectCard.setVisibility(View.VISIBLE);
                binding.btnSelectCard.setText(getString(R.string.set_default_card));
            } else {
                binding.btnSelectCard.setVisibility(View.INVISIBLE);
                binding.btnSelectCard.setText(context.getString(R.string.select_card));
            }
        }
    }

    private void cardListNetworkCall(boolean dialogShow) {
        if (dialogShow)
            AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getMyProfile(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), "user_credit_cards"), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    profileModel = (ProfileModel) data;
                    showCardData();
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }

    class LastUpdatedCardFilter implements Comparator<ProfileModel.Data> {
        public int compare(ProfileModel.Data m1, ProfileModel.Data m2) {
            if (m1.getAttributes().isIs_server_default()) return -1;
            else return 1;
        }
    }

    private void markDefaultCardNetworkCall(int position) {
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).markCardDefaultRequest(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), profileModel.getIncluded().get(position).getId()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    cardSelected(false, 0);
                    if (AppUtil.isInternetAvailable(context)) {
                        cardListNetworkCall(false);
                    } else {
                        AppDialogs.getInstance().showProgressBar(context, null, false);
                        AppUtil.showToast(context, getString(R.string.internet_error));
                    }

                } else {
                    AppDialogs.getInstance().showProgressBar(context, null, false);
                    AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.error).toUpperCase(), getString(R.string.wrong_with_backend), getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CARD_REQUEST && data != null) {
            if (AppUtil.isInternetAvailable(context)) {
                if (LocalPrefences.getInstance().getLocalAccessTokenModel(context) != null) {
                    cardListNetworkCall(true);
                }
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        }
    }
}
