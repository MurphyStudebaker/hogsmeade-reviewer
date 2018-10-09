package com.example.murphy.restaurantrec;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {
    HogsmeadeApplication appState = (HogsmeadeApplication) getApplication();

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        String selected = parent.getItemAtPosition(pos).toString();
        appState.getRestaurantDB().get(appState.getActiveIndex()).setCategory(selected);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(),"Please select a category", Toast.LENGTH_SHORT).show();
    }
}