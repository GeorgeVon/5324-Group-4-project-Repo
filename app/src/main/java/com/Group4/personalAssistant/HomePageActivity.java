package com.Group4.personalAssistant;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {
    private TtsManager ttsManager;
    private EditText editTaskText;
    private Button btnSpeakTask;
    private Button btnStopSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        ttsManager = new TtsManager(this);
        ttsManager.initialize();

        editTaskText = findViewById(R.id.editTaskText);
        btnSpeakTask = findViewById(R.id.btnSpeakTask);
        btnStopSpeech = findViewById(R.id.btnStopSpeech);

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
    }

    @Override
    protected void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        super.onDestroy();
    }
}
