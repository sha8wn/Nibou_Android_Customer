package com.nibou.niboucustomer.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.*;

import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.actioncable.ActionSessionHandler;
import com.nibou.niboucustomer.activitys.*;
import com.nibou.niboucustomer.adapters.ChatAdapter;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.FragmentSettingBinding;
import com.nibou.niboucustomer.models.AccessTokenModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;
import com.nibou.niboucustomer.utils.MediaUtil;

import java.util.HashMap;
import java.util.Locale;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;

    public SettingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppUtil.changeStatusBarColor(getActivity(), ContextCompat.getColor(getActivity(), R.color.white));
        initView();
        checkDeviceLanguage();
    }

    private void initView() {
        binding.logout.setOnClickListener(v -> {
            AppDialogs.getInstance().showConfirmCustomDialog(getActivity(), getActivity().getString(R.string.logout), getActivity().getString(R.string.logout_message), getActivity().getString(R.string.CANCEL), getActivity().getString(R.string.OK).toUpperCase(), new AppDialogs.DialogCallback() {
                @Override
                public void response(boolean status) {
                    if (status) {
                        logoutNetworkCall();
                    }
                }
            });
        });

        binding.updateProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        binding.changePwd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        binding.chooseLangauge.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LanguageActivity.class);
            intent.putExtra(AppConstant.VIEW, "true");
            getActivity().startActivityForResult(intent, AppConstant.LANGUAGE_SCREEN_CODE);
        });

        binding.manageCards.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PaymentCardListActivity.class);
            startActivity(intent);
        });
        binding.pricingTerms.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PricingAndTermsActivity.class);
            startActivity(intent);
        });
        binding.transactionHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EarningActivity.class);
            startActivity(intent);
        });
        binding.feedback.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FeedbackActivity.class);
            startActivity(intent);
        });
    }

    private void checkDeviceLanguage() {
        if (Locale.getDefault().getLanguage().equals(AppConstant.ENGLISH) || Locale.getDefault().getLanguage().equals(AppConstant.TURKISH) || Locale.getDefault().getLanguage().equals(AppConstant.ARABIC)) {
            //hide language option
            binding.langOption.setVisibility(View.GONE);
        } else {
            //show language option
            binding.langOption.setVisibility(View.VISIBLE);
        }
    }

    private void logoutNetworkCall() {
        HashMap<Object, Object> parameters = new HashMap<>();
        parameters.put("available", false);
        AppDialogs.getInstance().showProgressBar(getActivity(), null, true);
        ApiHandler.requestService(getActivity(), ApiClient.getClient().create(ApiEndPoint.class).logoutRequest(LocalPrefences.getInstance().getString(getActivity(), AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(getActivity()).getAccessToken(), parameters), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(getActivity(), null, false);
                clearLocalData();
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(getActivity(), null, false);
                clearLocalData();
            }
        });
    }

    private void clearLocalData() {
        ActionSessionHandler.getActionSessionHandler(getActivity()).disconnectWS();
        String language = LocalPrefences.getInstance().getString(getActivity(), AppConstant.APP_LANGUAGE);
        boolean firstLaunch = LocalPrefences.getInstance().getBoolean(getActivity(), AppConstant.IS_FIRST_LAUNCH_SUCCESS);
        LocalPrefences.getInstance().clearSharePreference(getActivity());
        LocalPrefences.getInstance().putString(getActivity(), AppConstant.APP_LANGUAGE, language);
        LocalPrefences.getInstance().putBoolean(getActivity(), AppConstant.IS_FIRST_LAUNCH_SUCCESS, firstLaunch);
        Intent intent = new Intent(getActivity(), UserCheckActivity.class);
        startActivity(intent);
        getActivity().finishAffinity();
    }
}
