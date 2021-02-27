package com.shorbgy.ecommerceapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Cart implements Parcelable{

    private String date;
    private String discount;
    private String name;
    private String pid;
    private String price;
    private String time;
    private String image_url;
    private String quantity;
    private String total_pieces;

    public Cart() {
    }

    public Cart(String name, String price, String quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    protected Cart(Parcel in) {
        date = in.readString();
        discount = in.readString();
        name = in.readString();
        pid = in.readString();
        price = in.readString();
        time = in.readString();
        image_url = in.readString();
        quantity = in.readString();
        total_pieces = in.readString();
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getTotal_pieces() {
        return total_pieces;
    }

    public void setTotal_pieces(String total_pieces) {
        this.total_pieces = total_pieces;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(discount);
        dest.writeString(name);
        dest.writeString(pid);
        dest.writeString(price);
        dest.writeString(time);
        dest.writeString(image_url);
        dest.writeString(quantity);
        dest.writeString(total_pieces);
    }
}
