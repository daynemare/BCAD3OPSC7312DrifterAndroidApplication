package com.daynemare.drifterapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daynemare.drifterapp.R;
import com.daynemare.drifterapp.adapters.TripLogAdapter;
import com.daynemare.drifterapp.models.LogRecord;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TripLogActivity extends AppCompatActivity {

    private CollectionReference tripLogColRef;
    private static final String TAG = "Error";
    RecyclerView recView;
    private ArrayList<String> arrayDateTime = new ArrayList<>();
    private ArrayList<String> arrayTo = new ArrayList<>();
    private ArrayList<String> arrayFrom = new ArrayList<>();
    private ArrayList<String> arrayTranMode = new ArrayList<>();
    TextView tripTitle;
    ImageView leave;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_log);
        recView = findViewById(R.id.rView);
        mAuth = FirebaseAuth.getInstance();

        getUserTripLogs(mAuth.getUid());
        tripTitle = findViewById(R.id.tvTripLogTitle);
        leave = findViewById(R.id.ivReturn);

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripLogActivity.this, MapViewActivity.class);
                startActivity(intent);
            }
        });

    }


    public void getUserTripLogs(String userUID){

        String tripLogCollID = "TRIPLOG" + userUID;
        FirebaseFirestore.getInstance().collection(tripLogCollID).orderBy("dateTime", Query.Direction.ASCENDING)
        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(QueryDocumentSnapshot documentSnapshots:queryDocumentSnapshots) {

                    LogRecord logRec = documentSnapshots.toObject(LogRecord.class);

                    arrayDateTime.add(logRec.getDateTime());
                    arrayTranMode.add(logRec.getTransportMethod());
                    arrayTo.add(logRec.getDestinationLocation());
                    arrayFrom.add(logRec.getStartingLocation());

                    TripLogAdapter ma = new TripLogAdapter(TripLogActivity.this,arrayDateTime,arrayTranMode,arrayTo,arrayFrom);
                    recView.setAdapter(ma);
                    recView.setLayoutManager(new LinearLayoutManager(TripLogActivity.this));

                }
                if(arrayDateTime.size()>0){

                    tripTitle.setText(R.string.trip_log_title);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TripLogActivity.this , "Data Retrieval Failed", Toast.LENGTH_SHORT).show();
                Log.d(TAG,e.toString());
            }
        });


    }

}
