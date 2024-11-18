package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class UsernameEditActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private Button buttonUpdate;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username_edit);

        editTextUsername = findViewById(R.id.editTextText1);
        buttonUpdate = findViewById(R.id.button1);
        dbHelper = new DatabaseHelper(this);

        // Access the currentUserId from MainActivity
        userId = MainActivity.currentUserId;

        // Retrieve the logged-in user's username
        Cursor cursor = dbHelper.getUserDetails(userId);
        if (cursor != null && cursor.moveToFirst()) {
            String currentUsername = cursor.getString(cursor.getColumnIndex("USERNAME"));
            editTextUsername.setText(currentUsername);
        }
        if (cursor != null) {
            cursor.close();
        }

        buttonUpdate.setOnClickListener(v -> {
            String newUsername = editTextUsername.getText().toString();
            if (!newUsername.isEmpty()) {
                boolean updateSuccessful = dbHelper.updateUser(userId, newUsername, null, null, null);
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