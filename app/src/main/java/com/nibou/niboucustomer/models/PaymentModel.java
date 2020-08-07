package com.nibou.niboucustomer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentModel implements Serializable {

    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public class Data {

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("attributes")
        @Expose
        private Attributes attributes;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public class Attributes {

        @SerializedName("total_seconds")
        @Expose
        private String totalSeconds;
        @SerializedName("amount")
        @Expose
        private String amount;

        @SerializedName("date_at")
        @Expose
        private String date_at;

        @SerializedName("payed")
        @Expose
        private boolean payed;

        @SerializedName("room_id")
        @Expose
        private String room_id;

        @SerializedName("created_at")
        @Expose
        private String created_at;
        @SerializedName("room")
        @Expose
        private RoomModel roomModel;

        public RoomModel getRoom() {
            return roomModel;
        }

        public void setRoom(RoomModel roomModel) {
            this.roomModel = roomModel;
        }



        public String getTotalSeconds() {
            return totalSeconds;
        }

        public void setTotalSeconds(String totalSeconds) {
            this.totalSeconds = totalSeconds;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getDate_at() {
            return date_at;
        }

        public void setDate_at(String date_at) {
            this.date_at = date_at;
        }

        public boolean isPayed() {
            return payed;
        }

        public void setPayed(boolean payed) {
            this.payed = payed;
        }

        public String getRoom_id() {
            return room_id;
        }

        public void setRoom_id(String room_id) {
            this.room_id = room_id;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}
