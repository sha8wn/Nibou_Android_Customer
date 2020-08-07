package com.nibou.niboucustomer.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class ReviewModel implements Serializable {

    @SerializedName("data")
    private List<ProfileModel.Data> data;

    @SerializedName("included")
    private List<ProfileModel.Data> included;

    public List<ProfileModel.Data> getData() {
        return data;
    }

    public void setData(List<ProfileModel.Data> data) {
        this.data = data;
    }

    public List<ProfileModel.Data> getIncluded() {
        return included;
    }

    public void setIncluded(List<ProfileModel.Data> included) {
        this.included = included;
    }


    private String name;
    private String description;
    private boolean isSurveyValue;

    public ReviewModel(boolean isSurveyValue, String name) {
        this.isSurveyValue = isSurveyValue;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSurveyValue() {
        return isSurveyValue;
    }

    public void setSurveyValue(boolean surveyValue) {
        isSurveyValue = surveyValue;
    }

    public void reverseReviewList() {
        if (getData() != null)
            Collections.reverse(getData());
    }

}
