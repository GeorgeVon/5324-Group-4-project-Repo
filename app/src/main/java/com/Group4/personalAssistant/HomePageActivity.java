package com.Group4.personalAssistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;

import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "schedule_prefs";
    private static final String SCHEDULE_KEY = "schedule_entries";

    private TtsManager ttsManager;
    private ArrayList<String> scheduleEntries;
    private SharedPreferences preferences;

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

        ttsManager = new TtsManager(this);
        ttsManager.initialize();

        editTaskText = findViewById(R.id.editTaskText);
        btnSpeakTask = findViewById(R.id.btnSpeakTask);
        btnStopSpeech = findViewById(R.id.btnStopSpeech);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        btnAddTask = findViewById(R.id.btnAddTask);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvScheduleEmpty = findViewById(R.id.tvScheduleEmpty);
        scheduleContainer = findViewById(R.id.scheduleContainer);
        calendarView = findViewById(R.id.calendarView);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        scheduleEntries = new ArrayList<>();
        loadScheduleEntries();
        updateScheduleDisplay();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateSelectedDateLabel();
        });

        btnAddEvent.setOnClickListener(v -> showAddEventDialog());
        btnAddTask.setOnClickListener(v -> showAddTaskDialog());

        btnSpeakTask.setOnClickListener(v -> {
            String taskText = editTaskText.getText().toString().trim();
            if (taskText.isEmpty()) {
                taskText = getString(R.string.home_page_default_task);
            }

            if (!ttsManager.isReady()) {
                Toast.makeText(this, R.string.home_page_tts_not_ready, Toast.LENGTH_SHORT).show();
                return;
            }

            ttsManager.speakTask(getString(R.string.home_page_title), taskText);
        });

        btnStopSpeech.setOnClickListener(v -> ttsManager.stop());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_recent);
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
                    return true;
                }
                return false;
            });
        }
    }

    private void loadScheduleEntries() {
        String saved = preferences.getString(SCHEDULE_KEY, "");
        if (saved == null || saved.isEmpty()) {
            return;
        }
        // ... (omitted JSON parsing for simplicity as display logic is removed)
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
            }
            return true;
        });
        popup.show();
    }
}
