package com.example.WeatherEye;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class WeatherMonitor extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    //Variables Declarations
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView tempText, humidText, rainText, lastUpdated, testView;
    GoogleMap gMap;

    boolean isPermissionGranted;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_monitor);
        checkPermission();

        //Variable Hooks
        drawerLayout = findViewById(R.id.drawer_Monitor);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.monitor_Toolbar);
        tempText = findViewById(R.id.temp);
        humidText = findViewById(R.id.humid);
        rainText = findViewById(R.id.rain);
        lastUpdated = findViewById(R.id.lastUpdate);

        //Toolbar
        setSupportActionBar(toolbar);

        //Navigation Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_weathermonitor);

        //Google Map Initilization
        if(checkGooglePlay()){
            SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.gadgetMap, supportMapFragment).commit();
            supportMapFragment.getMapAsync(this);

        }
        else{
            Toast.makeText(this, "Google Services Not Available", Toast.LENGTH_SHORT).show();
        }

        //Retrieve Data from Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("WeatherEye");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String tempDB = dataSnapshot.child("Temperature").getValue().toString();
                    String humidDB = dataSnapshot.child("Humidity").getValue().toString();
                    String rainDB = dataSnapshot.child("Raindrop").getValue().toString();
                    String timeDB = dataSnapshot.child("Time").getValue().toString();
                    String dateDB = dataSnapshot.child("Date").getValue().toString();

                    tempText.setText(tempDB + "°C");
                    humidText.setText(humidDB + "%");
                    rainText.setText(rainDB);

                    //Display Last Data is Updated
                    lastUpdated.setText("Last Updated: " + timeDB + ", " + dateDB);

                }
                else{
                    tempText.setText("Not Available");
                    humidText.setText("Not Available");
                    rainText.setText("Not Available");
                    lastUpdated.setText("Last Updated: Not Available");
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void checkPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                isPermissionGranted = true;
                Toast.makeText(WeatherMonitor.this, "Permission Granted", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();

            }
        }).check();
    }

    private boolean checkGooglePlay() {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int result = googleApi.isGooglePlayServicesAvailable(this);
        if(result == ConnectionResult.SUCCESS){

            return true;
        }
        else if(googleApi.isUserResolvableError(result)){

            Dialog dialog = googleApi.getErrorDialog(this, result, 201, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Toast.makeText(WeatherMonitor.this, "Dialog Canceled", Toast.LENGTH_SHORT).show();

                }
            });
            dialog.show();
        }

        return false;
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
                break;
            case R.id.nav_weatherforecast:
                redirectActivities(this, WeatherForecast.class);
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

    //Google Map functions
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        //Set Gadget Location
        gMap = googleMap;
        LatLng latLng = new LatLng(3.5469295, 103.4276627);

        //Initiliaze Marker
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("Gadget's Location");
        markerOptions.position(latLng);
        gMap.addMarker(markerOptions);

        //Set Camera Default Position
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        gMap.animateCamera(cameraUpdate);
    }
}