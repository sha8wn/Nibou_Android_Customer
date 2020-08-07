package com.nibou.niboucustomer.activitys;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityPaymentCardBinding;
import com.nibou.niboucustomer.utils.*;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Refund;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentCardActivity extends BaseActivity {
    private ActivityPaymentCardBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_card);
        context = this;
        ((ImageView) binding.toolbar.findViewById(R.id.back_arrow)).setColorFilter(ContextCompat.getColor(this, R.color.app_black_color_code), android.graphics.PorterDuff.Mode.MULTIPLY);
        binding.toolbar.findViewById(R.id.back_arrow).setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            onBackPressed();
        });

        if (getIntent().hasExtra("login")) {
            binding.toolbar.findViewById(R.id.back_arrow).setVisibility(View.GONE);
        }

        binding.etCardNumber.addTextChangedListener(new CreditCardNumberFormattingTextWatcher());
        binding.etCardExpiry.addTextChangedListener(new CreditCardExpiryFormattingTextWatcher());
        binding.etCardName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        binding.btnAddCard.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            if (AppUtil.isInternetAvailable(context)) {
                if (LocalPrefences.getInstance().getLocalAccessTokenModel(context) != null) {
                    if (screenValidate()) {
                        createCardToken();
                    }
                }
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getIntent().hasExtra("login")) {
        } else {
            super.onBackPressed();
        }
    }

    private void createCardToken() {

        try {
            AppDialogs.getInstance().showProgressBar(context, null, true);

            Card card = new Card(binding.etCardNumber.getText().toString().trim(), Integer.parseInt(binding.etCardExpiry.getText().toString().trim().split("/")[0]), Integer.parseInt(20 + binding.etCardExpiry.getText().toString().trim().split("/")[1]), binding.etCardCvv.getText().toString().trim());
            Stripe stripe = new Stripe(context, AppConstant.STRIPE_PUBLISH_KEY);
            stripe.createToken(card, mTokenCallback);

        } catch (Exception e) {
            e.printStackTrace();
            AppDialogs.getInstance().showProgressBar(context, null, false);
            AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.error), getString(R.string.card_error_alert), getString(R.string.OK), null);
        }
    }

    private final TokenCallback mTokenCallback = new TokenCallback() {
        @Override
        public void onSuccess(Token token) {
            if (AppUtil.isInternetAvailable(context)) {
                new CheckingCardTransactionTask(binding.etCardNumber.getText().toString().trim(), token).execute();
            } else {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        }

        @Override
        public void onError(Exception error) {
            AppDialogs.getInstance().showProgressBar(context, null, false);
            AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.error), getString(R.string.card_error_alert), getString(R.string.OK), null);
        }
    };

    class CheckingCardTransactionTask extends AsyncTask<String, Void, Boolean> {
        private Token token;
        private Customer customer;
        private String cardNumber;

        public CheckingCardTransactionTask(String cardNumber, Token token) {
            this.cardNumber = cardNumber;
            this.token = token;
        }

        protected Boolean doInBackground(String... urls) {
            try {
                com.stripe.Stripe.apiKey = AppConstant.STRIPE_SECRET_KEY;

                Map<String, Object> customerParams = new HashMap<String, Object>();
                customerParams.put("description", "create Customer");
                customerParams.put("source", token.getId());
                customer = Customer.create(customerParams);

                Map<String, Object> tokenParams = new HashMap<String, Object>();
                Map<String, Object> cardParams = new HashMap<String, Object>();
                cardParams.put("number", cardNumber);
                cardParams.put("exp_month", token.getCard().getExpMonth());
                cardParams.put("exp_year", token.getCard().getExpYear());
                cardParams.put("cvc", token.getCard().getCVC());
                tokenParams.put("card", cardParams);
                com.stripe.model.Token token1 = com.stripe.model.Token.create(tokenParams);

                Map<String, Object> params = new HashMap<>();
                params.put("amount", 100); //in cent=1 doller=3.67315 AED =5.53 TL
                params.put("currency", "usd");
                params.put("description", "Card charge test");
                params.put("source", token1.getId());
                Charge charge = Charge.create(params);
                if (charge.getStatus().equals("succeeded")) {
                    Map<String, Object> refundParams = new HashMap<>();
                    refundParams.put("charge", charge.getId());
                    Refund refund = Refund.create(refundParams);
                    if (refund.getStatus().equals("succeeded")) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        protected void onPostExecute(Boolean feed) {
            if (feed) {
                if (AppUtil.isInternetAvailable(context)) {
                    uploadCardNetworkCall(token, customer);
                } else {
                    AppDialogs.getInstance().showProgressBar(context, null, false);
                    AppUtil.showToast(context, getString(R.string.internet_error));
                }
            } else {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                AppDialogs.getInstance().showInfoCustomDialog(context, getString(R.string.error), getString(R.string.card_error_alert), getString(R.string.OK), null);
            }
        }
    }


    private void uploadCardNetworkCall(Token token, Customer customer) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("card_type", token.getCard().getBrand());
        parameters.put("last_numbers", token.getCard().getLast4());
        parameters.put("exp_date", binding.etCardExpiry.getText().toString().trim());
        parameters.put("is_default", true);
        parameters.put("card_id", token.getCard().getId());
        parameters.put("customer_id", customer.getId());

        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).saveCardRequest(LocalPrefences.getInstance().getString(this, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), parameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    if (getIntent().hasExtra(AppConstant.MENU_CARD_LIST)) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        if (getIntent().hasExtra("login")) {
                            if (getIntent().getBooleanExtra("isSessionActive", false)) {
                                Intent intent = new Intent(context, HomeActivity.class);
                                startActivityForResult(intent, 100);
                                finish();
                            } else {
                                Intent intent = new Intent(context, SurveyActivity.class);
                                intent.putExtras(getIntent().getExtras());
                                startActivityForResult(intent, 100);
                                finish();
                            }
                        } else {
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtras(getIntent().getExtras());
                            startActivityForResult(intent, 100);
                            finish();
                        }
                    }
                } else {
                    AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.error), context.getString(R.string.wrong_with_backend), context.getString(R.string.OK), null);
                }
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
            }
        });
    }


    private boolean screenValidate() {
        if (TextUtils.isEmpty(binding.etCardNumber.getText().toString().trim())) {
            AppUtil.showToast(this, getResources().getString(R.string.cardnumber_empty_alert));
            return false;
        } else if (TextUtils.isEmpty(binding.etCardName.getText().toString().trim())) {
            AppUtil.showToast(this, getResources().getString(R.string.cardholder_empty_alert));
            return false;
        } else if (TextUtils.isEmpty(binding.etCardExpiry.getText().toString().trim())) {
            AppUtil.showToast(this, getResources().getString(R.string.expiry_empty_alert));
            return false;
        } else if (TextUtils.isEmpty(binding.etCardCvv.getText().toString().trim())) {
            AppUtil.showToast(this, getResources().getString(R.string.cvv_empty_alert));
            return false;
        }
        return true;
    }


    public class CreditCardNumberFormattingTextWatcher implements TextWatcher {

        private boolean lock;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (lock || s.length() > 16) {
                return;
            }
            lock = true;
            for (int i = 4; i < s.length(); i += 5) {
                if (s.toString().charAt(i) != ' ') {
                    s.insert(i, " ");
                }
            }
            lock = false;

            if (binding != null) {
                int type = CreditCardUtils.getCardType(s.toString());
                if (type != -1) {
                    binding.count.setVisibility(View.VISIBLE);
                    binding.cardIcon.setVisibility(View.VISIBLE);
                    binding.cardIcon.setImageResource(type);
                } else {
                    binding.count.setVisibility(View.INVISIBLE);
                    binding.cardIcon.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public class CreditCardExpiryFormattingTextWatcher implements TextWatcher {

        private int previousLength;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            previousLength = binding.etCardExpiry.getText().toString().length();
        }

        @Override
        public void afterTextChanged(Editable s) {

            int length = binding.etCardExpiry.getText().toString().trim().length();

            if (previousLength <= length && length < 3) {
                int month = Integer.parseInt(binding.etCardExpiry.getText().toString());
                if (length == 1 && month >= 2) {
                    String autoFixStr = "0" + month + "/";
                    binding.etCardExpiry.setText(autoFixStr);
                    binding.etCardExpiry.setSelection(3);
                } else if (length == 2 && month <= 12) {
                    String autoFixStr = binding.etCardExpiry.getText().toString() + "/";
                    binding.etCardExpiry.setText(autoFixStr);
                    binding.etCardExpiry.setSelection(3);
                } else if (length == 2 && month > 12) {
                    binding.etCardExpiry.setText("1");
                    binding.etCardExpiry.setSelection(1);
                }
            }
        }
    }
}
