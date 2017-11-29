package com.mobillium.omnicrow.webservice.models;

/**
 * Created by oguzhandongul on 23/10/2017.
 */

public class PushModel extends BaseModel {
    private String token;

    public PushModel(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}


