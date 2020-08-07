package com.nibou.niboucustomer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SessionTimeoutModel {

    @SerializedName("action")
    @Expose
    private String action;
    @SerializedName("room_id")
    @Expose
    private String roomId;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
