package com.example.murphy.restaurantrec;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddRestaurant extends Activity {
    //Data
    HogsmeadeApplication appState;
    public static final String FIVE_STAR_ADDED = "com.example.FIVE_STAR_ADDED";

    //Views
    TextView name;
    TextView phone;
    TextView webURL;
    RatingBar rating;
    Spinner category;
    Button addRestaurant;
    Button cancel;

    String selectedCat = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appState = (HogsmeadeApplication) getApplication();
        setContentView(R.layout.add_rest);

        //Views
        name = (TextView) findViewById(R.id.inputName);
        phone = (TextView) findViewById(R.id.inputPhone);
        webURL = (TextView) findViewById(R.id.inputWeb);
        rating = (RatingBar) findViewById(R.id.inputRating);
        addRestaurant = (Button) findViewById(R.id.addRest);
        cancel = (Button) findViewById(R.id.cancelBtn);

        /* Configure Dropdown spinner */
        category = (Spinner) findViewById(R.id.selectCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);


        addRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = name.getText().toString();
                String newPhone = phone.getText().toString();
                String newWeb = webURL.getText().toString();
                float newRating = rating.getRating();

                if (newRating == 5) {
                    //send broadcast intent to a notification
                    int currentIndex = appState.getRestaurantDB().size(); //will be the index of the added restaurant
                    Intent bc = new Intent(FIVE_STAR_ADDED);
                    bc.putExtra("INDEX:",currentIndex);
                    AddRestaurant.this.sendBroadcast(bc);








                    Intent addedRestaurant = new Intent(getApplicationContext(), AddRestaurant.class);
                    addedRestaurant.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, addedRestaurant, 0);
                    //NotificationChannel channel = new NotificationChannel(NotificationChannel.DEFAULT_CHANNEL_ID, "channel", NotificationManager.IMPORTANCE_DEFAULT);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), NotificationChannel.DEFAULT_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle("Accio deliciousness!")
                            .setContentText("A fellow witch or wizard just added a 5 star restaurant.")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            // Set the intent that will fire when the user taps the notification
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(4, mBuilder.build());
                }

                Restaurant newRestaurant = new Restaurant(newName, newPhone, newWeb, selectedCat, newRating);
                appState.addRestaurant(newRestaurant);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
