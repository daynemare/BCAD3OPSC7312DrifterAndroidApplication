package com.daynemare.drifterapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.daynemare.drifterapp.helpers.FireStoreDatabaseHelper;
import com.daynemare.drifterapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.google.firebase.auth.FirebaseAuth;

public class SelectStyleActivity extends AppCompatActivity {

    private MapView mapView;
    private MapboxMap mapboxMap;
    Button saveChanges;
    private FireStoreDatabaseHelper dbh = new FireStoreDatabaseHelper();
    private FirebaseAuth mAuth;
    private String mapStyle = "mapbox://styles/mapbox/streets-v9";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Error_Message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_style);
        mAuth = FirebaseAuth.getInstance();

        Mapbox.getInstance(this, "pk.eyJ1IjoiZGF5bmU5NSIsImEiOiJjazZnOWhpYmUweWVsM2xtdmZvemxxZ3RmIn0.CCVZlLeLo_gsyIMA5iX08w");



        saveChanges = findViewById(R.id.btSaveChanges);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
        public void onMapReady(@NonNull MapboxMap mapboxMap) {
            SelectStyleActivity.this.mapboxMap = mapboxMap;

            setCurrentMapStyle();



             }
    });

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbh.updateMapStyle(mAuth.getUid(),mapStyle, SelectStyleActivity.this);
                Intent intent = new Intent(SelectStyleActivity.this, ProfileSettingsActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    public void setCurrentMapStyle(){

        db.collection("User Preferences").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String measurementOpt;
                    DocumentSnapshot dsUserPreferences = task.getResult();

                    mapStyle = dsUserPreferences.getString("Map_Style");

                    if(mapStyle.equals("Dark")){
                        mapboxMap.setStyle(Style.DARK);
                    }
                    else if(mapStyle.equals("Light")){
                        mapboxMap.setStyle(Style.LIGHT);
                    }
                    else if((mapStyle.equals("Outdoors"))){
                        mapboxMap.setStyle(Style.OUTDOORS);
                    }
                    else if((mapStyle.equals("Satellite Streets"))){
                        mapboxMap.setStyle(Style.SATELLITE_STREETS);
                    }
                    else{
                        mapboxMap.setStyle(Style.MAPBOX_STREETS);
                    }


                }
                else{
                    Log.d(TAG, "Error: " + task.getException().getMessage());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map_style, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_streets:
                mapboxMap.setStyle(Style.MAPBOX_STREETS);
                mapStyle = "Streets";
                return true;
            case R.id.menu_dark:
                mapboxMap.setStyle(Style.DARK);
                mapStyle = "Dark";
                return true;
            case R.id.menu_light:
                mapboxMap.setStyle(Style.LIGHT);
                mapStyle = "Light";
                return true;
            case R.id.menu_outdoors:
                mapboxMap.setStyle(Style.OUTDOORS);
                mapStyle = "Outdoors";
                return true;
            case R.id.menu_satellite_streets:
                mapboxMap.setStyle(Style.SATELLITE_STREETS);
                mapStyle = "Satellite Streets";
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
