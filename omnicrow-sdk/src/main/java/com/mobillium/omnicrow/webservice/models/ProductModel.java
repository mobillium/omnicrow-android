package com.mobillium.omnicrow.webservice.models;

/**
 * Created by oguzhandongul on 23/10/2017.
 */

public class ProductModel {
    private int quantity;
    private String id;
    private double price;

    public ProductModel(int quantity, String id, double price) {
        this.quantity = quantity;
        this.id = id;
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
