package com.daynemare.drifterapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daynemare.drifterapp.models.CustomItem;
import com.daynemare.drifterapp.helpers.FireStoreDatabaseHelper;
import com.daynemare.drifterapp.helpers.NavigationDrawerHelper;
import com.daynemare.drifterapp.R;
import com.daynemare.drifterapp.adapters.TransportModeSpinnerCustomAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class ProfileSettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "Error_Message";
    //creating an object of the NavigationDrawerHelper Class which generates a navigation drawer in the activity where it is called
    private NavigationDrawerHelper navDrawer = new NavigationDrawerHelper();


    TextView tvFullName;
    EditText etFullName;
    TextView tvMeasSystem;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Switch clearHist;

    TextView tvStyleChoice;
    Button btSaveExit;
    TextView tvCurrentMapStyle;
    Button btStyleSelect;
    FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FireStoreDatabaseHelper dbh = new FireStoreDatabaseHelper();
    private String selectedRB;
    private boolean checked = false;
    private ArrayList<CustomItem>customList;
    Spinner customSpinner;

    private String transportMethod = "Driving";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        mAuth = FirebaseAuth.getInstance();


        //creating a navigation drawer for application activity navigation
        navDrawer.createNavDrawer(this,2);
        radioGroup = findViewById(R.id.radioGroup);

        tvStyleChoice = findViewById(R.id.tvCurrentMapStyle);
        btSaveExit = findViewById(R.id.btSaveExit);

        tvFullName = findViewById(R.id.tvFullName);
        etFullName = findViewById(R.id.etFullName);
        tvMeasSystem = findViewById(R.id.tvMeasTitle);

        btStyleSelect = findViewById(R.id.btStyleSelect);
        clearHist = findViewById(R.id.swClear);

        customList = getCustomList();
        customSpinner= findViewById(R.id.customIconSpinner);
        TransportModeSpinnerCustomAdapter transportModeAdapter = new TransportModeSpinnerCustomAdapter(this,customList);

        if (customSpinner!=null) {
            customSpinner.setAdapter(transportModeAdapter);
            customSpinner.setOnItemSelectedListener(this);
        }

        getUserPreferenceData();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                  @Override
                                                  public void onCheckedChanged(RadioGroup group, int checkedId)
                                                  {
                                                      radioButton = (RadioButton) findViewById(checkedId);

                                                      selectedRB = radioButton.getText().toString();

                                                  }
                                              }
        );


        btStyleSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = etFullName.getText().toString();
                dbh.updateProfileSettings(mAuth.getUid(),fName,selectedRB, transportMethod,ProfileSettingsActivity.this);
                Intent intent = new Intent(v.getContext(), SelectStyleActivity.class);
                startActivity(intent);
                finish();

            }
        });

        clearHist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){

                    clearHist.setText("Yes");
                    checked = true;

                }
                else{
                    clearHist.setText("No");
                    checked = false;
                }

            }
        });



        btSaveExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String measSys;
                String fName = etFullName.getText().toString();

                if(fName.isEmpty()){
                    Toast.makeText(ProfileSettingsActivity.this, "The full name field cannot be left empty", Toast.LENGTH_SHORT).show();
                }
                else{

                    if(checked){
                        deleteUserTripLogs();
                    }

                    dbh.updateProfileSettings(mAuth.getUid(),fName,selectedRB, transportMethod,ProfileSettingsActivity.this);
                    Intent intent = new Intent(ProfileSettingsActivity.this, MapViewActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }

    public void deleteUserTripLogs(){

        String tripLogCollID = "TRIPLOG" + mAuth.getUid();
        FirebaseFirestore.getInstance().collection(tripLogCollID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot snapshot:snapshotList){
                    batch.delete(snapshot.getReference());
                }

                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Docs Deleted Successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Docs Failed to delete");
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Docs Failed to delete");
            }
        });


    }


    private ArrayList<CustomItem> getCustomList() {

        customList = new ArrayList<>();
        customList.add(new CustomItem("Driving",R.drawable.ic_directions_car_black_24dp));
        customList.add(new CustomItem("Cycling",R.drawable.ic_directions_bike_black_24dp));
        customList.add(new CustomItem("Walking",R.drawable.ic_directions_walk_black_24dp));

        return  customList;
    }

    public void getUserPreferenceData(){

        db.collection("User Preferences").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String measurementOpt;
                    String transportMode;
                    DocumentSnapshot dsUserPreferences = task.getResult();
                    etFullName.setText(dsUserPreferences.getString("User_Fullname"));
                    measurementOpt = dsUserPreferences.getString("Measurement_System");

                    if(measurementOpt.equals("Imperial")){
                        ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
                    }
                    else{
                        ((RadioButton)radioGroup.getChildAt(1)).setChecked(true);
                    }
                    tvStyleChoice.setText(dsUserPreferences.getString("Map_Style"));

                    transportMode = dsUserPreferences.getString("Transport_Mode");
                    if(transportMode.equals("Cycling")){

                        customSpinner.setSelection(1);
                    }
                    else if(transportMode.equals("Walking"))
                    {
                        customSpinner.setSelection(2);
                    }
                    else{
                        customSpinner.setSelection(0);
                    }

                }
                else{
                    Log.d(TAG, "Error: " + task.getException().getMessage());
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        CustomItem item = (CustomItem)adapterView.getSelectedItem();

        if(item.getSpinnerItemName().equals("Driving")){

            transportMethod = "Driving";
        }
        else if(item.getSpinnerItemName().equals("Cycling"))
        {

            transportMethod = "Cycling";
        }
        else{

            transportMethod = "Walking";
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
