package com.shorbgy.ecommerceapp.pojo;

public class Category {
    String category;
    int imageResource;

    public Category(String category, int imageResource) {
        this.category = category;
        this.imageResource = imageResource;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
