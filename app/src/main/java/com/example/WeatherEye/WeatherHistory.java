package com.example.WeatherEye;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherHistory extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Variables Declarations
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ListView historyView;
    ArrayList<HistoryList> historyLists;
    HistoryListAdapter historyAdapter;
    HistoryList hs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_history);

        //Variable Hooks
        drawerLayout = findViewById(R.id.drawer_History);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.history_Toolbar);
        historyView = findViewById(R.id.historyList);

        //Toolbar
        setSupportActionBar(toolbar);

        //Navigation Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_weatherhistory);

        //Dropdown Menu
        AutoCompleteTextView dropdown = findViewById(R.id.autoCompleteTextView);
        String[] historyDate = getResources().getStringArray(R.array.locations);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, historyDate);
        dropdown.setAdapter(adapter);

        /*//Temporary List Data
        HistoryList data1 = new HistoryList("23:00:34", "5-10-2022", "32.2", "48.0", "Yes");
        HistoryList data2 = new HistoryList("23:00:04", "5-10-2022", "32.2", "48.0", "Yes");
        HistoryList data3 = new HistoryList("22:59:36", "5-10-2022", "32.2", "48.0", "Yes");
        HistoryList data4 = new HistoryList("22:59:05", "5-10-2022", "32.2", "48.0", "Yes");
        HistoryList data5 = new HistoryList("22:58:44", "5-10-2022", "32.2", "48.0", "Yes");
        HistoryList data6 = new HistoryList("22:58:10", "5-10-2022", "32.2", "48.0", "Yes");
        HistoryList data7 = new HistoryList("22:57:41", "5-10-2022", "32.2", "48.0", "Yes");
        HistoryList data8 = new HistoryList("22:57:11", "5-10-2022", "32.2", "48.0", "Yes");
        HistoryList data9 = new HistoryList("22:56:53", "5-10-2022", "32.2", "48.0", "Yes");
        HistoryList data10 = new HistoryList("22:56:15", "5-10-2022", "32.2", "48.0", "Yes");

        //Add Data objects to an ArrayList
        ArrayList<HistoryList> historyLists = new ArrayList<>();
        historyLists.add(data1);
        historyLists.add(data2);
        historyLists.add(data3);
        historyLists.add(data4);
        historyLists.add(data5);
        historyLists.add(data6);
        historyLists.add(data7);
        historyLists.add(data8);
        historyLists.add(data9);
        historyLists.add(data10);

        HistoryListAdapter historyAdapter = new HistoryListAdapter(this, R.layout.historyitem_layout, historyLists);
        historyView.setAdapter(historyAdapter);
        historyView.setSelector(R.color.Secondary_Background);*/


        //Set Firebase Reference
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("WeatherEye/History");
        historyLists = new ArrayList<>();
        historyAdapter = new HistoryListAdapter(this, R.layout.historyitem_layout, historyLists);

        //Fetch Data from Firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:snapshot.getChildren()){

                    //Try add date and time variable to the arduino
                    //Testing GitHub
                    String dateDB = snapshot.child("History").child("5-11-2022").child("14:53:59").child("Date").getValue().toString();
                    String timeDB = snapshot.child("History").child("5-11-2022").child("14:53:59").child("Time").getValue().toString();
                    String tempDB = snapshot.child("History").child("5-11-2022").child("14:53:59").child("Temperature").getValue().toString();
                    String humidDB = snapshot.child("History").child("5-11-2022").child("14:53:59").child("Humidity").getValue().toString();
                    String rainDB = snapshot.child("History").child("5-11-2022").child("14:53:59").child("Raindrop").getValue().toString();

                    hs = new HistoryList(timeDB, dateDB, tempDB, humidDB, rainDB);
                    historyLists.add(hs);

                }
                historyView.setAdapter(historyAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void onBackPressed(){

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    public static void redirectActivities(Activity activity, Class aClass){
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_homepage:
                redirectActivities(this, Homepage.class);
                break;
            case R.id.nav_weathermonitor:
                redirectActivities(this, WeatherMonitor.class);
                break;
            case R.id.nav_weatherforecast:
                redirectActivities(this, WeatherForecast.class);
                break;
            case R.id.nav_weatherhistory:
                break;
            case R.id.nav_sendfeedback:
                redirectActivities(this, SendFeedback.class);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}