package com.nibou.niboucustomer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PaymentListModel implements Serializable {

    @SerializedName("data")
    @Expose
    private List<PaymentModel.Data> data;

    @SerializedName("meta")
    @Expose
    private Meta meta;

    public List<PaymentModel.Data> getData() {
        return data;
    }

    public void setData(List<PaymentModel.Data> data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public class Meta {

        @SerializedName("current_page")
        @Expose
        private int current_page;

        @SerializedName("next_page")
        @Expose
        private String next_page;

        @SerializedName("prev_page")
        @Expose
        private String prev_page;

        @SerializedName("total_pages")
        @Expose
        private int total_pages;

        @SerializedName("total_count")
        @Expose
        private int total_count;

        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public String getNext_page() {
            return next_page;
        }

        public void setNext_page(String next_page) {
            this.next_page = next_page;
        }

        public String getPrev_page() {
            return prev_page;
        }

        public void setPrev_page(String prev_page) {
            this.prev_page = prev_page;
        }

        public int getTotal_pages() {
            return total_pages;
        }

        public void setTotal_pages(int total_pages) {
            this.total_pages = total_pages;
        }

        public int getTotal_count() {
            return total_count;
        }

        public void setTotal_count(int total_count) {
            this.total_count = total_count;
        }
    }
}
