package com.daynemare.drifterapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daynemare.drifterapp.helpers.FireStoreDatabaseHelper;
import com.daynemare.drifterapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegistrationActivity extends AppCompatActivity {

    //Declarations
    private FirebaseAuth mAuth;
    private FireStoreDatabaseHelper db = new FireStoreDatabaseHelper();

    TextView title;
    EditText fullname;
    EditText email;
    EditText password;
    Button btSubmit;
    TextView link;
    RadioGroup radioGroup;
    RadioButton rBMeasurementSystem;
    RadioButton rbMetric;
    RadioButton rbImperial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        title = findViewById(R.id.tvRegisterTitle);
        email = findViewById(R.id.etEmail);
        fullname = findViewById(R.id.etFullName);
        password = findViewById(R.id.etPassword);
        btSubmit = findViewById(R.id.btSubmitReg);
        link = findViewById(R.id.tvToLogin);
        radioGroup = findViewById(R.id.radioGroup);
        rbImperial = findViewById(R.id.rbImperial);
        rbMetric = findViewById(R.id.rbMetric);

        toLoginScreen();
        registerUser();


    }

    public void checkRadioButton(View v){

        int radioId = radioGroup.getCheckedRadioButtonId();
        rBMeasurementSystem = findViewById(radioId);

    }

    private void registerUser(){
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                String emailInput = email.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();
                String fullNameInput = fullname.getText().toString().trim();

                if (TextUtils.isEmpty(fullNameInput)) {
                    Toast.makeText(getApplicationContext(), "Enter your full name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(emailInput)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(passwordInput)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6 ) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!rbImperial.isChecked() && !rbMetric.isChecked()){

                    Toast.makeText(getApplicationContext(), "Please select the measurement system you would like to use!", Toast.LENGTH_SHORT).show();
                    return;
                }


                //create user
                mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
                        .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(RegistrationActivity.this, "Registration Successful, You may now Login !", Toast.LENGTH_SHORT).show();

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegistrationActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                    db.createUserPreferenceDocument(mAuth.getUid(),email.getText().toString(),rBMeasurementSystem.getText().toString(),fullname.getText().toString(),"Dark",RegistrationActivity.this);

                                    finish();
                                }
                            }
                        });

            }
        });
    }

    private void toLoginScreen(){
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));

            }
        });

    }


}


