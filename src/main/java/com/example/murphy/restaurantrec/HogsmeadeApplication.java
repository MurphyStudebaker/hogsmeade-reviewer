package com.example.murphy.restaurantrec;

import android.app.Application;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class HogsmeadeApplication extends Application {
    private ArrayList<Restaurant> restaurantDB = new ArrayList<Restaurant>();
    private int activeIndex;
    private boolean preferences[] = new boolean[3];

    public void clearData() {
        restaurantDB.clear();
    }

    public void addRestaurant(Restaurant newRestaurant) {
        restaurantDB.add(newRestaurant);
    }

    /* Accessors and Mutators */
    public ArrayList<Restaurant> getRestaurantDB() {
        return restaurantDB;
    }

    public void setRestaurantDB(ArrayList<Restaurant> restaurantDB) {
        this.restaurantDB = restaurantDB;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public boolean[] getPreferences() {
        return preferences;
    }

    public void setPreferences(boolean phone, boolean web, boolean category) {
        preferences[0] = phone;
        preferences[1] = web;
        preferences[2] = category;
    }
}
