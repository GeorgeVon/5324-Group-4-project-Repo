package com.Group4.personalAssistant;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {

    EditText username, password, email, phone, address;
    Button btnBack, btnDone;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // VARIABLE DEFINITIONS
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);

        //BUTTON FUNCTIONALITY
        //done button
        btnDone = findViewById(R.id.btnDone);
        btnDone.setOnClickListener(v -> validateAndRegister());

        //return to main screen button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
    private void validateAndRegister()
    {

        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String ph = phone.getText().toString().trim();

        String errorMessage = "";

        // Username check
        if (user.isEmpty()) {
            errorMessage += "Username is required\n";
        }

        // Password validation
        if (!isValidPassword(pass)) {
            errorMessage += "Password must be 8+ chars, include upper, lower, special char\n";
        }

        // Email validation
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            errorMessage += "Invalid email format\n";
        }

        // Phone validation
        if (ph.length() < 10)
        {
            errorMessage += "Phone number must be at least 10 digits\n";
        }

    }
    // Password validation
    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;

        boolean hasUpper = false, hasLower = false, hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;

            // Disallowed characters
            if (c == '-' || c == ',' || c == '>' || c == '<') return false;
        }

        return hasUpper && hasLower && hasSpecial;
    }

    private void saveToFirebase(String user, String pass, String mail, String ph) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("username", user);
        map.put("email", mail);
        map.put("phone", ph);

        db.collection("Users")
                .add(map)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
// pop-up for any fields populated incorrectly
    private void showPopup(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Validation Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}