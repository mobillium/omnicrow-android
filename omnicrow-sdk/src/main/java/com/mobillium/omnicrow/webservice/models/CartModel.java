package com.mobillium.omnicrow.webservice.models;

import java.util.ArrayList;

/**
 * Created by oguzhandongul on 23/10/2017.
 */

public class CartModel extends BaseModel{
    private ArrayList<ProductModel> items = new ArrayList<>();

    public CartModel(ArrayList<ProductModel> items) {
        this.items = items;
    }

    public ArrayList<ProductModel> getItems() {
        return items;
    }

    public void setItems(ArrayList<ProductModel> items) {
        this.items = items;
    }
}
