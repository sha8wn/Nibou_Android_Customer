package com.nibou.niboucustomer.actioncable;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.hosopy.actioncable.ActionCable;
import com.hosopy.actioncable.Channel;
import com.hosopy.actioncable.Consumer;
import com.hosopy.actioncable.Subscription;
import com.nibou.niboucustomer.Dialogs.AppDialogs;
import com.nibou.niboucustomer.activitys.BaseActivity;
import com.nibou.niboucustomer.models.AppSessionModel;
import com.nibou.niboucustomer.models.MessageModel;
import com.nibou.niboucustomer.models.SessionTimeoutModel;
import com.nibou.niboucustomer.models.User;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.LocalPrefences;
import com.nibou.niboucustomer.utils.TimerUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

public class ActionSessionHandler {
    private Context context;
    private boolean showLog = true;
    private String CHANNEL_NAME = "UserNotificationChannel";
    private String URL = AppConstant.SOCKET_URL;

    private Subscription subscription;
    private Consumer consumer;
    private static ActionSessionHandler actionSessionHandler;

    private String ONLINE = "online";
    private String OFFLINE = "offline";


    public static ActionSessionHandler getActionSessionHandler(Context context) {
        if (actionSessionHandler == null) {
            actionSessionHandler = new ActionSessionHandler(context);
        }
        return actionSessionHandler;
    }

    private ActionSessionHandler() {
    }

    private ActionSessionHandler(Context context) {
        this.context = context;
    }

    public void connectWS() {
        URI uri = null;
        try {
            uri = new URI(URL + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken());
            Consumer.Options options = new Consumer.Options();
            options.reconnection = true;
            consumer = ActionCable.createConsumer(uri, options);

            Channel channel = new Channel(CHANNEL_NAME);

            subscription = consumer.getSubscriptions().create(channel);
            subscription.onConnected(() -> {
                showLog("Called when the subscription has been successfully completed");
            }).onRejected(() -> {
                showLog("Called when the subscription is rejected by the server");
            }).onReceived(data -> {
                showLog("Called when the subscription receives data from the server" + data.toString());
                try {
                    AppSessionModel messageModel = new Gson().fromJson(data, AppSessionModel.class);
                    handleAction(messageModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).onDisconnected(() -> {
                showLog("Called when the subscription has been closed");
                if (subscription != null) {
                    showLog(OFFLINE + " Action");
                    subscription.perform(OFFLINE);
                }
            }).onFailed(e -> {
                showLog("Called when the subscription encounters any error" + e.getLocalizedMessage());
            });
            consumer.connect();
            running10SecTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectWS() {
        if (consumer != null)
            consumer.disconnect();
        dismiss10SecTimer();
    }

    private void running10SecTimer() {
        TimerUtil.getTimerUtil().start10SecTimer(new TimerUtil.TimerCallback() {
            @Override
            public void onTimerCompleted() {

            }

            @Override
            public void onTimerTick(String time, long minute, long second) {
                if (subscription != null) {
                    showLog(ONLINE + " Action");
                    subscription.perform(ONLINE);
                }
            }
        });
    }

    private void dismiss10SecTimer() {
        TimerUtil.getTimerUtil().cancel10SecTimer();
    }

    private void handleAction(AppSessionModel sessionTimeoutModel) {
        String room_id = "";
        String name = "";
        String expert_id = "";

        if (sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.SESSION_TIMEOUT_ACTION) || sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.SESSION_END_ACTION)) {
            if (LocalPrefences.getInstance().isUserLogin(context)) {
                room_id = sessionTimeoutModel.getRoom().getData().getId();
                for (int i = 0; i < sessionTimeoutModel.getRoom().getData().getAttributes().getUsers().size(); i++) {
                    if (!(sessionTimeoutModel.getRoom().getData().getAttributes().getUsers().get(i).getData().getId().equals(LocalPrefences.getInstance().getLocalProfileModel(context).getData().getId()))) {
                        name = sessionTimeoutModel.getRoom().getData().getAttributes().getUsers().get(i).getData().getAttributes().getName();
                        expert_id = sessionTimeoutModel.getRoom().getData().getAttributes().getUsers().get(i).getData().getId();
                        break;
                    }
                }
            }
        }
        if (sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.USER_ONLINE_ACTION)) {
            HashSet<String> hashSet = LocalPrefences.getInstance().getLocalStatusOfExpert(context);
            if (hashSet == null) {
                hashSet = new HashSet<>();
            }
            hashSet.remove(sessionTimeoutModel.getUser_id());
            LocalPrefences.getInstance().saveLocalStatusOfExpert(context, hashSet);
            Intent intent = new Intent(AppConstant.ONLINE_OFFLINE_RECIEVER);
            intent.putExtra("id", sessionTimeoutModel.getUser_id());
            context.sendBroadcast(intent);
        } else if (sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.USER_OFFLINE_ACTION)) {
            HashSet<String> hashSet = LocalPrefences.getInstance().getLocalStatusOfExpert(context);
            if (hashSet == null) {
                hashSet = new HashSet<>();
            }
            hashSet.add(sessionTimeoutModel.getUser_id());
            LocalPrefences.getInstance().saveLocalStatusOfExpert(context, hashSet);
            Intent intent = new Intent(AppConstant.ONLINE_OFFLINE_RECIEVER);
            intent.putExtra("id", sessionTimeoutModel.getUser_id());
            context.sendBroadcast(intent);
        } else if (sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.SESSION_TIMEOUT_ACTION)) {
            if (LocalPrefences.getInstance().isUserLogin(context)) {
                Intent intent = new Intent(AppConstant.TIMEOUT_RECIEVER);
                intent.putExtra(AppConstant.SESSION_TIMEOUT_ACTION, "true");
                intent.putExtra("name", name);
                intent.putExtra("expert_id", expert_id);
                intent.putExtra("room_id", room_id);
                intent.putExtra("time", sessionTimeoutModel.getTimeout());
                context.sendBroadcast(intent);
            }
        } else if (sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.SESSION_END_ACTION)) {
            if (LocalPrefences.getInstance().isUserLogin(context)) {
                Intent intent = new Intent(AppConstant.TIMEOUT_RECIEVER);
                intent.putExtra("name", name);
                intent.putExtra("expert_id", expert_id);
                intent.putExtra("room_id", room_id);
                context.sendBroadcast(intent);

                Intent exitFromChatScreen = new Intent(AppConstant.NEW_MESSAGE_RECIEVER);
                intent.putExtra(AppConstant.SESSION_END_ACTION, "true");
                exitFromChatScreen.putExtra(AppConstant.ROOM_ID, room_id);
                context.sendBroadcast(exitFromChatScreen);
            }
        } else if (sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.NEW_MESSAGE_ACTION)) {
            if (LocalPrefences.getInstance().isUserLogin(context)) {
                if (!LocalPrefences.getInstance().getLocalProfileModel(context).getData().getId().equals(sessionTimeoutModel.getMessage().getData().getAttributes().getFrom_user_id())) {

                    if (!AppConstant.IGNORE_MESSAGE_TEXT.equals(sessionTimeoutModel.getMessage().getText() != null ? sessionTimeoutModel.getMessage().getText() : "")) {
                        LocalPrefences.getInstance().putInt(context, sessionTimeoutModel.getRoom().getData().getId(), (LocalPrefences.getInstance().getInt(context, sessionTimeoutModel.getRoom().getData().getId()) + 1));
                        Intent intent = new Intent(AppConstant.NEW_MESSAGE_RECIEVER);
                        intent.putExtra(AppConstant.APP_SESSION_MODEL, sessionTimeoutModel);
                        context.sendBroadcast(intent);
                    }
                }
            }
        }
    }

    private void showLog(String message) {
        if (showLog)
            Log.e("ActionSession", ":" + message);
    }
}
