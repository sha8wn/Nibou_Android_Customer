package com.nibou.niboucustomer.actioncable;

import android.content.Context;
import android.os.AsyncTask;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.models.MessageHistoryModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.LocalPrefences;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class MessageHistoryAsynTask extends AsyncTask<Void, Void, Boolean> {
    private MessageHistoryCallback historyCallback;
    private String room_id;
    private Context context;
    private MessageHistoryModel messageHistoryModel;

    public interface MessageHistoryCallback {
        void success(MessageHistoryModel messageHistoryModel);
    }

    public MessageHistoryAsynTask(Context context, String room_Id, MessageHistoryCallback historyCallback) {
        this.context = context;
        this.room_id = room_Id;
        this.historyCallback = historyCallback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getMessages(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), room_id), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    messageHistoryModel = (MessageHistoryModel) data;
                    onPostExecute(true);
                }
            }

            @Override
            public void failed() {
                onPostExecute(false);
            }
        });
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean && historyCallback != null) {
            historyCallback.success(messageHistoryModel);
        }
    }
}