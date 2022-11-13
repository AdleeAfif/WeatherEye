package com.example.WeatherEye;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class SendFeedback extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Variables Declarations
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);

        //Variable Hooks
        drawerLayout = findViewById(R.id.drawer_Feedback);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.feedback_Toolbar);

        //Toolbar
        setSupportActionBar(toolbar);

        //Navigation Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_sendfeedback);
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
                redirectActivities(this, WeatherHistory.class);
                break;
            case R.id.nav_sendfeedback:
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sendEmail(View view){

        String uriEmail = "mailto:adlee.afiff@gmail.com" + "?subject=" + Uri.encode("WeatherEye Feedback") + "&body=" + Uri.encode("Please Properly State Your Feedback About The WeatherEye App");
        Uri uri = Uri.parse(uriEmail);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(uri);

        startActivity(Intent.createChooser(intent, "Send Feedback"));
    }
}