package com.nibou.niboucustomer.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.nibou.niboucustomer.utils.DateFormatUtil;

import java.io.Serializable;
import java.util.List;

public class ProfileModel extends ErrorModel implements Serializable {

    @SerializedName("data")
    private Data data;

    @SerializedName("included")
    private List<Data> included;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public List<Data> getIncluded() {
        return included;
    }

    public void setIncluded(List<Data> included) {
        this.included = included;
    }

    public class Data implements Serializable, Comparable<Data> {
        @SerializedName("attributes")
        private Attributes attributes;

        @SerializedName("relationships")
        private Relationships relationships;

        @SerializedName("id")
        private String id;

        @SerializedName("type")
        private String type;

        public Relationships getRelationships() {
            return relationships;
        }

        public void setRelationships(Relationships relationships) {
            this.relationships = relationships;
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }


        @Override
        public int compareTo(@NonNull Data o) {
            if (o.getAttributes().getDay_number() < getAttributes().getDay_number()) {
                return 1;
            } else if (o.getAttributes().getDay_number() == getAttributes().getDay_number()) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    public class Attributes implements Serializable {

        @SerializedName("text")
        private String text;

        @SerializedName("exp_date")
        private String exp_date;

        @SerializedName("is_default")
        private boolean is_default;

        @SerializedName("is_active")
        private boolean is_active;

        private boolean is_server_default;

        @SerializedName("card_type")
        private String card_type;

        @SerializedName("last_numbers")
        private String last_numbers;

        @SerializedName("code")
        private String code;

        @SerializedName("title")
        private String title;

        @SerializedName("comment")
        private String comment;

        @SerializedName("value")
        private int value;

        @SerializedName("active")
        private String active;

        @SerializedName("country")
        private String country;

        @SerializedName("city")
        private String city;

        @SerializedName("username")
        private String username;

        @SerializedName("gender")
        private String gender;

        @SerializedName("short_bio")
        private String short_bio;

        @SerializedName("account_type")
        private String account_type;

        @SerializedName("timezone")
        private String timezone;

        @SerializedName("nationality")
        private String nationality;

        @SerializedName("dob")
        private String dob;

        @SerializedName("created_at")
        private String created_at;

        @SerializedName("updated_at")
        private String updated_at;

        @SerializedName("email")
        private String email;

        @SerializedName("name")
        private String name;

        @SerializedName("avatar")
        private Avatar avatar;

        @SerializedName("images")
        private List<Avatar> images;

        @SerializedName("pdf")
        private Pdf pdf;

        @SerializedName("day_number")
        private int day_number;

        @SerializedName("time_from")
        private String time_from;

        @SerializedName("time_to")
        private String time_to;

        @SerializedName("online")
        private boolean online;

        @SerializedName("available")
        private boolean available;

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public boolean isOnline() {
            return online;
        }

        public void setOnline(boolean online) {
            this.online = online;
        }

        public boolean isIs_active() {
            return is_active;
        }

        public void setIs_active(boolean is_active) {
            this.is_active = is_active;
        }

        public boolean isIs_server_default() {
            return is_server_default;
        }

        public void setIs_server_default(boolean is_server_default) {
            this.is_server_default = is_server_default;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getExp_date() {
            return exp_date;
        }

        public void setExp_date(String exp_date) {
            this.exp_date = exp_date;
        }

        public boolean isIs_default() {
            return is_default;
        }

        public void setIs_default(boolean is_default) {
            this.is_default = is_default;
        }

        public String getCard_type() {
            return card_type;
        }

        public void setCard_type(String card_type) {
            this.card_type = card_type;
        }

        public String getLast_numbers() {
            return last_numbers;
        }

        public void setLast_numbers(String last_numbers) {
            this.last_numbers = last_numbers;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public int getDay_number() {
            return day_number;
        }

        public void setDay_number(int day_number) {
            this.day_number = day_number;
        }

        public String getTime_from() {
            return time_from;
        }

        public void setTime_from(String time_from) {
            this.time_from = time_from;
        }

        public String getTime_to() {
            return time_to;
        }

        public void setTime_to(String time_to) {
            this.time_to = time_to;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getShort_bio() {
            return short_bio;
        }

        public void setShort_bio(String short_bio) {
            this.short_bio = short_bio;
        }

        public String getAccount_type() {
            return account_type;
        }

        public void setAccount_type(String account_type) {
            this.account_type = account_type;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Avatar getAvatar() {
            return avatar;
        }

        public void setAvatar(Avatar avatar) {
            this.avatar = avatar;
        }

        public List<Avatar> getImages() {
            return images;
        }

        public void setImages(List<Avatar> images) {
            this.images = images;
        }

        public Pdf getPdf() {
            return pdf;
        }

        public void setPdf(Pdf pdf) {
            this.pdf = pdf;
        }
    }

    public class Relationships implements Serializable {
        @SerializedName("customer")
        private Customer customer;

        @SerializedName("user_credit_cards")
        private CreditCard user_credit_cards;

        public Customer getCustomer() {
            return customer;
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }

        public CreditCard getUser_credit_cards() {
            return user_credit_cards;
        }

        public void setUser_credit_cards(CreditCard user_credit_cards) {
            this.user_credit_cards = user_credit_cards;
        }
    }


    public class CreditCard implements Serializable {
        @SerializedName("data")
        private List<Data> data;

        public List<Data> getData() {
            return data;
        }

        public void setData(List<Data> data) {
            this.data = data;
        }
    }


    public class Customer implements Serializable {
        @SerializedName("data")
        private CustomerData data;

        public CustomerData getData() {
            return data;
        }

        public void setData(CustomerData data) {
            this.data = data;
        }
    }

    public class CustomerData implements Serializable {
        @SerializedName("id")
        private String id;

        @SerializedName("type")
        private String type;

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

    public class Avatar implements Serializable {
        @SerializedName("url")
        private String url;

        @SerializedName("w220")
        private W220 w220;


        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public W220 getW220() {
            return w220;
        }

        public void setW220(W220 w220) {
            this.w220 = w220;
        }
    }

    public class Pdf implements Serializable {
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public class W220 implements Serializable {
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}




