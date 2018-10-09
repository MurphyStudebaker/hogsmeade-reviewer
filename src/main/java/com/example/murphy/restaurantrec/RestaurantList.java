package com.example.murphy.restaurantrec;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

import static android.support.v4.app.NotificationManagerCompat.from;
import static android.view.Window.FEATURE_ACTION_BAR;

/** Murphy Studebaker
 *  Hogsmeade Restaurant Review App
 *
 *  SOURCES:
 *  - https://developer.android.com/training/appbar/setting-up
 *  - https://developer.android.com/guide/topics/ui/menus
 *  - https://developer.android.com/guide/topics/ui/layout/cardview
 *  - Recylcer View code from the Class Database project
 *  - https://android.okhelp.cz/get-resource-id-by-resources-string-name-android-example/
 *  - https://developer.android.com/training/basics/network-ops/xml
 *  - https://stackoverflow.com/questions/15912825/how-to-read-file-from-res-raw-by-name/15912883
 *  - Notification Tut: https://www.youtube.com/watch?v=WozSRUnYoNM&list=PLGLfVvz_LVvSPjWpLPFEfOCbezi6vATIh&index=21
 *  - Broadcast Tut: https://www.youtube.com/watch?v=l8XBY1sqz70&list=PLGLfVvz_LVvSPjWpLPFEfOCbezi6vATIh&index=20
 *  - XML Parsing Tut: https://www.youtube.com/watch?v=HVvYRcxSq-Y&list=PLGLfVvz_LVvSPjWpLPFEfOCbezi6vATIh&index=17
 *  - https://stackoverflow.com/questions/12070744/add-back-button-to-action-bar
 */

// Main activity that holds a RecyclerView of restaurants and has an options toolbar
public class RestaurantList extends Activity {
    private HogsmeadeApplication appState;
    private ArrayList<Restaurant> restaurants;
    private RestaurantListAdapter adapter;
    private TextView welcome;

    ActionBar toolbar;
    RecyclerView list;
    NotificationManagerCompat manager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //Specifices actions for menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.addRest:
                //go to add restaurant activity
                Intent nextScreen = new Intent(getApplicationContext(), AddRestaurant.class);
                startActivity(nextScreen);
                welcome.setVisibility(View.GONE);
                return true;
            case R.id.clearData:
                //clear the data and reset appState
                appState.clearData();
                restaurants = appState.getRestaurantDB();
                adapter.notifyDataSetChanged();
                return true;
            case R.id.loadData:
                welcome.setVisibility(View.GONE);
                new XMLParserTask().execute(); //load in another thread
                restaurants = appState.getRestaurantDB();
                adapter.notifyDataSetChanged();
                return true;
            case R.id.pref:
                //open preferences dialog and update appState
                CharSequence[] prefOptions = new CharSequence[3];
                prefOptions[0] = "Display Phone";
                prefOptions[1] = "Display Web";
                prefOptions[2] = "Display Category";
                boolean[] selectedOptions = new boolean[3];
                selectedOptions = appState.getPreferences();
                AlertDialog ad = new AlertDialog.Builder(RestaurantList.this).setTitle("Preferences")
                        .setMultiChoiceItems(prefOptions, selectedOptions, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                boolean[] newPrefs = new boolean[3];
                                newPrefs = appState.getPreferences();
                                newPrefs[i] = b;
                                appState.setPreferences(newPrefs[0],newPrefs[1],newPrefs[2]);
                            }
                        }).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //does nothing
                            }
                        }).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window myWindow = getWindow();
        myWindow.requestFeature(FEATURE_ACTION_BAR);
        toolbar = getActionBar();
        toolbar.setTitle("Hogsmeade");
        //toolbar.setBackgroundDrawable(new ColorDrawable());
        setContentView(R.layout.rest_list);

        appState = (HogsmeadeApplication) getApplication();
        appState.setPreferences(true, true, true);
        restaurants = new ArrayList<Restaurant>();

        //Views
        list = (RecyclerView) findViewById(R.id.restaurantList);
        welcome = (TextView) findViewById(R.id.welcomeText);

        //configure recycler view
        list.setLayoutManager(new LinearLayoutManager(this));
        restaurants = appState.getRestaurantDB();
        adapter = new RestaurantListAdapter(restaurants);
        list.setAdapter(adapter);

        //listen for broadcast that a 5 star restaurant was added
        IntentFilter filter = new IntentFilter();
        filter.addAction(AddRestaurant.FIVE_STAR_ADDED);
        registerReceiver(fiveStarRecevier, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        restaurants = appState.getRestaurantDB();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
       // unregisterReceiver(fiveStarRecevier);
    }

    public ArrayList<Restaurant> filterByRating(int minRating) {
        ArrayList<Restaurant> db = new ArrayList<>();
        ArrayList<Restaurant> filteredDB = new ArrayList<>();
        db = restaurants;
        for (int i = 0; i < db.size(); ++i) {
            if (db.get(i).getRating() >= minRating)
                filteredDB.add(db.get(i));
        }
        return filteredDB;
    }

    /* List Adapter specific to updating Restaurant CardViews */
    public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {
        private ArrayList<Restaurant> dataset = new ArrayList<>();

        public RestaurantListAdapter(ArrayList<Restaurant> myDataset) {
                dataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public RestaurantViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
            return new RestaurantViewHolder(parent);
        }

        // Replace the contents of a view (invoked by the layout manager)
        public void onBindViewHolder(RestaurantViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.bind(this.dataset.get(position));
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    appState.setActiveIndex(position); //set active class as the one clicked
                    //go to "Class Info" activity
                    Intent nextScreen = new Intent(getApplicationContext(), RestaurantInfo.class);
                    startActivity(nextScreen);
                }
            });
            holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    appState.setActiveIndex(position); //set active class as the one clicked
                    AlertDialog ad = new AlertDialog.Builder(RestaurantList.this).setTitle("Delete item?")
                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //do nothing
                                }
                            }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    restaurants.remove(appState.getActiveIndex());
                                    appState.setRestaurantDB(restaurants);
                                    adapter.notifyDataSetChanged();
                                }
                            }).show();
                    return false;
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return dataset.size();
        }
    }

    /* Sets the structure for the View of the Course dataset, code is from Android documentation and
     * the RecyclerView tutorials on YouTube */
    class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView restName;
        ImageView restImg;
        RatingBar restRating;
        LinearLayout parentLayout;
        LinearLayout cardChild1;
        LinearLayout cardChild2;
        CardView cardLayout;

        public RestaurantViewHolder(ViewGroup container){
            super(LayoutInflater.from(RestaurantList.this).inflate(R.layout.card, container, false));
            parentLayout = itemView.findViewById(R.id.ll_parent);
            cardLayout = itemView.findViewById(R.id.rest_card);
            cardChild1 = itemView.findViewById(R.id.ll_vert);
            cardChild2 = itemView.findViewById(R.id.ll_hor);
            restName = (TextView) itemView.findViewById(R.id.name);
            restImg = (ImageView) itemView.findViewById(R.id.image);
            restRating = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }
        public void bind(Restaurant rest) {
            restName.setText(rest.getName());
            int resID = getResources().getIdentifier(rest.getImageID(),
                    "drawable", getPackageName());
            restImg.setImageResource(resID);
            restRating.setRating(rest.getRating());
        }
    }

    /* Loads in Restaurants and inserts them into the database using another Thread */
    class XMLParserTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String xml = "";
            InputStream inputFile = null;
            HogsmeadeApplication appState = (HogsmeadeApplication) getApplication();

             try {
                 //read in the xml file to string
                 inputFile = getResources().openRawResource(getResources().getIdentifier
                         ("input_data", "raw", getPackageName()));
                 BufferedReader br = new BufferedReader(new InputStreamReader(inputFile));
                 StringBuilder sb = new StringBuilder();
                 String line = null;
                 while((line = br.readLine()) != null) {
                     sb.append(line);
                 }
                 xml = sb.toString();

                 //set up the xml reader
                 XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                 XmlPullParser xpp = factory.newPullParser();
                 xpp.setInput(new StringReader(xml));

                 //set up variables to be read in
                 String name = "";
                 String img = "";
                 String phone = "";
                 String web = "";
                 String cat = "";
                 float rating = 0;

                 //move through xml string and run on each restaurant item until end of file
                 int eventType = xpp.getEventType();
                 while (eventType != XmlPullParser.END_DOCUMENT) {
                    if ((eventType == XmlPullParser.START_TAG) && (xpp.getName().equals("restaurant"))) {
                        name = xpp.getAttributeValue(null, "name");
                        img = xpp.getAttributeValue(null, "img");
                        phone = xpp.getAttributeValue(null, "phone");
                        web = xpp.getAttributeValue(null, "web");
                        cat = xpp.getAttributeValue(null, "category");
                        try {
                            rating = Float.parseFloat(xpp.getAttributeValue(null,"rating"));
                        } catch (NumberFormatException e) {
                            rating = 0;
                        }
                        appState.addRestaurant(new Restaurant(name,phone,web,cat,rating,img));
                    }
                    eventType = xpp.next(); //move to next tag
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             } catch (XmlPullParserException e) {
                 e.printStackTrace();
             }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private BroadcastReceiver fiveStarRecevier = new BroadcastReceiver() {
        int index = 0;
        @Override
        public void onReceive(Context context, Intent intent) {
            //send out notification
            Toast.makeText(getApplicationContext(), "Accio Deliciousness!", Toast.LENGTH_SHORT).show();
            Intent incoming = getIntent();
            int index = incoming.getIntExtra("INDEX",0);
            dispatchNotification(index);
        }
    };

    public void dispatchNotification(int addedIndex) {
        Intent addedRestaurant = new Intent(getApplicationContext(), RestaurantInfo.class);
        addedRestaurant.putExtra("INDEX", addedIndex);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), NotificationChannel.DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.drawable.beer_icon)
                .setContentTitle("Accio deliciousness!")
                .setContentText("A fellow witch or wizard just added a 5 star restaurant.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        // Set the intent that will fire when the user taps the notification
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(RestaurantList.class);
        stackBuilder.addNextIntent(addedRestaurant);

        PendingIntent pi = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);

        manager = (NotificationManagerCompat) manager.from(getApplicationContext());
        manager.notify(38,mBuilder.build());
    }
}
