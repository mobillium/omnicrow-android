package com.mobillium.omnicrow.webservice.models;

/**
 * Created by oguzhandongul on 23/10/2017.
 */

public class CategoryModel extends BaseModel {

    private String path;

    public CategoryModel(String user_id, String path) {
        super(user_id);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
