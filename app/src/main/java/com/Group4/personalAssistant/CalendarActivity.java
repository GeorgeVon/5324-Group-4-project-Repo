package com.Group4.personalAssistant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class CalendarActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "schedule_prefs";
    private static final String SCHEDULE_KEY = "schedule_entries";

    private TtsManager ttsManager;
    private EditText editTaskText;
    private Button btnSpeakTask;
    private Button btnStopSpeech;
    private Button btnAddEvent;
    private Button btnAddTask;
    private TextView tvSelectedDate;
    private TextView tvScheduleEmpty;
    private LinearLayout scheduleContainer;
    private CalendarView calendarView;
    private Calendar selectedDate;
    private ArrayList<String> scheduleEntries;
    private SharedPreferences preferences;

    private TextView tvGreeting;
    private Button btnNewQuery, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Dashboard/Voice Assistant elements
        tvGreeting = findViewById(R.id.tvGreeting);
        // btnNewQuery = findViewById(R.id.btnNewQuery); // Not in activity_calendar.xml
        btnLogout = findViewById(R.id.btnLogout);

        String name = getIntent().getStringExtra("USER_NAME");
        if (name == null || name.isEmpty()) {
            name = "User";
        }
        if (tvGreeting != null) {
            tvGreeting.setText("Hello, " + name);
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(CalendarActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }

        // Marissa's Calendar/Schedule elements
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

        selectedDate = Calendar.getInstance();
        updateSelectedDateLabel();
        loadScheduleEntries();
        updateScheduleDisplay();

        if (calendarView != null) {
            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateSelectedDateLabel();
            });
        }

        if (btnAddEvent != null) btnAddEvent.setOnClickListener(v -> showAddEventDialog());
        if (btnAddTask != null) btnAddTask.setOnClickListener(v -> showAddTaskDialog());

        if (btnSpeakTask != null) {
            btnSpeakTask.setOnClickListener(v -> {
                if (!ttsManager.isReady()) {
                    Toast.makeText(this, R.string.home_page_tts_not_ready, Toast.LENGTH_SHORT).show();
                    return;
                }

                String scheduleToRead = "";
                if (scheduleEntries.isEmpty()) {
                    scheduleToRead = "You have no events or tasks scheduled.";
                } else {
                    StringBuilder sb = new StringBuilder("You have " + scheduleEntries.size() + " items scheduled. ");
                    for (int i = 0; i < scheduleEntries.size(); i++) {
                        String entry = scheduleEntries.get(i).replace("\n", ". ");
                        sb.append("Item ").append(i + 1).append(": ").append(entry).append(". ");
                    }
                    scheduleToRead = sb.toString();
                }

                ttsManager.speakTask(getString(R.string.home_page_title), scheduleToRead);
            });
        }

        if (btnStopSpeech != null) btnStopSpeech.setOnClickListener(v -> ttsManager.stop());

        // Setup Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_calendar);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_tasks) {
                    startActivity(new Intent(this, TaskActivity.class));
                    return true;
                } else if (id == R.id.nav_calendar) {
                    // Already here
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

    private void updateSelectedDateLabel() {
        if (tvSelectedDate != null) {
            tvSelectedDate.setText(getString(R.string.selected_date_label, formatDate(selectedDate)));
        }
    }

    private void showAddEventDialog() {
        Calendar eventTime = (Calendar) selectedDate.clone();
        eventTime.set(Calendar.HOUR_OF_DAY, 9);
        eventTime.set(Calendar.MINUTE, 0);

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(32, 24, 32, 24);

        EditText etEventTitle = new EditText(this);
        etEventTitle.setHint(getString(R.string.event_title_hint));
        dialogLayout.addView(etEventTitle);

        EditText etEventNotes = new EditText(this);
        etEventNotes.setHint(getString(R.string.event_notes_hint));
        dialogLayout.addView(etEventNotes);

        TextView tvTimeLabel = new TextView(this);
        tvTimeLabel.setText(getString(R.string.event_time_label));
        tvTimeLabel.setPadding(0, 24, 0, 0);
        dialogLayout.addView(tvTimeLabel);

        Button btnPickTime = new Button(this);
        btnPickTime.setText(getString(R.string.pick_time_button));
        dialogLayout.addView(btnPickTime);

        TextView tvTimeValue = new TextView(this);
        tvTimeValue.setText(formatTime(eventTime));
        tvTimeValue.setPadding(0, 8, 0, 0);
        dialogLayout.addView(tvTimeValue);

        btnPickTime.setOnClickListener(v -> new TimePickerDialog(this, (timePicker, hourOfDay, minute) -> {
            eventTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            eventTime.set(Calendar.MINUTE, minute);
            tvTimeValue.setText(formatTime(eventTime));
        }, eventTime.get(Calendar.HOUR_OF_DAY), eventTime.get(Calendar.MINUTE), false).show());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.add_event_title)
                .setView(dialogLayout)
                .setPositiveButton(R.string.add_event_button, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = etEventTitle.getText().toString().trim();
            String notes = etEventNotes.getText().toString().trim();
            if (title.isEmpty()) {
                title = getString(R.string.untitled_event);
            }

            String entry = getString(R.string.event_entry_template,
                    title,
                    formatDate(selectedDate),
                    formatTime(eventTime),
                    notes.isEmpty() ? getString(R.string.no_notes_text) : notes);
            addScheduleEntry(entry);
            dialog.dismiss();
        }));

        dialog.show();
    }

    private void showAddTaskDialog() {
        Calendar dueDate = (Calendar) selectedDate.clone();

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(32, 24, 32, 24);

        EditText etTaskTitle = new EditText(this);
        etTaskTitle.setHint(getString(R.string.task_title_hint));
        dialogLayout.addView(etTaskTitle);

        TextView tvDueDateLabel = new TextView(this);
        tvDueDateLabel.setText(getString(R.string.task_due_date_label));
        tvDueDateLabel.setPadding(0, 24, 0, 0);
        dialogLayout.addView(tvDueDateLabel);

        Button btnPickDueDate = new Button(this);
        btnPickDueDate.setText(getString(R.string.pick_due_date_button));
        dialogLayout.addView(btnPickDueDate);

        TextView tvDueDateValue = new TextView(this);
        tvDueDateValue.setText(formatDate(dueDate));
        tvDueDateValue.setPadding(0, 8, 0, 0);
        dialogLayout.addView(tvDueDateValue);

        btnPickDueDate.setOnClickListener(v -> new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    dueDate.set(Calendar.YEAR, year);
                    dueDate.set(Calendar.MONTH, month);
                    dueDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    tvDueDateValue.setText(formatDate(dueDate));
                }, dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH)).show());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.add_task_title)
                .setView(dialogLayout)
                .setPositiveButton(R.string.add_task_button, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            if (title.isEmpty()) {
                title = getString(R.string.untitled_task);
            }

            String entry = getString(R.string.task_entry_template,
                    title,
                    formatDate(dueDate));
            addScheduleEntry(entry);
            
            // Also save to Tasks activity preferences
            saveToOtherPrefs("tasks_prefs", "tasks_entries", entry);
            
            dialog.dismiss();
        }));

        dialog.show();
    }

    private void saveToOtherPrefs(String prefsName, String key, String entry) {
        SharedPreferences otherPrefs = getSharedPreferences(prefsName, MODE_PRIVATE);
        String saved = otherPrefs.getString(key, "");
        ArrayList<String> entries = new ArrayList<>();
        if (saved != null && !saved.isEmpty()) {
            try {
                JSONArray array = new JSONArray(saved);
                for (int i = 0; i < array.length(); i++) {
                    entries.add(array.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        entries.add(0, entry);
        JSONArray array = new JSONArray();
        for (String s : entries) {
            array.put(s);
        }
        otherPrefs.edit().putString(key, array.toString()).apply();
    }

    private void addScheduleEntry(String entry) {
        scheduleEntries.add(entry);
        updateScheduleDisplay();
        saveScheduleEntries();
    }

    private void updateScheduleDisplay() {
        if (scheduleContainer == null) return;
        scheduleContainer.removeAllViews();
        if (scheduleEntries.isEmpty()) {
            if (tvScheduleEmpty != null) tvScheduleEmpty.setVisibility(TextView.VISIBLE);
            return;
        }

        if (tvScheduleEmpty != null) tvScheduleEmpty.setVisibility(TextView.GONE);
        for (String entry : scheduleEntries) {
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setBackgroundResource(R.drawable.schedule_item_background);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 12);
            itemLayout.setLayoutParams(layoutParams);

            TextView itemView = new TextView(this);
            itemView.setText(entry);
            
            android.util.TypedValue typedValueText = new android.util.TypedValue();
            getTheme().resolveAttribute(R.attr.customSecondaryTextColor, typedValueText, true);
            itemView.setTextColor(typedValueText.data);

            itemView.setTextSize(14f);
            itemView.setPadding(18, 16, 18, 16);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            itemView.setLayoutParams(textParams);

            Button btnComplete = new Button(this);
            btnComplete.setText("Done");
            btnComplete.setBackgroundColor(android.graphics.Color.TRANSPARENT);

            // Get themed color
            android.util.TypedValue typedValue = new android.util.TypedValue();
            getTheme().resolveAttribute(R.attr.customSecondaryTextColor, typedValue, true);
            btnComplete.setTextColor(typedValue.data);

            btnComplete.setOnClickListener(v -> {
                scheduleEntries.remove(entry);
                removeFromOtherPrefs("tasks_prefs", "tasks_entries", entry);
                updateScheduleDisplay();
                saveScheduleEntries();
            });

            itemLayout.addView(itemView);
            itemLayout.addView(btnComplete);

            scheduleContainer.addView(itemLayout);
        }
    }

    private void removeFromOtherPrefs(String prefsName, String key, String entryToRemove) {
        SharedPreferences otherPrefs = getSharedPreferences(prefsName, MODE_PRIVATE);
        String saved = otherPrefs.getString(key, "");
        if (saved != null && !saved.isEmpty()) {
            try {
                JSONArray array = new JSONArray(saved);
                JSONArray newArray = new JSONArray();
                for (int i = 0; i < array.length(); i++) {
                    String item = array.getString(i);
                    if (!item.equals(entryToRemove)) {
                        newArray.put(item);
                    }
                }
                otherPrefs.edit().putString(key, newArray.toString()).apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveScheduleEntries() {
        JSONArray array = new JSONArray();
        for (String entry : scheduleEntries) {
            array.put(entry);
        }
        preferences.edit().putString(SCHEDULE_KEY, array.toString()).apply();
    }

    private void loadScheduleEntries() {
        String saved = preferences.getString(SCHEDULE_KEY, "");
        if (saved == null || saved.isEmpty()) {
            return;
        }

        try {
            JSONArray array = new JSONArray(saved);
            for (int i = 0; i < array.length(); i++) {
                scheduleEntries.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String formatDate(Calendar date) {
        return new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(date.getTime());
    }

    private String formatTime(Calendar time) {
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(time.getTime());
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
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.theme_default) {
                    ThemeHelper.setTheme(CalendarActivity.this, R.style.Theme_Group4);
                } else if (itemId == R.id.theme_dark) {
                    ThemeHelper.setTheme(CalendarActivity.this, R.style.Theme_Group4_Dark);
                } else if (itemId == R.id.theme_midnight) {
                    ThemeHelper.setTheme(CalendarActivity.this, R.style.Theme_Group4_Midnight);
                } else if (itemId == R.id.theme_colorblind) {
                    ThemeHelper.setTheme(CalendarActivity.this, R.style.Theme_Group4_ColorBlind);
                } else if (itemId == R.id.theme_black_and_white) {
                    ThemeHelper.setTheme(CalendarActivity.this, R.style.Theme_Group4_BlackAndWhite);
                }
                return true;
            }
        });
        popup.show();
    }
}