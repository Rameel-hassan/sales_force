package com.app.salesforce.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerResponse {
    @SerializedName("Exception")
    @Expose
    private Object exception;
    @SerializedName("ResultType")
    @Expose
    private int resultType;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("ValidationErrors")
    @Expose
    private Object validationErrors;

    public Object getException() {
        return exception;
    }

    public void setException(Object exception) {
        this.exception = exception;
    }

    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Object validationErrors) {
        this.validationErrors = validationErrors;
    }
}
