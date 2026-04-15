package com.Group4.personalAssistant;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Small wrapper around Android's TextToSpeech engine.
 * Keeps TTS setup out of the Activity so the code is easier to maintain.
 */
public class TtsManager implements TextToSpeech.OnInitListener {
    private static final String TAG = "TtsManager";

    private final Context appContext;
    private TextToSpeech textToSpeech;
    private boolean isReady = false;

    public TtsManager(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public void initialize() {
        if (textToSpeech == null) {
            textToSpeech = new TextToSpeech(appContext, this);
        }
    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            Log.e(TAG, "TextToSpeech initialization failed.");
            isReady = false;
            return;
        }

        int result = textToSpeech.setLanguage(Locale.US);
        isReady = result != TextToSpeech.LANG_MISSING_DATA
                && result != TextToSpeech.LANG_NOT_SUPPORTED;

        if (!isReady) {
            Log.e(TAG, "Selected TTS language is not supported on this device.");
            return;
        }

        textToSpeech.setSpeechRate(1.0f);
        textToSpeech.setPitch(1.0f);
    }

    public boolean isReady() {
        return isReady;
    }

    public void speak(String text) {
        if (!isReady || text == null || text.trim().isEmpty()) {
            return;
        }

        textToSpeech.speak(text.trim(), TextToSpeech.QUEUE_FLUSH, null, "GROUP4_TTS_UTTERANCE");
    }

    public void speakTask(String title, String details) {
        if (title == null) {
            title = "";
        }
        if (details == null) {
            details = "";
        }

        StringBuilder builder = new StringBuilder();
        if (!title.trim().isEmpty()) {
            builder.append(title.trim());
        }
        if (!details.trim().isEmpty()) {
            if (builder.length() > 0) {
                builder.append(". ");
            }
            builder.append(details.trim());
        }

        speak(builder.toString());
    }

    public void stop() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
            isReady = false;
        }
    }
}
