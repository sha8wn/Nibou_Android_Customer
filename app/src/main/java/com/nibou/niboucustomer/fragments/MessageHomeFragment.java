package com.nibou.niboucustomer.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.*;

import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.activitys.SurveyActivity;
import com.nibou.niboucustomer.adapters.IssueHistoryAdapter;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.FragmentHomeMessageBinding;
import com.nibou.niboucustomer.models.ActiveChatSessionModel;
import com.nibou.niboucustomer.models.AppSessionModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.AppUtil;
import com.nibou.niboucustomer.utils.LocalPrefences;

import java.util.ArrayList;
import java.util.HashSet;

public class MessageHomeFragment extends Fragment {

    private FragmentHomeMessageBinding binding;
    private ActiveChatSessionModel activeChatSessionModel;
    private IssueHistoryAdapter issueHistoryAdapter;
    private Context context;

    private boolean isRefreshing = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_message, container, false);
        context = getActivity();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppUtil.changeStatusBarColor(getActivity(), ContextCompat.getColor(getActivity(), R.color.pdf_bg));
        try {
            getActivity().registerReceiver(broadcastRefreshReceiver, new IntentFilter(AppConstant.NEW_MESSAGE_RECIEVER));
        } catch (Exception e) {
            e.printStackTrace();
        }
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(broadcastRefreshReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (issueHistoryAdapter != null && activeChatSessionModel != null && activeChatSessionModel.getData() != null && activeChatSessionModel.getData().size() > 0) {
            activeChatSessionModel.sort();
            issueHistoryAdapter.refereshList(activeChatSessionModel);
        }

        if (AppUtil.isInternetAvailable(context)) {
            getChatSessionNetworkCall();
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void initView() {
        binding.rectangle.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            Intent intent = new Intent(getActivity(), SurveyActivity.class);
            intent.putExtra(AppConstant.SWITCH, true);
            startActivity(intent);
        });

        if (LocalPrefences.getInstance().getLocalProfileModel(context) != null)
            binding.custoName.setText(getString(R.string.hello) + ", " + LocalPrefences.getInstance().getLocalProfileModel(context).getData().getAttributes().getName());

        if (AppUtil.isInternetAvailable(context)) {
            getChatSessionNetworkCall();
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    private void getChatSessionNetworkCall() {
        if (isRefreshing)
            return;
        isRefreshing = true;
        AppDialogs.getInstance().showProgressBar(context, null, true);
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getActiveChatSession(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                if (isSuccess) {
                    activeChatSessionModel = (ActiveChatSessionModel) data;
                    activeChatSessionModel.sort();
                    showSessionList();
                }
                isRefreshing = false;
            }

            @Override
            public void failed() {
                AppDialogs.getInstance().showProgressBar(context, null, false);
                isRefreshing = false;
            }
        });
    }

    private void showSessionList() {
        if (issueHistoryAdapter == null) {
            binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
            issueHistoryAdapter = new IssueHistoryAdapter(getActivity(), this, activeChatSessionModel);
            binding.recyclerview.setAdapter(issueHistoryAdapter);
        } else {
            issueHistoryAdapter.refereshList(activeChatSessionModel);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (binding != null) {
            if (issueHistoryAdapter != null) {
                if (activeChatSessionModel.getData() != null && activeChatSessionModel.getData().size() > 0)
                    activeChatSessionModel.sort();
                issueHistoryAdapter.refereshList(activeChatSessionModel);
            }
            if (AppUtil.isInternetAvailable(context)) {
                getChatSessionNetworkCall();
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        }
    }

    BroadcastReceiver broadcastRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (binding != null) {
                    if (intent.hasExtra(AppConstant.APP_SESSION_MODEL)) {
                        if (intent.hasExtra(AppConstant.APP_FCM)) {
                            updateLastMesssageTimeStampFromPushNotification((AppSessionModel) intent.getSerializableExtra(AppConstant.APP_SESSION_MODEL));
                        } else {
                            updateLastMesssageTimeStamp((AppSessionModel) intent.getSerializableExtra(AppConstant.APP_SESSION_MODEL));
                        }
                    } else {
                        if (AppUtil.isInternetAvailable(context)) {
                            getChatSessionNetworkCall();
                        } else {
                            AppUtil.showToast(context, getString(R.string.internet_error));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void updateLastMesssageTimeStamp(AppSessionModel appSessionModel) {
        try {
            if (activeChatSessionModel != null && activeChatSessionModel.getData() != null) {
                for (int i = 0; i < activeChatSessionModel.getData().size(); i++) {
                    if (activeChatSessionModel.getData().get(i).getId().equals(appSessionModel.getRoom().getData().getId())) {
                        if (activeChatSessionModel.getData().get(i).getAttributes().getLast_message() != null) {
                            activeChatSessionModel.getData().get(i).getAttributes().getLast_message().getData().getAttributes().setCreated_at(appSessionModel.getMessage().getData().getAttributes().getCreated_at());
                        } else {
                            activeChatSessionModel.getData().get(i).getAttributes().setLast_message(appSessionModel.getMessage());
                        }
                    }
                }
                activeChatSessionModel.sort();
                new Handler().postDelayed(() -> {
                    if (issueHistoryAdapter != null)
                        issueHistoryAdapter.refereshList(activeChatSessionModel);
                }, 300);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateLastMesssageTimeStampFromPushNotification(AppSessionModel appSessionModel) {
        try {
            if (activeChatSessionModel != null && activeChatSessionModel.getData() != null) {
                for (int i = 0; i < activeChatSessionModel.getData().size(); i++) {
                    if (activeChatSessionModel.getData().get(i).getId().equals(appSessionModel.getRoom_id())) {
                        activeChatSessionModel.getData().get(i).getAttributes().getLast_message().getData().getAttributes().setCreated_at(appSessionModel.getMessage().getCreated_at());
                    }
                }
                activeChatSessionModel.sort();
                new Handler().postDelayed(() -> {
                    if (issueHistoryAdapter != null)
                        issueHistoryAdapter.refereshList(activeChatSessionModel);
                }, 300);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}