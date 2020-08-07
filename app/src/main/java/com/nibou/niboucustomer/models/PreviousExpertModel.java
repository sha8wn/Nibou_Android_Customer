package com.nibou.niboucustomer.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PreviousExpertModel implements Serializable {

    @SerializedName("data")
    private List<ProfileModel.Data> data;

    public List<ProfileModel.Data> getData() {
        return data;
    }

    public void setData(List<ProfileModel.Data> data) {
        this.data = data;
    }
}
