package com.example.myhomebuddy.ui.products;

import org.json.JSONObject;

public class Products {
    int id;
    String name;
    String image;
    String category;
    String subcategory;
    Double price;
    int quantity;
    JSONObject sellerProperties;

    public Products(
        int id,
        String name,
        String image,
        String category,
        String subcategory,
        Double price,
        int quantity,
        JSONObject sellerProperties
    ) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.category = category;
        this.subcategory = subcategory;
        this.price = price;
        this.quantity = quantity;
        this.sellerProperties = sellerProperties;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public JSONObject getSellerProperties() {
        return sellerProperties;
    }

    public void setSellerProperties(JSONObject sellerProperties) {
        this.sellerProperties = sellerProperties;
    }
}
