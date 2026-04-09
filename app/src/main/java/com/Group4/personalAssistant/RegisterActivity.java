package com.Group4.personalAssistant;

import android.os.Bundle;
import android.widget.Button;
//import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class RegisterActivity extends AppCompatActivity {

    //EditText username, password, email, phone, address;
    Button btnBack, btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        username = findViewById(R.id.username);
//        password = findViewById(R.id.password);
//        email = findViewById(R.id.email);
//        phone = findViewById(R.id.phone);
//        address = findViewById(R.id.address);
//        btnDone = findViewById(R.id.btnDone);
       // btnDone.setOnClickListener(v -> validateAndRegister());

        //return to main screen button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}