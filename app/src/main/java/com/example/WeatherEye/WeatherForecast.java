package com.example.WeatherEye;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WeatherForecast extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Variables Declarations
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ExpandableHeightListView forecastView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        //Variable Hooks
        drawerLayout = findViewById(R.id.drawer_Forecast);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.forecast_Toolbar);
        forecastView = findViewById(R.id.forecastListView);

        //Toolbar
        setSupportActionBar(toolbar);

        //Navigation Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_weatherforecast);

//        //Dropdown Menu
//        AutoCompleteTextView dropdown = findViewById(R.id.autoCompleteTextView);
//        String[] forecastDate = getResources().getStringArray(R.array.forecasts);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, forecastDate);
//        dropdown.setAdapter(adapter);

        getForecasts();

    }


    public ArrayList<ForecastList> getForecasts(){

        //Retrieve Data from Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Forecast");

        String[] t = new String[120];
        String[] h = new String[120];
        String[] r = new String[120];

        ArrayList<ForecastList> todayForecasts = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todayForecasts.clear();

                int num = 0;

                for(int i = 0; i < 12; i++){
                    r[num] = snapshot.child("Next 12 Hours").child(String.valueOf(i)).getValue().toString();
                    num++;
                }

                for(int j = 0; j < num; j++){
                    ForecastList data = new ForecastList(String.valueOf(j), r[j]);
                    todayForecasts.add(data);
                }

                ForecastListAdapter forecastListAdapter = new ForecastListAdapter(WeatherForecast.this, R.layout.forecastitem_layout, todayForecasts);
                forecastView.setAdapter(forecastListAdapter);
                forecastView.setExpanded(true);
                forecastView.setSelector(R.color.Secondary_Background);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return todayForecasts;
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
                break;
            case R.id.nav_weatherhistory:
                redirectActivities(this, WeatherHistory.class);
                break;
            case R.id.nav_sendfeedback:
                redirectActivities(this, SendFeedback.class);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}