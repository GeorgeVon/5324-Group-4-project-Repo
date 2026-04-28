package com.Group4.personalAssistant;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RecoveryActivity extends AppCompatActivity {
    private EditText emailEditText;
    private Button btnBack, btnResetPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnBack);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RecoveryActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RecoveryActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
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
                    ThemeHelper.setTheme(RecoveryActivity.this, R.style.Theme_Group4);
                } else if (itemId == R.id.theme_dark) {
                    ThemeHelper.setTheme(RecoveryActivity.this, R.style.Theme_Group4_Dark);
                } else if (itemId == R.id.theme_midnight) {
                    ThemeHelper.setTheme(RecoveryActivity.this, R.style.Theme_Group4_Midnight);
                } else if (itemId == R.id.theme_colorblind) {
                    ThemeHelper.setTheme(RecoveryActivity.this, R.style.Theme_Group4_ColorBlind);
                } else if (itemId == R.id.theme_black_and_white) {
                    ThemeHelper.setTheme(RecoveryActivity.this, R.style.Theme_Group4_BlackAndWhite);
                }
                return true;
            }
        });
        popup.show();
    }
}