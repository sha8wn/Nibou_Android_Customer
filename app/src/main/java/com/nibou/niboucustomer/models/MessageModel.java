package com.nibou.niboucustomer.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MessageModel implements Serializable {

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("from_user_id")
    private String from_user_id;

    @SerializedName("text")
    private String text;

    @SerializedName("images")
    private List<ProfileModel> images;

    @SerializedName("to_user_id")
    private String to_user_id;

    @SerializedName("updated_at")
    private String updated_at;

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<ProfileModel> getImages() {
        return images;
    }

    public void setImages(List<ProfileModel> images) {
        this.images = images;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data implements Serializable {

        @SerializedName("attributes")
        private Attributes attributes;

        @SerializedName("relationships")
        private Relationships relationships;

        @SerializedName("id")
        private String id;

        @SerializedName("type")
        private String type;

        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

        public Relationships getRelationships() {
            return relationships;
        }

        public void setRelationships(Relationships relationships) {
            this.relationships = relationships;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public class Attributes implements Serializable {

        @SerializedName("created_at")
        private String created_at;

        @SerializedName("from_user_id")
        private String from_user_id;

        @SerializedName("text")
        private String text;

        @SerializedName("images")
        private List<ProfileModel> images;

        @SerializedName("to_user_id")
        private String to_user_id;

        @SerializedName("updated_at")
        private String updated_at;

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getFrom_user_id() {
            return from_user_id;
        }

        public void setFrom_user_id(String from_user_id) {
            this.from_user_id = from_user_id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<ProfileModel> getImages() {
            return images;
        }

        public void setImages(List<ProfileModel> images) {
            this.images = images;
        }

        public String getTo_user_id() {
            return to_user_id;
        }

        public void setTo_user_id(String to_user_id) {
            this.to_user_id = to_user_id;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }


    }

    public class Relationships implements Serializable {

        @SerializedName("room")
        private Room room;

        public Room getRoom() {
            return room;
        }

        public void setRoom(Room room) {
            this.room = room;
        }
    }

    public class Room implements Serializable {

        @SerializedName("data")
        private Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }
}
