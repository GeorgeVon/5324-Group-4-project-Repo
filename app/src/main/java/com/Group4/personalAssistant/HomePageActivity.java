package com.Group4.personalAssistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomePageActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private Button btnNewQuery, btnLogout, btnThemes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        tvGreeting = findViewById(R.id.tvGreeting);
        btnNewQuery = findViewById(R.id.btnNewQuery);
        btnLogout = findViewById(R.id.btnLogout);
        btnThemes = new Button(this); // We'll add it to layout or use a popup

        String name = getIntent().getStringExtra("USER_NAME");
        if (name == null || name.isEmpty()) {
            name = "User";
        }
        tvGreeting.setText("Hello, " + name);

        btnNewQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, VoiceAssistantActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
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
                    ThemeHelper.setTheme(HomePageActivity.this, R.style.Theme_Group4);
                } else if (itemId == R.id.theme_dark) {
                    ThemeHelper.setTheme(HomePageActivity.this, R.style.Theme_Group4_Dark);
                } else if (itemId == R.id.theme_midnight) {
                    ThemeHelper.setTheme(HomePageActivity.this, R.style.Theme_Group4_Midnight);
                } else if (itemId == R.id.theme_colorblind) {
                    ThemeHelper.setTheme(HomePageActivity.this, R.style.Theme_Group4_ColorBlind);
                }
                return true;
            }
        });
        popup.show();
    }
}