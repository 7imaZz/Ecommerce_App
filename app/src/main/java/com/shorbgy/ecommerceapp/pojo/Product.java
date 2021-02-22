package com.shorbgy.ecommerceapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private String pid;
    private String category;
    private String date;
    private String description;
    private String image_url;
    private String price;
    private String product_name;
    private String time;

    public Product() {
    }


    protected Product(Parcel in) {
        pid = in.readString();
        category = in.readString();
        date = in.readString();
        description = in.readString();
        image_url = in.readString();
        price = in.readString();
        product_name = in.readString();
        time = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pid);
        dest.writeString(category);
        dest.writeString(date);
        dest.writeString(description);
        dest.writeString(image_url);
        dest.writeString(price);
        dest.writeString(product_name);
        dest.writeString(time);
    }
}
