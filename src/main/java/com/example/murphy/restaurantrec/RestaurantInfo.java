package com.example.murphy.restaurantrec;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RestaurantInfo extends Activity {
    //Data
    private HogsmeadeApplication appState;
    private ArrayList<Restaurant> restaurants;
    private boolean preferences[];

    //Views
    ImageView displayIMG;
    TextView name;
    TextView category;
    RatingBar rating;
    TextView phoneNum;
    TextView webURL;
    ImageView phoneIcon;
    ImageView webIcon;

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appState = (HogsmeadeApplication) getApplication();
        restaurants = appState.getRestaurantDB();
        preferences = new boolean[4];
        preferences = appState.getPreferences();
        setContentView(R.layout.info);

       // actionBar.setDisplayHomeAsUpEnabled(true);
       // actionBar.setHomeButtonEnabled(true);

        //Instantiate Views
        displayIMG = (ImageView) findViewById(R.id.restImage);
        name = (TextView) findViewById(R.id.restName);
        category = (TextView) findViewById(R.id.categoryInfo);
        rating = (RatingBar) findViewById(R.id.ratingBarDisplay);
        phoneNum = (TextView) findViewById(R.id.phoneNumber);
        webURL = (TextView) findViewById(R.id.webURL);
        phoneIcon = (ImageView) findViewById(R.id.imageView);
        webIcon = (ImageView) findViewById(R.id.imageView2);
        phoneIcon.setImageResource(R.drawable.ic_local_phone_black_24dp);
        webIcon.setImageResource(R.drawable.ic_desktop_windows_black_24dp);

        //Display the data according to the preferences
        Restaurant activeRestaurant = restaurants.get(appState.getActiveIndex());

        int resID = getResources().getIdentifier(activeRestaurant.getImageID(),
                "drawable", getPackageName());
        displayIMG.setImageResource(resID);
        name.setText(activeRestaurant.getName());
        rating.setRating(restaurants.get(appState.getActiveIndex()).getRating());

        /* Display content according to preferences */
        if (preferences[0] == true)//want phone number displayed
            phoneNum.setText(activeRestaurant.getPhone());
        else
            phoneNum.setVisibility(View.GONE);
            phoneIcon.setVisibility(View.GONE);
        if (preferences[1] == true)//want website displayed
            webURL.setText(activeRestaurant.getWeb());
        else
            webURL.setVisibility(View.GONE);
            webIcon.setVisibility(View.GONE);
        if (preferences[2] == true){
            category.setText(activeRestaurant.getCategory());
            category.setAllCaps(true);
        }
        else
            category.setVisibility(View.GONE);

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                restaurants.get(appState.getActiveIndex()).setRating(v);
                Toast.makeText(getApplicationContext(),"Rating has been changed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        appState.setRestaurantDB(restaurants); //save information when back button pressed
        finish();
    }
}
