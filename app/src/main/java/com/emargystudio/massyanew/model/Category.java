package com.emargystudio.massyanew.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {


    private int id;
    private int category_id;
    private String name;
    private String en_name;
    private int versionNumber;


    public Category(int id, int category_id, String name, String en_name, int versionNumber) {
        this.id = id;
        this.category_id = category_id;
        this.name = name;
        this.en_name = en_name;
        this.versionNumber = versionNumber;
    }


    public Category(int category_id, String name, String en_name, int versionNumber) {
        this.category_id = category_id;
        this.name = name;
        this.en_name = en_name;
        this.versionNumber = versionNumber;
    }


    protected Category(Parcel in) {
        id = in.readInt();
        category_id = in.readInt();
        name = in.readString();
        en_name = in.readString();
        versionNumber = in.readInt();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getEn_name() {
        return en_name;
    }

    public void setEn_name(String en_name) {
        this.en_name = en_name;
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
        dest.writeInt(category_id);
        dest.writeString(name);
        dest.writeString(en_name);
        dest.writeInt(versionNumber);
    }
}
