package com.mobillium.omnicrow.webservice.models;

import com.google.gson.annotations.SerializedName;

import static com.mobillium.omnicrow.OmniCrow.generateUUID;

/**
 * Created by oguzhandongul on 23/10/2017.
 */

public class ItemModel {
    private String uuid;
    @SerializedName("user-id")
    private String user_id;
    private String id;

    public ItemModel(String user_id, String id) {
        this.uuid = generateUUID();
        this.user_id = user_id;
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
