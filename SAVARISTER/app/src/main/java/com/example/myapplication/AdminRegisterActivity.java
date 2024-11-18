package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class AdminRegisterActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private EditText editTextEmail, editTextPassword, editTextDeleteEmail;
    private Button buttonRegister, buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        // Enable Edge to Edge and handle window insets
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the database handler
        dbHelper = new DatabaseHelper(this);

        // Find views by ID
        editTextEmail = findViewById(R.id.inputEmail);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        editTextDeleteEmail = findViewById(R.id.editTextDeleteEmail);
        buttonRegister = findViewById(R.id.button2);
        buttonDelete = findViewById(R.id.buttonDelete);

        // Set onClickListener for the register button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(AdminRegisterActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isInserted = dbHelper.insertAdmin(email, password);
                if (isInserted) {
                    Toast.makeText(AdminRegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    // Optionally navigate to a different fragment or activity here
                } else {
                    Toast.makeText(AdminRegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set onClickListener for the delete button
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextDeleteEmail.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(AdminRegisterActivity.this, "Please enter an email to delete", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isDeleted = dbHelper.deleteAdmin(email);
                if (isDeleted) {
                    Toast.makeText(AdminRegisterActivity.this, "Admin Deleted Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminRegisterActivity.this, "Deletion Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
