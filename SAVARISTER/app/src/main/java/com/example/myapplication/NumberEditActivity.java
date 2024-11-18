package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NumberEditActivity extends AppCompatActivity {

    private EditText editTextMobileNumber;
    private Button buttonUpdateMobile;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_edit);

        editTextMobileNumber = findViewById(R.id.editTextText1);
        buttonUpdateMobile = findViewById(R.id.button1);
        dbHelper = new DatabaseHelper(this);

        // Get user ID from shared preferences or intent
        userId = MainActivity.currentUserId;

        buttonUpdateMobile.setOnClickListener(v -> {
            String mobileNumber = editTextMobileNumber.getText().toString();
            if (!mobileNumber.isEmpty()) {
                boolean updateSuccessful = dbHelper.updateMobileNumber(userId, mobileNumber);
                if (updateSuccessful) {
                    Toast.makeText(NumberEditActivity.this, "Mobile number updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NumberEditActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}