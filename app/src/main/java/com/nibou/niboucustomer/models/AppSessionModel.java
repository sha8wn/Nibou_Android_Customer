package com.nibou.niboucustomer.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AppSessionModel implements Serializable {

    @SerializedName("action")
    @Expose
    private String action;

    @SerializedName("timeout")
    @Expose
    private String timeout;

    @SerializedName("room_id")
    @Expose
    private String room_id;

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("notification")
    @Expose
    private Notification notification;

    @SerializedName("message")
    @Expose
    private MessageModel message;

    @SerializedName("room")
    @Expose
    private Room room;

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public MessageModel getMessage() {
        return message;
    }

    public void setMessage(MessageModel message) {
        this.message = message;
    }

    public class Room implements Serializable {

        @SerializedName("data")
        @Expose
        private Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

    }

    public class Notification implements Serializable {

        @SerializedName("icon")
        @Expose
        private String icon;

        @SerializedName("body")
        @Expose
        private String body;

        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("user_id")
        @Expose
        private String user_id;

        @SerializedName("user_name")
        @Expose
        private String user_name;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }
    }

    public class Data implements Serializable {

        @SerializedName("attributes")
        @Expose
        private Attributes attributes;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("relationships")
        @Expose
        private Relationships relationships;
        @SerializedName("type")
        @Expose
        private String type;

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

        public Relationships getRelationships() {
            return relationships;
        }

        public void setRelationships(Relationships relationships) {
            this.relationships = relationships;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }

    public class Attributes implements Serializable {

        @SerializedName("active")
        @Expose
        private Boolean active;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("expertises")
        @Expose
        private List<Expertise> expertises = null;
        @SerializedName("is_private")
        @Expose
        private Boolean isPrivate;

        @SerializedName("last_message")
        @Expose
        private MessageModel lastMessage;

        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("users")
        @Expose
        private List<User> users = null;

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public List<Expertise> getExpertises() {
            return expertises;
        }

        public void setExpertises(List<Expertise> expertises) {
            this.expertises = expertises;
        }

        public Boolean getIsPrivate() {
            return isPrivate;
        }

        public void setIsPrivate(Boolean isPrivate) {
            this.isPrivate = isPrivate;
        }

        public MessageModel getLastMessage() {
            return lastMessage;
        }

        public void setLastMessage(MessageModel lastMessage) {
            this.lastMessage = lastMessage;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

    }

    public class Relationships implements Serializable {

        @SerializedName("bookmarks")
        @Expose
        private Bookmarks bookmarks;

        public Bookmarks getBookmarks() {
            return bookmarks;
        }

        public void setBookmarks(Bookmarks bookmarks) {
            this.bookmarks = bookmarks;
        }

    }

    public class Bookmarks implements Serializable {

        @SerializedName("data")
        @Expose
        private List<Object> data = null;

        public List<Object> getData() {
            return data;
        }

        public void setData(List<Object> data) {
            this.data = data;
        }

    }

    public class Expertise implements Serializable {

        @SerializedName("data")
        @Expose
        private ExpertiseData data;

        public ExpertiseData getData() {
            return data;
        }

        public void setData(ExpertiseData data) {
            this.data = data;
        }

    }

    public class ExpertiseData implements Serializable {

        @SerializedName("attributes")
        @Expose
        private ExpertiseAttributes attributes;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("type")
        @Expose
        private String type;

        public ExpertiseAttributes getAttributes() {
            return attributes;
        }

        public void setAttributes(ExpertiseAttributes attributes) {
            this.attributes = attributes;
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

    public class ExpertiseAttributes implements Serializable {

        @SerializedName("active")
        @Expose
        private Boolean active;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

    }


}
