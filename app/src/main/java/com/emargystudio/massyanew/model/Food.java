package com.emargystudio.massyanew.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;


public class Food implements Parcelable {

    private int id;
    private int food_id;
    private int category_id;
    private String name , description, description2, description3,image_uri ,en_name , en_description, en_description2, en_description3
            , en_description4, description4;
    private int price , price2 ,price3,price4 ,versionNumber;


    public Food(int id, int food_id, int category_id, String name, String description, String description2,
                String description3, String image_uri, String en_name, String en_description, String en_description2,
                String en_description3, String en_description4, String description4,
                int price, int price2, int price3, int price4, int versionNumber) {
        this.id = id;
        this.food_id = food_id;
        this.category_id = category_id;
        this.name = name;
        this.description = description;
        this.description2 = description2;
        this.description3 = description3;
        this.image_uri = image_uri;
        this.en_name = en_name;
        this.en_description = en_description;
        this.en_description2 = en_description2;
        this.en_description3 = en_description3;
        this.en_description4 = en_description4;
        this.description4 = description4;
        this.price = price;
        this.price2 = price2;
        this.price3 = price3;
        this.price4 = price4;
        this.versionNumber = versionNumber;
    }


    public Food(int food_id, int category_id, String name, String description, String description2, String description3, String image_uri,
                String en_name, String en_description, String en_description2, String en_description3,
                String en_description4, String description4, int price, int price2, int price3, int price4, int versionNumber) {
        this.food_id = food_id;
        this.category_id = category_id;
        this.name = name;
        this.description = description;
        this.description2 = description2;
        this.description3 = description3;
        this.image_uri = image_uri;
        this.en_name = en_name;
        this.en_description = en_description;
        this.en_description2 = en_description2;
        this.en_description3 = en_description3;
        this.en_description4 = en_description4;
        this.description4 = description4;
        this.price = price;
        this.price2 = price2;
        this.price3 = price3;
        this.price4 = price4;
        this.versionNumber = versionNumber;
    }


    protected Food(Parcel in) {
        id = in.readInt();
        food_id = in.readInt();
        category_id = in.readInt();
        name = in.readString();
        description = in.readString();
        description2 = in.readString();
        description3 = in.readString();
        image_uri = in.readString();
        en_name = in.readString();
        en_description = in.readString();
        en_description2 = in.readString();
        en_description3 = in.readString();
        en_description4 = in.readString();
        description4 = in.readString();
        price = in.readInt();
        price2 = in.readInt();
        price3 = in.readInt();
        price4 = in.readInt();
        versionNumber = in.readInt();
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFood_id() {
        return food_id;
    }

    public void setFood_id(int food_id) {
        this.food_id = food_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getDescription3() {
        return description3;
    }

    public void setDescription3(String description3) {
        this.description3 = description3;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public String getEn_name() {
        return en_name;
    }

    public void setEn_name(String en_name) {
        this.en_name = en_name;
    }

    public String getEn_description() {
        return en_description;
    }

    public void setEn_description(String en_description) {
        this.en_description = en_description;
    }

    public String getEn_description2() {
        return en_description2;
    }

    public void setEn_description2(String en_description2) {
        this.en_description2 = en_description2;
    }

    public String getEn_description3() {
        return en_description3;
    }

    public void setEn_description3(String en_description3) {
        this.en_description3 = en_description3;
    }

    public String getEn_description4() {
        return en_description4;
    }

    public void setEn_description4(String en_description4) {
        this.en_description4 = en_description4;
    }

    public String getDescription4() {
        return description4;
    }

    public void setDescription4(String description4) {
        this.description4 = description4;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice2() {
        return price2;
    }

    public void setPrice2(int price2) {
        this.price2 = price2;
    }

    public int getPrice3() {
        return price3;
    }

    public void setPrice3(int price3) {
        this.price3 = price3;
    }

    public int getPrice4() {
        return price4;
    }

    public void setPrice4(int price4) {
        this.price4 = price4;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(food_id);
        dest.writeInt(category_id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(description2);
        dest.writeString(description3);
        dest.writeString(image_uri);
        dest.writeString(en_name);
        dest.writeString(en_description);
        dest.writeString(en_description2);
        dest.writeString(en_description3);
        dest.writeString(en_description4);
        dest.writeString(description4);
        dest.writeInt(price);
        dest.writeInt(price2);
        dest.writeInt(price3);
        dest.writeInt(price4);
        dest.writeInt(versionNumber);
    }
}
