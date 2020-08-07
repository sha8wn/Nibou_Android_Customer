package com.nibou.niboucustomer.models;

import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import com.nibou.niboucustomer.utils.DateFormatUtil;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class ActiveChatSessionModel implements Serializable {

    @SerializedName("data")
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void sort() {
        if (getData() != null)
            Collections.sort(getData());
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data implements Serializable, Comparable<Data> {
        @SerializedName("attributes")
        private Attributes attributes;

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

        @Override
        public int compareTo(@NonNull Data o) {
            try {
                if (o.getAttributes().getLast_message() != null && getAttributes().getLast_message() != null) {
                    if (Long.parseLong(DateFormatUtil.getServerMilliSeconds(o.getAttributes().getLast_message().getData().getAttributes().getCreated_at())) > Long.parseLong(DateFormatUtil.getServerMilliSeconds(getAttributes().getLast_message().getData().getAttributes().getCreated_at()))) {
                        return 1;
                    } else if (Long.parseLong(DateFormatUtil.getServerMilliSeconds(o.getAttributes().getLast_message().getData().getAttributes().getCreated_at())) == Long.parseLong(DateFormatUtil.getServerMilliSeconds(getAttributes().getLast_message().getData().getAttributes().getCreated_at()))) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }
    }

    public class Attributes implements Serializable {

        @SerializedName("users")
        private List<ProfileModel> users;

        @SerializedName("last_message")
        private MessageModel last_message;

        @SerializedName("expertises")
        private List<ExpertiseModel> expertises;

        @SerializedName("updated_at")
        private String updated_at;

        @SerializedName("created_at")
        private String created_at;

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public List<ProfileModel> getUsers() {
            return users;
        }

        public void setUsers(List<ProfileModel> users) {
            this.users = users;
        }

        public List<ExpertiseModel> getExpertises() {
            return expertises;
        }

        public void setExpertises(List<ExpertiseModel> expertises) {
            this.expertises = expertises;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public MessageModel getLast_message() {
            return last_message;
        }

        public void setLast_message(MessageModel last_message) {
            this.last_message = last_message;
        }
    }

}
