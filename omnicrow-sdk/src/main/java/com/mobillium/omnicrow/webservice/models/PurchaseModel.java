package com.mobillium.omnicrow.webservice.models;

import java.util.ArrayList;

/**
 * Created by oguzhandongul on 23/10/2017.
 */

public class PurchaseModel extends BaseModel{
    private String id;
    private ArrayList<ProductModel> items = new ArrayList<>();
    private double total;

    public PurchaseModel(String id, ArrayList<ProductModel> items, double total) {
        this.id = id;
        this.items = items;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<ProductModel> getItems() {
        return items;
    }

    public void setItems(ArrayList<ProductModel> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
