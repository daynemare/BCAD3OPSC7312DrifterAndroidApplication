package com.daynemare.drifterapp.helpers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.daynemare.drifterapp.R;
import com.daynemare.drifterapp.activities.LoginActivity;
import com.daynemare.drifterapp.activities.MapViewActivity;
import com.daynemare.drifterapp.activities.ProfileSettingsActivity;
import com.daynemare.drifterapp.activities.TripLogActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;


public class NavigationDrawerHelper extends AppCompatActivity {

    private static final String TAG = "Error";
    private Drawer result = null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tripLogColRef;
    ArrayList<String> count = new ArrayList<>();
    private String currentUser;

    //Responsible for creating a nav drawer with activity items that can be selected for the purpose of app navigation
    public void createNavDrawer(Activity act,int item){

        mAuth = FirebaseAuth.getInstance();
        final Activity activity = act;

        getUserFullName();
        DocumentReference docRef = db.collection("User Preferences").document(mAuth.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {

                        currentUser = document.getString("User_Fullname");
                        // Create the AccountHeader
                        AccountHeader headerResult = new AccountHeaderBuilder()
                                .withActivity(act)
                                .withTranslucentStatusBar(true)
                                .withHeaderBackground(R.color.lightBlue)
                                .addProfiles(
                                        new ProfileDrawerItem().withName(currentUser).withIcon(R.mipmap.ic_launcher)
                                )
                                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                                    @Override
                                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                                        return false;
                                    }
                                })
                                .build();

                        //The DrawerBuilder class allows the developer to generate a navigation drawer and has methods that can be used
                        //for defining the nav drawers behaviour and appearance
                        result = new DrawerBuilder()
                                .withAccountHeader(headerResult)
                                .withActivity(act)
                                .addDrawerItems(

                                        //Adding new items to the nav drawer
                                        new PrimaryDrawerItem().withName(R.string.drawer_item_map_view).withIcon(R.drawable.ic_map_black_24dp).withIdentifier(1).withSelectable(false),
                                        new PrimaryDrawerItem().withName(R.string.drawer_item_profile).withIcon(R.drawable.ic_person_black_24dp).withIdentifier(2).withSelectable(false),
                                        new PrimaryDrawerItem().withName(R.string.drawer_item_trip_log).withIcon(R.drawable.ic_check_black_24dp).withIdentifier(3).withSelectable(false),
                                        new PrimaryDrawerItem().withName(R.string.drawer_item_sign_out).withIcon(R.drawable.ic_exit_to_app_black_24dp).withIdentifier(4).withSelectable(false)

                                ).withSelectedItem(item)
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    //When an item is clicked in the nav drawer the onItemClick method determines which activity the app should navigate too
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                                        if (drawerItem != null) {

                                            Intent intent = null;

                                            if (drawerItem.getIdentifier() == 1) {

                                                intent = new Intent(activity , MapViewActivity.class);


                                            } else if (drawerItem.getIdentifier() == 2) {

                                                intent = new Intent(activity, ProfileSettingsActivity.class);


                                            }
                                            else if (drawerItem.getIdentifier() == 3) {



                                                intent = new Intent(activity, TripLogActivity.class);



                                            }
                                            else if (drawerItem.getIdentifier() == 4) {

                                                FirebaseAuth.getInstance().signOut();
                                                intent = new Intent(activity, LoginActivity.class);

                                            }

                                            if (intent != null) {
                                                activity.startActivity(intent);

                                            }

                                        }

                                        return false;
                                    }
                                }).build();

                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });






    }

    public void getUserFullName(){
        DocumentReference docRef = db.collection("User Preferences").document(mAuth.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {

                        currentUser = document.getString("User_Fullname");

                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });
    }

    public void openNavDrawer(){
        result.openDrawer();
    }
/*
    public void getUserTripLogs(String userUID){

        String tripLogCollID = "TRIPLOG" + userUID;

        tripLogColRef = db.collection(tripLogCollID);

        tripLogColRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(QueryDocumentSnapshot documentSnapshots:queryDocumentSnapshots){

                    LogRecord logRec = documentSnapshots.toObject(LogRecord.class);
                    count.add(logRec.getDateTime());

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG,e.toString());
            }
        });


    }
*/

}
