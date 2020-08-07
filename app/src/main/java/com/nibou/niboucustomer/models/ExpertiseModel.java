package com.nibou.niboucustomer.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ExpertiseModel implements Serializable {

    @SerializedName("data")
    private SurveyModel.Data data;

    public SurveyModel.Data getData() {
        return data;
    }

    public void setData(SurveyModel.Data data) {
        this.data = data;
    }
}
