package com.nibou.niboucustomer.actioncable;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.hosopy.actioncable.*;
import com.nibou.niboucustomer.models.AccessTokenModel;
import com.nibou.niboucustomer.models.MessageModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.LocalPrefences;

import java.net.URI;
import java.net.URISyntaxException;

public class ActionCableHandler {

    private Context context;
    private boolean showLog = true;
    private String CHANNEL_NAME = "ChatChannel";
    private String URL = AppConstant.SOCKET_URL;
    private Subscription subscription;
    private Consumer consumer;
    private MessageCallBack messageCallBack;

    public interface MessageCallBack {
        void messageRecieved(MessageModel messageModel);
    }

    public ActionCableHandler(Context context, MessageCallBack messageCallBack) {
        this.context = context;
        this.messageCallBack = messageCallBack;
        URL = URL + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken();
    }

    public void connectWS(String room_id) {
        URI uri = null;
        try {
            uri = new URI(URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Consumer.Options options = new Consumer.Options();
        options.reconnection = true;
        consumer = ActionCable.createConsumer(uri, options);

        Channel channel = new Channel(CHANNEL_NAME);
        channel.addParam("rid", room_id);

        subscription = consumer.getSubscriptions().create(channel);
        subscription.onConnected(() -> {
            showLog("Called when the subscription has been successfully completed");
        }).onRejected(() -> {
            showLog("Called when the subscription is rejected by the server");
        }).onReceived(data -> {
            showLog("Called when the subscription receives data from the server" + data.toString());
            MessageModel messageModel = new Gson().fromJson(data, MessageModel.class);
            if (messageCallBack != null && messageModel != null) {
                messageCallBack.messageRecieved(messageModel);
            }
        }).onDisconnected(() -> {
            showLog("Called when the subscription has been closed");
        }).onFailed(e -> {
            showLog("Called when the subscription encounters any error");
        });
        consumer.connect();
    }

    public void disconnectWS() {
        if (consumer != null)
            consumer.disconnect();
    }

    private void showLog(String message) {
        if (showLog)
            Log.e("ActionCable", ":" + message);
    }
}
