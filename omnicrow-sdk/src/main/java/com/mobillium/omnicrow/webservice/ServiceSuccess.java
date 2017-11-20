package com.mobillium.omnicrow.webservice;

/**
 * Created by oguzhandongul on 31/05/2017.
 */

public class ServiceSuccess {
    private String message;
    private String code;

    public ServiceSuccess(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
