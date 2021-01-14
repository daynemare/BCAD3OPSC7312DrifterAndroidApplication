package com.daynemare.drifterapp.helpers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.daynemare.drifterapp.models.LogRecord;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;


public class FireStoreDatabaseHelper {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //User Preferences Collection
    private static final String TAG = "Error_Log";
    private static final String KEY_USER_FULLNAME = "User_Fullname";
    private static final String KEY_USER_ID = "User_Id";
    private static final String KEY_USER_EMAIL = "User_Email";
    private static final String KEY_MEASUREMENT_SYSTEM = "Measurement_System";
    private static final String KEY_MAP_STYLE = "Map_Style";

    private static final String KEY_TRANSPORT_MODE = "Transport_Mode";

    private CollectionReference tripLogColRef;
    private String tag;

    public void createAddUserTripLog(String userUID, String dateTime, String transMode, String startLoc, String destLoc, String lat, String lng, Activity act){

        final Activity activ = act;

        String tripLogCollID = "TRIPLOG" + userUID;

        tripLogColRef = db.collection(tripLogCollID);

        LogRecord logRec = new LogRecord(dateTime,transMode,startLoc,destLoc,lat,lng);

        tripLogColRef.add(logRec).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activ , "Data Upload Failed", Toast.LENGTH_SHORT).show();
                Log.d(TAG,e.toString());
            }
        });

    }

    public void createUserPreferenceDocument(String userUID, String userEmail, String measSystem, String uFullName, String uMapStyle, Activity act){


        final Activity activ = act;

        Map<String,Object> upload = new HashMap<>();

        upload.put(KEY_USER_ID,userUID);
        upload.put(KEY_USER_EMAIL,userEmail);
        upload.put(KEY_USER_FULLNAME,uFullName);
        upload.put(KEY_MEASUREMENT_SYSTEM,measSystem);
        upload.put(KEY_MAP_STYLE,uMapStyle);
        upload.put(KEY_TRANSPORT_MODE,"Driving");

        db.collection("User Preferences").document(userUID).set(upload)
                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(tag, "Upload Successful");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(activ , "Data Upload Failed", Toast.LENGTH_SHORT).show();
                Log.d(TAG,e.toString());
            }
        });


    }

    public void updateProfileSettings(String userUID,String fullName, String measSystem,String transportMode,Activity act){

        final Activity activ = act;

        DocumentReference docRef = db.collection("User Preferences").document(userUID);

        Map<String,Object> updateProfile = new HashMap<>();
        updateProfile.put(KEY_USER_FULLNAME,fullName );
        updateProfile.put(KEY_MEASUREMENT_SYSTEM,measSystem);
        updateProfile.put(KEY_TRANSPORT_MODE,transportMode);

        docRef.update(updateProfile).addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                Timber.d("User Preferences Updated Successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(activ , "Profile Settings Update Failed, Please check your mobile connection", Toast.LENGTH_SHORT).show();
                Log.d(TAG,e.toString());
            }
        });

    }

    public void updateMapStyle(String userUID,String styleUrl,Activity act){

        final Activity activ = act;

        DocumentReference docRef = db.collection("User Preferences").document(userUID);

        Map<String,Object> updateMapStyle = new HashMap<>();
        updateMapStyle.put(KEY_MAP_STYLE, styleUrl);

        docRef.update(updateMapStyle).addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                Timber.d( "Map Style Updated Successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(activ , "Map Style Update Failed, Please check your mobile connection", Toast.LENGTH_SHORT).show();
                Log.d(TAG,e.toString());
            }
        });

    }


}
