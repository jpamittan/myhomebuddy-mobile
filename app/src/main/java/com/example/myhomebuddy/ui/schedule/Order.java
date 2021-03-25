package com.example.myhomebuddy.ui.schedule;

public class Order {
    int id;
    int product_id;
    String image;
    String product_name;
    String seller_name;
    String category;
    String sub_category;

    public Order(
        int id,
        int product_id,
        String image,
        String product_name,
        String seller_name,
        String category,
        String sub_category
    ) {
        this.id = id;
        this.product_id = product_id;
        this.image = image;
        this.product_name = product_name;
        this.seller_name = seller_name;
        this.category = category;
        this.sub_category = sub_category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }
}
