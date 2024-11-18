package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;

public class EmailEditActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private Button buttonUpdate;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_edit);

        editTextEmail = findViewById(R.id.editTextText4);
        buttonUpdate = findViewById(R.id.button7);
        dbHelper = new DatabaseHelper(this);

        // Access the currentUserId from MainActivity
        userId = MainActivity.currentUserId;

        // Retrieve the logged-in user's email
        Cursor cursor = dbHelper.getUserDetails(userId);
        if (cursor != null && cursor.moveToFirst()) {
            String currentEmail = cursor.getString(cursor.getColumnIndex("EMAIL"));
            editTextEmail.setText(currentEmail);
        }
        if (cursor != null) {
            cursor.close();
        }

        buttonUpdate.setOnClickListener(v -> {
            String newEmail = editTextEmail.getText().toString();
            if (!newEmail.isEmpty()) {
                boolean updateSuccessful = dbHelper.updateUser(userId, null, newEmail, null, null);
                if (updateSuccessful) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    // Notify the user of the failed update
                }
            }
        });
    }
}