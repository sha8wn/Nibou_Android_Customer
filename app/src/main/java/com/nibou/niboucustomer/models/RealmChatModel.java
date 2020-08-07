package com.nibou.niboucustomer.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import java.io.Serializable;

public class RealmChatModel extends RealmObject implements Serializable {

    @PrimaryKey
    @Required
    private String messageId;
    private String chatId;

    private String userId;
    private String message;
    private String images;
    private String localImageUrl;
    private String timeStamp;

    private boolean isSending;
    private boolean isDownloading;
    private boolean isError;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getLocalImageUrl() {
        return localImageUrl;
    }

    public void setLocalImageUrl(String localImageUrl) {
        this.localImageUrl = localImageUrl;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isSending() {
        return isSending;
    }

    public void setSending(boolean sending) {
        isSending = sending;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }
}