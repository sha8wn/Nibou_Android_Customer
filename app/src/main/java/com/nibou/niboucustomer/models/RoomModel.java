package com.nibou.niboucustomer.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RoomModel implements Serializable {

    @SerializedName("data")
    private ActiveChatSessionModel.Data data;

    public ActiveChatSessionModel.Data getData() {
        return data;
    }

    public void setData(ActiveChatSessionModel.Data data) {
        this.data = data;
    }
}
