package com.Group4.personalAssistant;

import static com.Group4.personalAssistant.R.*;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class RecoveryActivity extends AppCompatActivity {
    EditText username, password, email, phone;
    Button btnBack, btnResetPassword;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);
        // VARIABLE DEFINITIONS
        //username = findViewById(R.id.username);
        //password = findViewById(R.id.password);
        email = findViewById(R.id.email);
       // phone = findViewById(R.id.phone);

        //BUTTON FUNCTIONALITY
        //done button
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(v -> {
            finish(); // closes this screen and goes back
        });

        //return to main screen button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // closes this screen and goes back
        });


    }
}
