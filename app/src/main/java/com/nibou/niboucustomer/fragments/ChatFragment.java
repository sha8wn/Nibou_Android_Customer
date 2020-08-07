package com.nibou.niboucustomer.fragments;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.actioncable.ActionCableHandler;
import com.nibou.niboucustomer.actioncable.MessageApiCall;
import com.nibou.niboucustomer.actioncable.MessageHistoryAsynTask;
import com.nibou.niboucustomer.activitys.*;
import com.nibou.niboucustomer.adapters.ChatAdapter;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.databinding.ActivityChatBinding;
import com.nibou.niboucustomer.databinding.FragmentChatBinding;
import com.nibou.niboucustomer.databinding.FragmentHomeMessageBinding;
import com.nibou.niboucustomer.models.*;
import com.nibou.niboucustomer.realm.RealmHandler;
import com.nibou.niboucustomer.utils.*;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.*;

public class ChatFragment extends Fragment implements ActionCableHandler.MessageCallBack, ChatAdapter.ChatCallback {

    private FragmentChatBinding binding;
    private Context context;

    private ChatAdapter chatAdapter;
    private MediaUtil mediaUtil;
    private RealmHandler realmHandler;
    private ActionCableHandler actionCableHandler;

    private String getRoomId() {
        return getActivity().getIntent().getStringExtra(ChatConstants.ROOM_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        context = getActivity();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalPrefences.getInstance().putInt(context, getRoomId(), 0);
        if (AppUtil.isInternetAvailable(context)) {
            new Thread(() -> checkRoomOpenOrClosedNetworkCall()).start();
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocalPrefences.getInstance().putInt(context, getRoomId(), 0);
        NotificationManager notificationManager = (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        try {
            getActivity().registerReceiver(broadcastRefreshReceiver, new IntentFilter(AppConstant.NEW_MESSAGE_RECIEVER));
        } catch (Exception e) {
            e.printStackTrace();
        }
        initView();
        actionCableHandler = new ActionCableHandler(getActivity(), this);
        actionCableHandler.connectWS(getRoomId());

        if (AppUtil.isInternetAvailable(context)) {
            loadMessageHistoryNetworkCall();
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaUtil.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mediaUtil.onRequestPermissionsResult(requestCode);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (actionCableHandler != null)
            actionCableHandler.disconnectWS();
        try {
            getActivity().unregisterReceiver(broadcastRefreshReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        realmHandler = new RealmHandler();
        mediaUtil = new MediaUtil(getActivity(), this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
        chatAdapter = new ChatAdapter(getActivity(), this, getRoomId());
        binding.recyclerView.setAdapter(chatAdapter);

        binding.ivAttachment.setOnClickListener(v -> {
            AppUtil.hideKeyBoard(context);
            mediaUtil.openMultipleImageGallery((isCamera, imageResult) -> {
                if (AppUtil.isInternetAvailable(context)) {
                    mediaUtil.compressMultipleImages((ArrayList<Image>) imageResult, 80, (bitmap, fileName, path) -> {

                        if (path != null) {
                            getActivity().runOnUiThread(() -> {

                                String taskId = UUID.randomUUID().toString();
                                String message = binding.etMessage.getText().toString().trim();
                                sendMessage(taskId, message, path);

                                RequestBody text = null;
                                if (!message.isEmpty())
                                    text = RequestBody.create(MediaType.parse("text/plain"), message);

                                ArrayList<MultipartBody.Part> partArrayList = new ArrayList<>();
                                File file = new File(path);
                                partArrayList.add(MultipartBody.Part.createFormData("images[]", "avatar." + FilenameUtils.getExtension(file.toString()), RequestBody.create(MediaType.parse("multipart/form-data"), file)));

                                sendMessageNetworkCall(taskId, text, partArrayList);

                            });
                        }

                    });
                } else {
                    AppUtil.showToast(context, getString(R.string.internet_error));
                }
            });
        });

        binding.ivSend.setOnClickListener(v -> {
            if (AppUtil.isInternetAvailable(context)) {
                if (screenValidate()) {
                    String taskId = UUID.randomUUID().toString();
                    String message = binding.etMessage.getText().toString().trim();
                    sendMessage(taskId, message, null);
                    sendMessageNetworkCall(taskId, RequestBody.create(MediaType.parse("text/plain"), message), null);
                }
            } else {
                AppUtil.showToast(context, getString(R.string.internet_error));
            }
        });
    }

    @Override
    public void retry(RealmChatModel realmChatModel) {
        if (AppUtil.isInternetAvailable(context)) {
            RealmChatModel model = realmHandler.getRealm().copyFromRealm(realmChatModel);
            model.setSending(true);
            model.setError(false);
            realmHandler.insertMessageInRealm(model);

            RequestBody text = null;
            if (!realmChatModel.getMessage().isEmpty())
                text = RequestBody.create(MediaType.parse("text/plain"), realmChatModel.getMessage());

            ArrayList<MultipartBody.Part> partArrayList = new ArrayList<>();
            File file = new File(realmChatModel.getLocalImageUrl());
            partArrayList.add(MultipartBody.Part.createFormData("images[]", "avatar." + FilenameUtils.getExtension(file.toString()), RequestBody.create(MediaType.parse("multipart/form-data"), file)));

            sendMessageNetworkCall(realmChatModel.getMessageId(), text, partArrayList);
        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    @Override
    public void download(RealmChatModel realmChatModel) {
        if (AppUtil.isInternetAvailable(context)) {
            RealmChatModel model = realmHandler.getRealm().copyFromRealm(realmChatModel);
            model.setDownloading(true);
            realmHandler.insertMessageInRealm(model);
            mediaUtil.downloadImage(realmChatModel.getMessageId(), realmChatModel.getImages().split(ChatConstants.IMAGE_PREVIEW_SEPRATOR)[0], new MediaUtil.DownloadedFileCallback() {
                @Override
                public void progress(int progress) {

                }

                @Override
                public void response(boolean isSuccess, String taskId, String path) {
                    RealmChatModel m = realmHandler.getMessageInRealm(taskId);
                    RealmChatModel newModel = realmHandler.getRealm().copyFromRealm(m);
                    if (isSuccess)
                        newModel.setLocalImageUrl(path);
                    newModel.setDownloading(false);
                    realmHandler.insertMessageInRealm(newModel);
                    chatAdapter.notifyDataSetChanged();
                }
            });

        } else {
            AppUtil.showToast(context, getString(R.string.internet_error));
        }
    }

    @Override
    public void loadMoreMessageHistory(RealmChatModel realmChatModel) {

    }

    @Override
    public void messageRecieved(MessageModel messageModel) {
        //get callback of other user messages
        if (!messageModel.getData().getAttributes().getFrom_user_id().equals(LocalPrefences.getInstance().getLocalProfileModel(context).getData().getId())) {
            getActivity().runOnUiThread(() -> {
                RealmChatModel realmChatModel = new RealmChatModel();
                saveMessagesInLocalDB(realmChatModel, messageModel.getData());
                if (binding != null && ((LinearLayoutManager) binding.recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() < 3) {
                    binding.etMessage.setText("");
                    binding.recyclerView.scrollToPosition(0);
                }
            });
        }
    }

    private void sendMessageNetworkCall(String taskId, RequestBody text, List<MultipartBody.Part> images) {

        new MessageApiCall(context, taskId).sendMessageNetworkCall(getRoomId(), text, images, new MessageApiCall.Callback() {
            @Override
            public void success(String taskId, MessageModel messageModel) {
                RealmChatModel realmChatModel = realmHandler.getMessageInRealm(taskId);

                RealmChatModel model = new RealmChatModel();
                if (realmChatModel.getLocalImageUrl() != null && !realmChatModel.getLocalImageUrl().isEmpty()) {
                    model.setLocalImageUrl(realmChatModel.getLocalImageUrl());
                    saveMessagesInLocalDB(model, messageModel.getData());
                } else {
                    saveMessagesInLocalDB(model, messageModel.getData());
                }
                realmHandler.deleteMessageInRealm(taskId);
                moveScreenToBottom();
            }

            @Override
            public void failed(String taskId, boolean isCardError, String error) {
                try {
                    realmHandler.deleteMessageInRealm(taskId);
                    if (error != null) {
                        if (isCardError) {
                            AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.credit_card_title_alert), context.getString(R.string.credit_card_message_alert), context.getString(R.string.OK), status -> {
                                Intent intent = new Intent(getActivity(), PaymentCardListActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            });
                        } else {
                            AppDialogs.getInstance().showInfoCustomDialog(context, context.getString(R.string.error), error, context.getString(R.string.OK), null);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void saveMessagesInLocalDB(RealmChatModel realmChatModel, MessageModel.Data data) {
        if (AppConstant.IGNORE_MESSAGE_TEXT.equals(data.getAttributes().getText()))
            return;
        realmChatModel.setMessageId(data.getId());
        realmChatModel.setUserId(data.getAttributes().getFrom_user_id());
        realmChatModel.setChatId(data.getRelationships().getRoom().getData().getId());
        realmChatModel.setMessage(data.getAttributes().getText());
        if (data.getAttributes().getImages() != null && data.getAttributes().getImages().size() > 0 && data.getAttributes().getImages().get(0).getData().getAttributes().getImages() != null && data.getAttributes().getImages().get(0).getData().getAttributes().getImages().size() > 0) {
            realmChatModel.setImages(AppConstant.FILE_BASE_URL + data.getAttributes().getImages().get(0).getData().getAttributes().getImages().get(0).getUrl() + "$" + AppConstant.FILE_BASE_URL + data.getAttributes().getImages().get(0).getData().getAttributes().getImages().get(0).getW220().getUrl());
        }
        realmChatModel.setTimeStamp(DateFormatUtil.getLocalFormatMillies(data.getAttributes().getCreated_at()));
        realmHandler.insertMessageInRealm(realmChatModel);
    }

    private void sendMessage(String taskId, String message, String images) {
        RealmChatModel realmChatModel = new RealmChatModel();
        realmChatModel.setMessageId(taskId);
        realmChatModel.setUserId(LocalPrefences.getInstance().getLocalProfileModel(context).getData().getId());
        realmChatModel.setChatId(getRoomId());
        realmChatModel.setMessage(message);
        realmChatModel.setLocalImageUrl(images);
        realmChatModel.setTimeStamp(DateFormatUtil.getLocalMillies());
        realmChatModel.setSending(true);
        realmHandler.insertMessageInRealm(realmChatModel);
        moveScreenToBottom();
    }

    private void loadMessageHistoryNetworkCall() {
        new MessageHistoryAsynTask(context, getRoomId(), messageHistoryModel -> {
            for (int i = 0; i < messageHistoryModel.getData().size(); i++) {
                if (realmHandler.getMessageInRealm(messageHistoryModel.getData().get(i).getId()) == null) {
                    saveMessagesInLocalDB(new RealmChatModel(), messageHistoryModel.getData().get(i));
                }
            }
            moveScreenToBottom();
        }).execute();
    }

    private boolean screenValidate() {
        if (binding.etMessage.getText().toString().trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void moveScreenToBottom() {
        binding.etMessage.setText("");
        binding.recyclerView.scrollToPosition(0);
        chatAdapter.notifyDataSetChanged();
    }

    BroadcastReceiver broadcastRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (binding != null) {
                    if (intent.hasExtra(AppConstant.APP_SESSION_MODEL)) {
                        if (getRoomId().equals(((AppSessionModel) intent.getSerializableExtra(AppConstant.APP_SESSION_MODEL)).getRoom().getData().getId()))
                            LocalPrefences.getInstance().putInt(context, ((AppSessionModel) intent.getSerializableExtra(AppConstant.APP_SESSION_MODEL)).getRoom().getData().getId(), 0);
                    }

                    if (intent != null && intent.hasExtra(AppConstant.SESSION_END_ACTION) && intent.getStringExtra(AppConstant.ROOM_ID).equals(getRoomId())) {
                        getActivity().finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void checkRoomOpenOrClosedNetworkCall() {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).checkRoomOpenOrClosedRequest(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), getRoomId()), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                try {
                    if (!isSuccess && binding != null) {
                        AppDialogs.getInstance().showInfoWithOutTitleCustomDialog(context, context.getString(R.string.error).toUpperCase(), context.getString(R.string.session_ended_alert), context.getString(R.string.OK), status -> getActivity().finish());
                    }
                    if (isSuccess && binding != null) {
                        ProfileModel profileModel = getProfileModel(((ChatSessionModel) data).getData());
                        if (profileModel.getData().getAttributes().isAvailable()) {
                            HashSet<String> hashSet = LocalPrefences.getInstance().getLocalStatusOfExpert(context);
                            if (hashSet == null) {
                                hashSet = new HashSet<>();
                            }
                            hashSet.remove(profileModel.getData().getId());
                            LocalPrefences.getInstance().saveLocalStatusOfExpert(context, hashSet);
                        } else {
                            HashSet<String> hashSet = LocalPrefences.getInstance().getLocalStatusOfExpert(context);
                            if (hashSet == null) {
                                hashSet = new HashSet<>();
                            }
                            hashSet.add(profileModel.getData().getId());
                            LocalPrefences.getInstance().saveLocalStatusOfExpert(context, hashSet);
                            if (getActivity() instanceof ChatActivity) {
                                ((ChatActivity) getActivity()).showInactiveDialog();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed() {
            }
        });
    }

    private ProfileModel getProfileModel(ActiveChatSessionModel.Data data) {
        for (int i = 0; i < data.getAttributes().getUsers().size(); i++) {
            if (!(data.getAttributes().getUsers().get(i).getData().getId().equals(LocalPrefences.getInstance().getLocalProfileModel(context).getData().getId()))) {
                return data.getAttributes().getUsers().get(i);
            }
        }
        return null;
    }

}
