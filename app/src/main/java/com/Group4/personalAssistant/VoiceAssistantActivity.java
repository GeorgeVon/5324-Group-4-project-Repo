package com.Group4.personalAssistant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceAssistantActivity extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 100;
    private static final int PERMISSION_RECORD_AUDIO_REQUEST_CODE = 200;
    private TextView tvSpeechResult;
    private FloatingActionButton btnMic;
    private ProgressBar progressBar;
    private MaterialButton btnAddDetectedTask;
    private TtsManager ttsManager;
    private GeminiManager geminiManager;
    private static final String GEMINI_API_KEY = "AIzaSyALCUPkCTJPGglqAKwKu2Twa754TagtHJM"; 

    private Task detectedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_assistant);

        tvSpeechResult = findViewById(R.id.tvSpeechResult);
        btnMic = findViewById(R.id.btnMic);
        progressBar = findViewById(R.id.progressBar);
        btnAddDetectedTask = findViewById(R.id.btnAddDetectedTask);

        ttsManager = new TtsManager(this);
        ttsManager.initialize();

        geminiManager = new GeminiManager();

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndStartSpeech();
            }
        });

        btnAddDetectedTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detectedTask != null) {
                    saveTaskToPrefs(detectedTask);
                    Toast.makeText(VoiceAssistantActivity.this, "Task added to calendar!", Toast.LENGTH_SHORT).show();
                    btnAddDetectedTask.setVisibility(View.GONE);
                }
            }
        });
    }

    private void saveTaskToPrefs(Task task) {
        android.content.SharedPreferences preferences = getSharedPreferences("schedule_prefs", MODE_PRIVATE);
        String saved = preferences.getString("schedule_entries", "");
        ArrayList<String> scheduleEntries = new ArrayList<>();
        if (saved != null && !saved.isEmpty()) {
            try {
                org.json.JSONArray array = new org.json.JSONArray(saved);
                for (int i = 0; i < array.length(); i++) {
                    scheduleEntries.add(array.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        String entry = getString(R.string.task_entry_template, task.getTitle(), task.getDueDate());
        scheduleEntries.add(entry);
        
        org.json.JSONArray array = new org.json.JSONArray();
        for (String s : scheduleEntries) {
            array.put(s);
        }
        preferences.edit().putString("schedule_entries", array.toString()).apply();
    }

    private void checkPermissionAndStartSpeech() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_RECORD_AUDIO_REQUEST_CODE);
        } else {
            startSpeechToText();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSpeechToText();
            } else {
                Toast.makeText(this, "Permission denied to record audio", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "How can I help you?");

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Speech recognition is not supported on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                tvSpeechResult.setText(getString(R.string.user_label, spokenText));
                
                // Show progress bar and disable mic
                progressBar.setVisibility(View.VISIBLE);
                btnMic.setEnabled(false);
                
                // Send to Gemini
                geminiManager.generateResponse(spokenText, new GeminiManager.GeminiResponseCallback() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            btnMic.setEnabled(true);
                            String cleanResponse;
                            detectedTask = null;

                            // Look for JSON block
                            if (response.contains("{") && response.contains("}")) {
                                try {
                                    int start = response.indexOf("{");
                                    int end = response.lastIndexOf("}") + 1;
                                    String jsonStr = response.substring(start, end);

                                    // Extract text before the JSON block
                                    String textBefore = response.substring(0, start).trim();
                                    // Remove markdown code block markers if they exist
                                    if (textBefore.endsWith("```json")) {
                                        cleanResponse = textBefore.substring(0, textBefore.length() - 7).trim();
                                    } else if (textBefore.endsWith("```")) {
                                        cleanResponse = textBefore.substring(0, textBefore.length() - 3).trim();
                                    } else {
                                        cleanResponse = textBefore;
                                    }

                                    JSONObject json = new JSONObject(jsonStr);
                                    if (json.has("task")) {
                                        JSONObject taskJson = json.getJSONObject("task");
                                        String title = taskJson.getString("title");
                                        String date = taskJson.getString("date");
                                        detectedTask = new Task(title, date);
                                        btnAddDetectedTask.setVisibility(View.VISIBLE);
                                    }

                                    if (cleanResponse.isEmpty()) {
                                        cleanResponse = "Task detected: " + detectedTask.getTitle();
                                    }
                                } catch (JSONException e) {
                                    // If JSON parsing fails, keep the original response and log the error
                                    e.printStackTrace();
                                    cleanResponse = response; // Fallback to the full response
                                }
                            } else {
                                // No JSON found, use the full response
                                cleanResponse = response;
                            }

                            tvSpeechResult.append("\n\n" + getString(R.string.gemini_label, cleanResponse));
                            ttsManager.speak(cleanResponse);
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            btnMic.setEnabled(true);
                            Toast.makeText(VoiceAssistantActivity.this, getString(R.string.gemini_error, throwable.getMessage()), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (ttsManager != null) {
            ttsManager.shutdown();
        }
        super.onDestroy();
    }
}