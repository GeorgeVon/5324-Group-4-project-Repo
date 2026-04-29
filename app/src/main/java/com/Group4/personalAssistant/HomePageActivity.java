package com.Group4.personalAssistant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomePageActivity extends AppCompatActivity {
    private TtsManager ttsManager;

    private TextView tvGreeting;
    private Button btnNewQuery, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Dashboard/Voice Assistant elements
        tvGreeting = findViewById(R.id.tvGreeting);
        btnNewQuery = findViewById(R.id.btnNewQuery);
        btnLogout = findViewById(R.id.btnLogout);

        String name = getIntent().getStringExtra("USER_NAME");
        if (name == null || name.isEmpty()) {
            name = "User";
        }
        tvGreeting.setText("Hello, " + name);

        if (btnNewQuery != null) {
            btnNewQuery.setOnClickListener(v -> {
                Intent intent = new Intent(HomePageActivity.this, VoiceAssistantActivity.class);
                startActivity(intent);
            });
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        ttsManager = new TtsManager(this);
        ttsManager.initialize();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            // There is no explicit home in bottom navigation, let's keep nav_recent unselected or nothing selected
            // bottomNavigationView.setSelectedItemId(R.id.nav_recent);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_tasks) {
                    startActivity(new Intent(this, TaskActivity.class));
                    return true;
                } else if (id == R.id.nav_calendar) {
                    startActivity(new Intent(this, CalendarActivity.class));
                    return true;
                } else if (id == R.id.nav_voice_assistant) {
                    startActivity(new Intent(this, VoiceAssistantActivity.class));
                    return true;
                } else if (id == R.id.nav_recent) {
                    // startActivity(new Intent(this, RecentActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        super.onDestroy();
    }

    public void showThemeMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.theme_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.theme_default) {
                ThemeHelper.setTheme(HomePageActivity.this, R.style.Theme_Group4);
            } else if (itemId == R.id.theme_dark) {
                ThemeHelper.setTheme(HomePageActivity.this, R.style.Theme_Group4_Dark);
            } else if (itemId == R.id.theme_midnight) {
                ThemeHelper.setTheme(HomePageActivity.this, R.style.Theme_Group4_Midnight);
            } else if (itemId == R.id.theme_colorblind) {
                ThemeHelper.setTheme(HomePageActivity.this, R.style.Theme_Group4_ColorBlind);
            } else if (itemId == R.id.theme_black_and_white) {
                ThemeHelper.setTheme(HomePageActivity.this, R.style.Theme_Group4_BlackAndWhite);
            }
            return true;
        });
        popup.show();
    }
}
