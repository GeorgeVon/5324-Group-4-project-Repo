package com.Group4.personalAssistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // VARIABLE DEFINITIONS
    EditText etUsername, etPassword; //user input for these text fields
    Button btnLogin, btnRegister, btnForgot; //current buttons on Login page

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //BUTTON DEFINITIONS
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
        btnRegister = findViewById(R.id.btnRegister); //register button
        btnLogin = findViewById(R.id.btnLogin); //login button
        btnForgot = findViewById(R.id.btnForgot); //forgot button


        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            // For now, simple bypass to Home Page
            Intent homeIntent = new Intent(LoginActivity.this, HomePageActivity.class);
            if (!username.isEmpty()) {
                homeIntent.putExtra("USER_NAME", username);
            }
            startActivity(homeIntent);
            finish();
        });

        //REGISTER FUNCTIONS
        btnRegister.setOnClickListener(v -> {
            Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        }); //send user to register page

        //FORGOT FUNCTIONS
        // FORGOT USERNAME/PASSWORD
        btnForgot.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RecoveryActivity.class));
        });
    }

    public void showThemeMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.theme_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.theme_default) {
                    ThemeHelper.setTheme(LoginActivity.this, R.style.Theme_Group4);
                } else if (itemId == R.id.theme_dark) {
                    ThemeHelper.setTheme(LoginActivity.this, R.style.Theme_Group4_Dark);
                } else if (itemId == R.id.theme_midnight) {
                    ThemeHelper.setTheme(LoginActivity.this, R.style.Theme_Group4_Midnight);
                } else if (itemId == R.id.theme_colorblind) {
                    ThemeHelper.setTheme(LoginActivity.this, R.style.Theme_Group4_ColorBlind);
                }
                return true;
            }
        });
        popup.show();
    }
}