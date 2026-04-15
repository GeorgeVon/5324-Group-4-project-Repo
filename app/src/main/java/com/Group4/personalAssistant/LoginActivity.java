package com.Group4.personalAssistant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // VARIABLE DEFINITIONS
    //EditText etUsername, etPassword; //user input for these text fields
    Button btnLogin, btnRegister; //current buttons on Login page

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //BUTTON DEFINITIONS
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister); //register button

        btnLogin.setOnClickListener(v -> {
            Intent homeIntent = new Intent(LoginActivity.this, HomePageActivity.class);
            startActivity(homeIntent);
        });

        //REGISTER FUNCTIONS
        btnRegister.setOnClickListener(v -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        }); //send user to register page
    }
}