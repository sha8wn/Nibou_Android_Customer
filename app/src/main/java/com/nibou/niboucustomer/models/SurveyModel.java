package com.nibou.niboucustomer.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SurveyModel implements Serializable {

    @SerializedName("data")
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data implements Serializable {
        @SerializedName("attributes")
        private Attributes attributes;

        @SerializedName("id")
        private String id;

        @SerializedName("type")
        private String type;

        public Data() {
            attributes = new Attributes();
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

        private boolean isSelected;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }

    public static class Attributes implements Serializable {

        @SerializedName("title")
        private String title;

        @SerializedName("active")
        private boolean active;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }


    private ArrayList<String> selectedSurveyList;

    public ArrayList<String> getSelectedSurveyList() {
        return selectedSurveyList;
    }

    public void setSelectedSurveyList(ArrayList<String> selectedSurveyList) {
        this.selectedSurveyList = selectedSurveyList;
    }
}
