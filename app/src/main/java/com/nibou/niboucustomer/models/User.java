package com.nibou.niboucustomer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    @SerializedName("data")
    @Expose
    private UserData data;


    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    public class UserData implements Serializable{

        @SerializedName("attributes")
        @Expose
        private UserAttributes attributes;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("relationships")
        @Expose
        private UserRelationships relationships;
        @SerializedName("type")
        @Expose
        private String type;

        public UserAttributes getAttributes() {
            return attributes;
        }

        public void setAttributes(UserAttributes attributes) {
            this.attributes = attributes;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public UserRelationships getRelationships() {
            return relationships;
        }

        public void setRelationships(UserRelationships relationships) {
            this.relationships = relationships;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }

    public class UserAttributes implements Serializable{

        @SerializedName("account_type")
        @Expose
        private Integer accountType;
        @SerializedName("avatar")
        @Expose
        private Avatar avatar;
        @SerializedName("city")
        @Expose
        private Object city;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("dob")
        @Expose
        private Object dob;
        @SerializedName("gender")
        @Expose
        private Object gender;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("nationality")
        @Expose
        private Object nationality;
        @SerializedName("online")
        @Expose
        private String online;
        @SerializedName("pdf")
        @Expose
        private Avatar pdf;
        @SerializedName("short_bio")
        @Expose
        private Object shortBio;
        @SerializedName("timezone")
        @Expose
        private String timezone;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("username")
        @Expose
        private String username;

        public Integer getAccountType() {
            return accountType;
        }

        public void setAccountType(Integer accountType) {
            this.accountType = accountType;
        }

        public Avatar getAvatar() {
            return avatar;
        }

        public void setAvatar(Avatar avatar) {
            this.avatar = avatar;
        }

        public Object getCity() {
            return city;
        }

        public void setCity(Object city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public Object getDob() {
            return dob;
        }

        public void setDob(Object dob) {
            this.dob = dob;
        }

        public Object getGender() {
            return gender;
        }

        public void setGender(Object gender) {
            this.gender = gender;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getNationality() {
            return nationality;
        }

        public void setNationality(Object nationality) {
            this.nationality = nationality;
        }

        public String getOnline() {
            return online;
        }

        public void setOnline(String online) {
            this.online = online;
        }

        public Avatar getPdf() {
            return pdf;
        }

        public void setPdf(Avatar pdf) {
            this.pdf = pdf;
        }

        public Object getShortBio() {
            return shortBio;
        }

        public void setShortBio(Object shortBio) {
            this.shortBio = shortBio;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

    }

    public class UserRelationships implements Serializable{

        @SerializedName("expertises")
        @Expose
        private Expertises expertises;
        @SerializedName("languages")
        @Expose
        private Expertises languages;
        @SerializedName("reviews")
        @Expose
        private Expertises reviews;
        @SerializedName("user_credit_cards")
        @Expose
        private Expertises userCreditCards;
        @SerializedName("user_timings")
        @Expose
        private Expertises userTimings;

        public Expertises getExpertises() {
            return expertises;
        }

        public void setExpertises(Expertises expertises) {
            this.expertises = expertises;
        }

        public Expertises getLanguages() {
            return languages;
        }

        public void setLanguages(Expertises languages) {
            this.languages = languages;
        }

        public Expertises getReviews() {
            return reviews;
        }

        public void setReviews(Expertises reviews) {
            this.reviews = reviews;
        }

        public Expertises getUserCreditCards() {
            return userCreditCards;
        }

        public void setUserCreditCards(Expertises userCreditCards) {
            this.userCreditCards = userCreditCards;
        }

        public Expertises getUserTimings() {
            return userTimings;
        }

        public void setUserTimings(Expertises userTimings) {
            this.userTimings = userTimings;
        }

    }


    public class Expertises implements Serializable{

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

    public class Avatar implements Serializable{

        @SerializedName("url")
        @Expose
        private Object url;
        @SerializedName("w220")
        @Expose
        private W220 w220;
        @SerializedName("w50")
        @Expose
        private W220 w50;

        public Object getUrl() {
            return url;
        }

        public void setUrl(Object url) {
            this.url = url;
        }

        public W220 getW220() {
            return w220;
        }

        public void setW220(W220 w220) {
            this.w220 = w220;
        }

        public W220 getW50() {
            return w50;
        }

        public void setW50(W220 w50) {
            this.w50 = w50;
        }

    }

    public class W220 implements Serializable{

        @SerializedName("url")
        @Expose
        private Object url;

        public Object getUrl() {
            return url;
        }

        public void setUrl(Object url) {
            this.url = url;
        }

    }

}
