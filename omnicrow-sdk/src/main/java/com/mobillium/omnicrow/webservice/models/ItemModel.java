package com.mobillium.omnicrow.webservice.models;

/**
 * Created by oguzhandongul on 23/10/2017.
 */

public class ItemModel extends BaseModel{
    private String id;

    public ItemModel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
