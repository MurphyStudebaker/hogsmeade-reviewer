package com.example.murphy.restaurantrec;

public class Restaurant {
    private String name;
    private String phone;
    private String web;
    private String category;
    private float rating;
    private String imageID;

    public Restaurant(String n, String p, String w, String c, float r) { //without img
        name = n;
        phone = p;
        web = w;
        category = c;
        rating = r;
        imageID = "hogsmeade";
    }
    public Restaurant(String n, String p, String w, String c, float r, String img) { //with img
        name = n;
        phone = p;
        web = w;
        category = c;
        rating = r;
        imageID = img;
    }

    /* Accessors and Mutators */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }
}
