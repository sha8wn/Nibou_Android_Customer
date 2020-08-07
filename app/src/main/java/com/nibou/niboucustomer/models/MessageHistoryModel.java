package com.nibou.niboucustomer.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MessageHistoryModel implements Serializable {

    @SerializedName("data")
    private List<MessageModel.Data> data;

    public List<MessageModel.Data> getData() {
        return data;
    }

    public void setData(List<MessageModel.Data> data) {
        this.data = data;
    }
}
