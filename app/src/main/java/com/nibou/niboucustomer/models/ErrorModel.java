package com.nibou.niboucustomer.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ErrorModel implements Serializable {

    @SerializedName("errors")
    private ArrayList<Error> errors;

    @SerializedName("error")
    private String error;

    @SerializedName("error_description")
    private String error_description;

    public ArrayList<Error> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<Error> errors) {
        this.errors = errors;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public class Error implements Serializable {
        @SerializedName("detail")
        private String detail;

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }
}
