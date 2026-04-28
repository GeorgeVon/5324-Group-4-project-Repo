package com.Group4.personalAssistant;

import androidx.annotation.NonNull;

import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiManager {
    private final GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private static final String SYSTEM_PROMPT = "You are a helpful personal assistant. If the user asks to create a task, remind them or schedule something, include a JSON block at the end of your response in the following format: {\"task\": {\"title\": \"task title\", \"date\": \"YYYY-MM-DD\"}}. Use the current date if none is specified. Otherwise, just respond naturally.\n\nUser: ";

    public GeminiManager() {
        // Initialize the Gemini Developer API backend service
        // Create a `GenerativeModel` instance with a model that supports your use case
        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-3-flash-preview");


        // Use the GenerativeModelFutures Java compatibility layer which offers
        // support for ListenableFuture and Publisher APIs
        this.model = GenerativeModelFutures.from(ai);
    }

    public interface GeminiResponseCallback {
        void onSuccess(String response);
        void onError(Throwable throwable);
    }

    public void generateResponse(String prompt, GeminiResponseCallback callback) {
        String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
        String fullPrompt = "Today is " + currentDate + ". " + SYSTEM_PROMPT + prompt;
        
        // Prefix the prompt with the system instructions
        Content content = new Content.Builder()
                .addText(fullPrompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                callback.onSuccess(resultText);
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                callback.onError(t);
            }
        }, executor);
    }
}
