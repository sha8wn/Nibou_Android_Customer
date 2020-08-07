package com.nibou.niboucustomer.actioncable;

import android.content.Context;
import com.nibou.niboucustomer.api.ApiClient;
import com.nibou.niboucustomer.api.ApiEndPoint;
import com.nibou.niboucustomer.api.ApiHandler;
import com.nibou.niboucustomer.models.MessageHistoryModel;
import com.nibou.niboucustomer.models.MessageModel;
import com.nibou.niboucustomer.utils.AppConstant;
import com.nibou.niboucustomer.utils.ChatConstants;
import com.nibou.niboucustomer.utils.LocalPrefences;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MessageApiCall {
    private String taskId;
    private Context context;

    public MessageApiCall(Context context, String taskId) {
        this.taskId = taskId;
        this.context = context;
    }

    public void sendMessageNetworkCall(String room_id, RequestBody text, List<MultipartBody.Part> images, Callback callback) {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).sendMessage(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), room_id, text, images), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    if (callback != null)
                        callback.success(taskId, (MessageModel) data);
                } else {
                    if (callback != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(data.toString());
                            if (((JSONObject) jsonObject.getJSONArray("errors").get(0)).getJSONObject("source").get("pointer").equals("/data/attributes/credit_card")) {
                                callback.failed(taskId, true, null);
                            } else {
                                callback.failed(taskId, false, ((JSONObject) jsonObject.getJSONArray("errors").get(0)).getString("detail"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.failed(taskId, false, null);
                        }
                    }
                }
            }

            @Override
            public void failed() {
                if (callback != null)
                    callback.failed(taskId, false, null);
            }
        });
    }

    public interface Callback {
        void success(String taskId, MessageModel messageModel);

        void failed(String taskId, boolean isCardError, String error);
    }
}