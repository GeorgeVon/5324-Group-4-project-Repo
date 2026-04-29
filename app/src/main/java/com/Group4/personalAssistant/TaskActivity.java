package com.Group4.personalAssistant;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity {
    private static final String TASKS_PREFS = "tasks_prefs";
    private static final String TASKS_KEY = "tasks_entries";

    private TtsManager ttsManager;
    private EditText editTaskText, editSearchTasks;
    private Button btnSpeakTask, btnStopSpeech, btnAddTask, btnNewQuery;
    private TextView tvTaskEmpty;
    private LinearLayout taskContainer;
    private ArrayList<String> taskEntries;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        ttsManager = new TtsManager(this);
        ttsManager.initialize();

        editTaskText = findViewById(R.id.editTaskText);
        editSearchTasks = findViewById(R.id.editSearchTasks);
        btnSpeakTask = findViewById(R.id.btnSpeakTask);
        btnStopSpeech = findViewById(R.id.btnStopSpeech);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnNewQuery = findViewById(R.id.btnNewQuery);
        tvTaskEmpty = findViewById(R.id.tvTaskEmpty);
        taskContainer = findViewById(R.id.taskContainer);

        preferences = getSharedPreferences(TASKS_PREFS, MODE_PRIVATE);
        taskEntries = new ArrayList<>();

        loadTaskEntries();
        updateTaskDisplay(taskEntries);

        btnAddTask.setOnClickListener(v -> showAddTaskDialog(null));

        if (btnNewQuery != null) {
            btnNewQuery.setOnClickListener(v -> {
                Intent intent = new Intent(TaskActivity.this, VoiceAssistantActivity.class);
                startActivity(intent);
            });
        }

        btnSpeakTask.setOnClickListener(v -> {
            if (!ttsManager.isReady()) {
                Toast.makeText(this, R.string.home_page_tts_not_ready, Toast.LENGTH_SHORT).show();
                return;
            }

            String tasksToRead = "";
            if (taskEntries.isEmpty()) {
                tasksToRead = "You have no tasks currently.";
            } else {
                StringBuilder sb = new StringBuilder("You have " + taskEntries.size() + " tasks. ");
                for (int i = 0; i < taskEntries.size(); i++) {
                    String entry = taskEntries.get(i).replace("\n", ". ");
                    sb.append("Task ").append(i + 1).append(": ").append(entry).append(". ");
                }
                tasksToRead = sb.toString();
            }

            ttsManager.speakTask(getString(R.string.task_title), tasksToRead);
        });

        btnStopSpeech.setOnClickListener(v -> ttsManager.stop());

        editSearchTasks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTasks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_tasks);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_tasks) {
                    return true;
                } else if (id == R.id.nav_calendar) {
                    startActivity(new Intent(this, CalendarActivity.class));
                    return true;
                } else if (id == R.id.nav_voice_assistant) {
                    startActivity(new Intent(this, VoiceAssistantActivity.class));
                    return true;
                } else if (id == R.id.nav_recent) {
                    startActivity(new Intent(this, HomePageActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    private void filterTasks(String query) {
        ArrayList<String> filteredList = new ArrayList<>();
        for (String task : taskEntries) {
            if (task.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(task);
            }
        }
        updateTaskDisplay(filteredList);
    }

    private void showAddTaskDialog(final String existingEntry) {
        Calendar dueDate = Calendar.getInstance();
        String initialTitle = "";

        if (existingEntry != null) {
            // Simple parsing of "Task: [Title]\nDue: [Date]"
            String[] parts = existingEntry.split("\n");
            if (parts.length >= 1 && parts[0].startsWith("Task: ")) {
                initialTitle = parts[0].substring(6);
            }
        }

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(32, 24, 32, 24);

        EditText etTaskTitle = new EditText(this);
        etTaskTitle.setHint(getString(R.string.task_title_hint));
        etTaskTitle.setText(initialTitle);
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
                .setTitle(existingEntry == null ? R.string.add_task_title : R.string.task_title)
                .setView(dialogLayout)
                .setPositiveButton(existingEntry == null ? R.string.add_task_button : android.R.string.ok, null)
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
            
            if (existingEntry != null) {
                int index = taskEntries.indexOf(existingEntry);
                if (index != -1) {
                    taskEntries.set(index, entry);
                }
            } else {
                taskEntries.add(0, entry);
                // Also save to Calendar preferences for new tasks
                saveToOtherPrefs("schedule_prefs", "schedule_entries", entry);
            }
            
            updateTaskDisplay(taskEntries);
            saveTaskEntries();
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

    private void updateTaskDisplay(ArrayList<String> listToDisplay) {
        taskContainer.removeAllViews();
        if (listToDisplay.isEmpty()) {
            tvTaskEmpty.setVisibility(TextView.VISIBLE);
            return;
        }

        tvTaskEmpty.setVisibility(TextView.GONE);
        for (int i = 0; i < listToDisplay.size(); i++) {
            final String entry = listToDisplay.get(i);
            
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

            // Simple click to edit
            itemView.setOnClickListener(v -> showAddTaskDialog(entry));

            Button btnComplete = new Button(this);
            btnComplete.setText("Done");
            btnComplete.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            
            // Get themed color
            android.util.TypedValue typedValue = new android.util.TypedValue();
            getTheme().resolveAttribute(R.attr.customSecondaryTextColor, typedValue, true);
            btnComplete.setTextColor(typedValue.data);

            btnComplete.setOnClickListener(v -> {
                taskEntries.remove(entry);
                removeFromOtherPrefs("schedule_prefs", "schedule_entries", entry);
                updateTaskDisplay(taskEntries);
                saveTaskEntries();
            });

            itemLayout.addView(itemView);
            itemLayout.addView(btnComplete);

            taskContainer.addView(itemLayout);
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

    private void showDeleteDialog(String entryToDelete) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    taskEntries.remove(entryToDelete);
                    updateTaskDisplay(taskEntries);
                    saveTaskEntries();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveTaskEntries() {
        JSONArray array = new JSONArray();
        for (String entry : taskEntries) {
            array.put(entry);
        }
        preferences.edit().putString(TASKS_KEY, array.toString()).apply();
    }

    private void loadTaskEntries() {
        String saved = preferences.getString(TASKS_KEY, "");
        if (saved == null || saved.isEmpty()) return;

        try {
            JSONArray array = new JSONArray(saved);
            for (int i = 0; i < array.length(); i++) {
                taskEntries.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String formatDate(Calendar date) {
        return new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).format(date.getTime());
    }

    @Override
    protected void onDestroy() {
        if (ttsManager != null) ttsManager.shutdown();
        super.onDestroy();
    }
}