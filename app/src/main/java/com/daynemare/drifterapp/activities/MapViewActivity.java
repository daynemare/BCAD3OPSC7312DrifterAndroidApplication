package com.daynemare.drifterapp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.daynemare.drifterapp.models.CustomItem;
import com.daynemare.drifterapp.helpers.FireStoreDatabaseHelper;
import com.daynemare.drifterapp.models.LogRecord;
import com.daynemare.drifterapp.helpers.NavigationDrawerHelper;
import com.daynemare.drifterapp.R;
import com.daynemare.drifterapp.adapters.TransportModeSpinnerCustomAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.map.NavigationMapboxMap;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;



public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener, AdapterView.OnItemSelectedListener {

    //**Declarations of layout components**
    //creating an object of the NavigationDrawerHelper Class which generates a navigation drawer in the activity where it is called
    private NavigationDrawerHelper navDrawer = new NavigationDrawerHelper();
    ImageView btOpenDrawer;
    Spinner customSpinner;
    Button start;
    FloatingActionButton refresh;
    TextView destAddress;
    TextView tripStats;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private CarmenFeature home;
    private CarmenFeature work;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private String symbolIconId = "symbolIconId";
    private NavigationMapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private String mapStyle = "mapbox://styles/mapbox/streets-v9";
    private String units = "Imperial";
    private String transportMethod = DirectionsCriteria.PROFILE_DRIVING;
    private ArrayList<CustomItem>customList;
    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
    private Point destinationPoint;
    private Point originPoint;
    private boolean isLoaded = false;

    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;

    // variables needed to initialize navigation
    private Button button;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FireStoreDatabaseHelper dbs = new FireStoreDatabaseHelper();
    FirebaseAuth mAuth;

    private static final String TAG = "Error_Message";
    private static final String ACCESS_TOKEN = "pk.eyJ1IjoiZGF5bmU5NSIsImEiOiJjazZnOWhpYmUweWVsM2xtdmZvemxxZ3RmIn0.CCVZlLeLo_gsyIMA5iX08w";
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private LogRecord logRec = new LogRecord();
    private long tripDuration;
    private double tripDistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, ACCESS_TOKEN );

        setContentView(R.layout.activity_map_view);

        mAuth = FirebaseAuth.getInstance();
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        start = findViewById(R.id.startButton);
        customSpinner= findViewById(R.id.customIconSpinner);
        btOpenDrawer = findViewById(R.id.ivNavDrawer);
        destAddress = findViewById(R.id.tvDestination);
        tripStats = findViewById(R.id.tvStats);
        refresh = findViewById(R.id.btRefreshMap);
        generateSpinner();
        generateNavDrawer();
        refreshMap();

    }

    private void refreshMap(){

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navigationMapRoute.removeRoute();
                mapboxMap.removeAnnotations();
                button.setEnabled(false);
                button.setBackgroundResource(R.drawable.custom_button_disabled);
                button.setTextColor(getResources().getColor(R.color.mapboxGrayLight));
                destAddress.setText("Tap an area on the map or the search icon below to set your destination !");
                tripStats.setText("");

            }
        });

    }

    private void generateSpinner(){
        customList = getCustomList();
        TransportModeSpinnerCustomAdapter transportModeAdapter = new TransportModeSpinnerCustomAdapter(this,customList);

        if (customSpinner!=null) {
            customSpinner.setAdapter(transportModeAdapter);
            customSpinner.setOnItemSelectedListener(this);
        }
    }

    private void generateNavDrawer(){
        //creating a navigation drawer for application activity navigation
        navDrawer.createNavDrawer(this,1);

        btOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navDrawer.openNavDrawer();
            }
        });

    }

    private void initSearchFab() {

        Point prox = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());

        findViewById(R.id.btlocationSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(ACCESS_TOKEN)
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .country(Locale.getDefault())
                                .proximity(prox)
                                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS,
                                        GeocodingCriteria.TYPE_POI,
                                        GeocodingCriteria.TYPE_PLACE)
                                .limit(10)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(MapViewActivity.this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();

                // Move map camera to the selected location
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);


                    onMapClick(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),((Point) selectedCarmenFeature.geometry()).longitude()));

                }
            }
        }


    private ArrayList<CustomItem> getCustomList() {

        customList = new ArrayList<>();
        customList.add(new CustomItem("Driving",R.drawable.ic_directions_car_black_24dp));
        customList.add(new CustomItem("Cycling",R.drawable.ic_directions_bike_black_24dp));
        customList.add(new CustomItem("Walking",R.drawable.ic_directions_walk_black_24dp));

        return  customList;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;


        db.collection("User Preferences").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {

                    String fsMapStyle;
                    String fsTransportMode;
                    DocumentSnapshot dsUserSettings = task.getResult();

                    fsMapStyle = dsUserSettings.getString("Map_Style");

                    if (fsMapStyle.equals("Light")) {
                        mapStyle = getString(R.string.map_uri_light);
                    } else if (fsMapStyle.equals("Outdoors")) {
                        mapStyle = getString(R.string.map_uri_outdoors);
                    } else if (fsMapStyle.equals("Dark")) {
                        mapStyle = getString(R.string.map_uri_dark);
                    } else if (fsMapStyle.equals("Satellite Streets")) {

                        mapStyle = getString(R.string.map_uri_satellite_streets);
                    } else {

                        mapStyle = getString(R.string.map_uri_streets);
                    }

                    fsTransportMode = dsUserSettings.getString("Transport_Mode");
                    if(fsTransportMode.equals("Cycling")){

                        customSpinner.setSelection(1);
                    }
                    else if(fsTransportMode.equals("Walking"))
                    {
                        customSpinner.setSelection(2);
                    }
                    else{
                        customSpinner.setSelection(0);
                    }



                    mapboxMap.setStyle(new Style.Builder().fromUri(mapStyle), new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            enableLocationComponent(style);
                            addDestinationIconSymbolLayer(style);


                            try {
                                initSearchFab();
                            } catch(Exception ex) {

                                locationComponent = null;
                                Toast.makeText(MapViewActivity.this, "Your Location setting is off on your device please turn it on and then start the app", Toast.LENGTH_LONG).show();

                                finish();

                            }
                            //addUserLocations();

                            mapboxMap.addOnMapClickListener(MapViewActivity.this);

                            MapViewActivity.this.mapboxMap = mapboxMap;
                            button = findViewById(R.id.startButton);

                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    boolean simulateRoute = false;

                                    NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                            .directionsRoute(currentRoute)
                                            .shouldSimulateRoute(simulateRoute)
                                            .build();
                                    // Call this method with Context from within an Activity
                                    NavigationLauncher.startNavigation(MapViewActivity.this, options);


                                    calendar = Calendar.getInstance();
                                    dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss" ,Locale.UK);
                                    logRec.setDateTime(dateFormat.format(calendar.getTime()));

                                    dbs.createAddUserTripLog(mAuth.getUid(),logRec.getDateTime(),logRec.getTransportMethod(),logRec.getStartingLocation(), logRec.getDestinationLocation(),logRec.getDestLatitude(),logRec.getDestLongitude(),MapViewActivity.this);
                                    navDrawer.finish();
                                    generateNavDrawer();
                                }
                            });
                        }
                    });

                }
                else{
                    Log.d(TAG, "Error: " + task.getException().getMessage());
                }
            }
        });

    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        destinationPoint = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        originPoint = Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
                locationComponent.getLastKnownLocation().getLatitude());

        logRec.setDestLongitude(String.valueOf(point.getLongitude()));
        logRec.setDestLatitude(String.valueOf(point.getLatitude()));

        mapboxMap.removeAnnotations();

        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(point.getLatitude(), point.getLongitude()))
                .title("Your Destination"));
        getRoute(originPoint, destinationPoint);

        reverseGeocodeDest(destinationPoint);
        reverseGeocodeStart(originPoint);

        return true;
    }

    private void getRoute(Point origin, Point destination) {

        db.collection("User Preferences").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {

                    DocumentSnapshot dsUnitMeas = task.getResult();

                    String dsUnits;
                    dsUnits = dsUnitMeas.getString("Measurement_System");

                    if(dsUnits.equals("Imperial")){

                        units = DirectionsCriteria.IMPERIAL;
                    }
                    else{
                        units = DirectionsCriteria.METRIC;
                    }

                    NavigationRoute.builder(MapViewActivity.this)
                            .accessToken(ACCESS_TOKEN)
                            .origin(origin)
                            .profile(transportMethod)
                            .destination(destination)
                            .voiceUnits(units)
                            .build()
                            .getRoute(new Callback<DirectionsResponse>() {
                                @Override
                                public void onResponse(Call<DirectionsResponse> call,  Response<DirectionsResponse> response) {
                                    // You can get the generic HTTP info about the response
                                    Log.d(TAG, "Response code: " + response.code());
                                    if (response.body() == null) {
                                        Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                                        return;
                                    } else if (response.body().routes().size() < 1) {
                                        Log.e(TAG, "No routes found");
                                        return;
                                    }

                                    currentRoute = response.body().routes().get(0);

                                    // Draw the route on the map
                                    if (navigationMapRoute != null) {
                                        navigationMapRoute.removeRoute();
                                    } else {
                                        navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                                    }

                                    //calculates the duration of the trip
                                    tripDuration = Math.round(response.body().routes().get(0).duration()/60);
                                    int timeInMilliseconds = (int)(tripDuration * 60000);
                                    @SuppressLint("DefaultLocale")
                                    String duration = String.format("%d H %02d Min",
                                            TimeUnit.MILLISECONDS.toHours(timeInMilliseconds),
                                            TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds) -
                                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMilliseconds)));

                                    // Variable used to store either Km or Miles
                                    String unitString;

                                    // Calculation for metric units
                                    if(units.equals("metric"))
                                    {
                                        tripDistance = Math.round(response.body().routes().get(0).distance()/1000.0*100.0)/100.0;
                                        unitString = " Km";
                                    }

                                    // Calculation for imperial units
                                    else
                                    {
                                        tripDistance = Math.round(response.body().routes().get(0).distance()/1609.344*100.0)/100.0;
                                        unitString = " Miles";
                                    }
                                    String statsOutput = tripDistance + unitString + "\t\t" + duration;
                                    tripStats.setText(statsOutput);

                                    navigationMapRoute.addRoute(currentRoute);
                                    button.setEnabled(true);
                                    button.setBackgroundResource(R.drawable.custom_button_enabled);
                                    button.setTextColor(getResources().getColor(R.color.mapboxWhite));
                                }

                                @Override
                                public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                                    Log.e(TAG, "Error: " + throwable.getMessage());
                                }
                            });

                }
                else{
                    Log.d(TAG, "Error: " + task.getException().getMessage());
                }
            }
        });


    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
            // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Activate the MapboxMap LocationComponent to show user location
            // Adding in LocationComponentOptions is also an optional parameter
            locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        CustomItem item = (CustomItem)adapterView.getSelectedItem();
        if (navigationMapRoute != null) {

            navigationMapRoute.removeRoute();
            mapboxMap.removeAnnotations();
            button.setEnabled(false);
            button.setBackgroundResource(R.drawable.custom_button_disabled);
            button.setTextColor(getResources().getColor(R.color.mapboxGrayLight));
           destAddress.setText("Tap an area on the map or the search icon below to set your destination !");
            tripStats.setText("");

        }


        if(item.getSpinnerItemName().equals("Driving")){

            logRec.setTransportMethod("Driving");
            transportMethod = DirectionsCriteria.PROFILE_DRIVING;

        }
        else if(item.getSpinnerItemName().equals("Cycling"))
        {
            logRec.setTransportMethod("Cycling");
            transportMethod = DirectionsCriteria.PROFILE_CYCLING;

        }
        else{

            logRec.setTransportMethod("Walking");
            transportMethod = DirectionsCriteria.PROFILE_WALKING;


        }
        if(isLoaded){
            updateTransportMethodDb(logRec.getTransportMethod());
        }


        isLoaded= true;


    }

    public void updateTransportMethodDb(String data){
        DocumentReference userPref = db.collection("User Preferences").document(mAuth.getUid());

// Set the "isCapital" field of the city 'DC'
        userPref.update("Transport_Mode", data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void reverseGeocodeDest(final Point point) {
        try {
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(getString(R.string.access_token))
                    .query(Point.fromLngLat(point.longitude(), point.latitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                    if (response.body() != null) {
                        List<CarmenFeature> results = response.body().features();
                        if (results.size() > 0) {
                            CarmenFeature feature = results.get(0);

                            // If the geocoder returns a result, we take the first in the list and show a Toast with the place name.
                            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {

                                       /* Toast.makeText(MapViewActivity.this,
                                                String.format(feature.placeName()), Toast.LENGTH_SHORT).show();*/

                                        destAddress.setText(feature.placeName());
                                        logRec.setDestinationLocation(feature.placeName());

                                }
                            });

                        } else {
                            Toast.makeText(MapViewActivity.this, "No Results", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                    Timber.e("Geocoding Failure: %s", throwable.getMessage());
                }
            });
        } catch (ServicesException servicesException) {
            Timber.e("Error geocoding: %s", servicesException.toString());
            servicesException.printStackTrace();
        }
    }

    private void reverseGeocodeStart(final Point point) {
        try {
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(getString(R.string.access_token))
                    .query(Point.fromLngLat(point.longitude(), point.latitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                    if (response.body() != null) {
                        List<CarmenFeature> results = response.body().features();
                        if (results.size() > 0) {
                            CarmenFeature feature = results.get(0);

                            // If the geocoder returns a result, we take the first in the list and show a Toast with the place name.
                            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {

                                    logRec.setStartingLocation(feature.placeName());

                                }
                            });

                        } else {
                            Toast.makeText(MapViewActivity.this, "No Results", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                    Timber.e("Geocoding Failure: %s", throwable.getMessage());
                }
            });
        } catch (ServicesException servicesException) {
            Timber.e("Error geocoding: %s", servicesException.toString());
            servicesException.printStackTrace();
        }
    }

}

