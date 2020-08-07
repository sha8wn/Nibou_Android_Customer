package com.nibou.niboucustomer.models;

import java.io.Serializable;

public class CardModel implements Serializable {
    private String name;
    private boolean isSelected;
    private boolean isExpired;
    private boolean isDefault;

    public CardModel(String name,boolean isDefault,boolean isExpired) {
        this.name = name;
        this.isDefault=isDefault;
        this.isExpired=isExpired;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
