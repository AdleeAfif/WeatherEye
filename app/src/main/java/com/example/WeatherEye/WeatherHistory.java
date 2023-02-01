package com.example.WeatherEye;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherHistory extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "WeatherHistory";

    //Variables Declarations
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    //ListView historyView;
    RecyclerView historyView;
    ArrayList<HistoryList> historyLists;
    HistoryListAdapter historyAdapter;
    Button filterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_history);

        //Variable Hooks
        drawerLayout = findViewById(R.id.drawer_History);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.history_Toolbar);
        historyView = findViewById(R.id.historyList);
        filterButton = findViewById(R.id.filterButton);

        historyLists = new ArrayList<>();

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
        String[] historyDate = getResources().getStringArray(R.array.days);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.dropdown_item, historyDate);
        dropdown.setAdapter(adapter);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {

                //Get filter input
                String filterChoice = dropdown.getText().toString();

                //Get Today's Date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
                TimeZone tz = TimeZone.getTimeZone("SGT");
                sdf.setTimeZone(tz);
                java.util.Date date= new java.util.Date();
                String strDate = sdf.format(date).substring(0,10);

                //Get previous days date
                LocalDate currentDate = LocalDate.parse(strDate);
                String returnvalue;

                //Retrieve the data from API based on the filter
                switch (filterChoice){
                    case "Today":
                        returnvalue = currentDate.minusDays(0).toString();
                        fetchPastAPIData(returnvalue);
                        Toast.makeText(WeatherHistory.this, "Showing Today's Data", Toast.LENGTH_SHORT).show();
                        break;
                    case "Yesterday":
                        returnvalue = currentDate.minusDays(1).toString();
                        fetchPastAPIData(returnvalue);
                        Toast.makeText(WeatherHistory.this, "Showing Yesterday's Data", Toast.LENGTH_SHORT).show();
                        break;
                    case "2 Days Ago":
                        returnvalue = currentDate.minusDays(2).toString();
                        fetchPastAPIData(returnvalue);
                        Toast.makeText(WeatherHistory.this, "Showing 2 Days Ago's Data", Toast.LENGTH_SHORT).show();
                        break;
                    case "3 Days Ago":
                        returnvalue = currentDate.minusDays(3).toString();
                        fetchPastAPIData(returnvalue);
                        Toast.makeText(WeatherHistory.this, "Showing 3 Days Ago's Data", Toast.LENGTH_SHORT).show();
                        break;
                    case "4 Days Ago":
                        returnvalue = currentDate.minusDays(4).toString();
                        fetchPastAPIData(returnvalue);
                        Toast.makeText(WeatherHistory.this, "Showing 4 Days Ago's Data", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        fetchAllAPIData();
                        Toast.makeText(WeatherHistory.this, "Showing All Data", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        fetchAllAPIData();
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

    //Catch data from Thingspeak API
    public void fetchAllAPIData(){
        historyLists.clear();
        Method method = RetrofitClient.getRetrofitInstance().create(Method.class);
        Call<Model> call = method.getAllData();

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                Log.e(TAG, "onResponse: code : " + response.code());
                ArrayList<Model.feeds> feeds = response.body().getFeeds();

                //Call the data from the Model
                for(Model.feeds feeds1: feeds){
                    Log.e(TAG, "onResponse: temp: " + feeds1.getField1());
                    historyLists.add(new HistoryList(feeds1.getEntry_id(), feeds1.getCreated_at(), feeds1.getCreated_at(), feeds1.getField1(), feeds1.getField2(), feeds1.getField4()));

                }

                //Sort the data from latest to old
                Collections.sort(historyLists, new Comparator<HistoryList>() {
                    @Override
                    public int compare(HistoryList historyList, HistoryList t1) {
                        return historyList.getID().compareToIgnoreCase(t1.getID());
                    }
                });

                Collections.reverse(historyLists);

                //Setup the list
                LinearLayoutManager manager = new LinearLayoutManager(WeatherHistory.this);
                historyView.setNestedScrollingEnabled(false);
                historyView.setLayoutManager(manager);
                historyView.setHasFixedSize(true);
                historyAdapter = new HistoryListAdapter(historyLists);
                historyView.setAdapter(historyAdapter);
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    //Recatch data based on the filter input
    public void fetchPastAPIData(String passDate){
        historyLists.clear();
        Method method = RetrofitClient.getRetrofitInstance().create(Method.class);
        Call<Model> call = method.getAllData();

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                Log.e(TAG, "onResponse: code : " + response.code());
                ArrayList<Model.feeds> feeds = response.body().getFeeds();

                //Call the data from the Model
                for(Model.feeds feeds1: feeds){
                    Log.e(TAG, "onResponse: temp: " + feeds1.getField1());

                    if(feeds1.getCreated_at().substring(0, 10).equals(passDate))
                        historyLists.add(new HistoryList(feeds1.getEntry_id(), feeds1.getCreated_at(), feeds1.getCreated_at(), feeds1.getField1(), feeds1.getField2(), feeds1.getField4()));

                }

                //Sort the data from latest to old
                Collections.sort(historyLists, new Comparator<HistoryList>() {
                    @Override
                    public int compare(HistoryList historyList, HistoryList t1) {
                        return historyList.getID().compareToIgnoreCase(t1.getID());
                    }
                });

                Collections.reverse(historyLists);

                //Setup the list
                LinearLayoutManager manager = new LinearLayoutManager(WeatherHistory.this);
                historyView.setNestedScrollingEnabled(false);
                historyView.setLayoutManager(manager);
                historyView.setHasFixedSize(true);
                historyAdapter = new HistoryListAdapter(historyLists);
                historyView.setAdapter(historyAdapter);
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}