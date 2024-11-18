package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText editTextEmail, editTextPassword;
    Button buttonLogin, buttonRegister;
    public static int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);



        myDb = new DatabaseHelper(this);

        editTextEmail = findViewById(R.id.editTextText2);
        editTextPassword = findViewById(R.id.editTextText3);
        buttonLogin = findViewById(R.id.button4);
        buttonRegister = findViewById(R.id.button5);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isUserValid = myDb.checkUser(email, password);
                boolean isAdminValid = myDb.checkAdmin(email, password);

                if (isUserValid)
                {
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    currentUserId = myDb.getUserIdByEmail(email);

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                }
                else if(isAdminValid)
                {
                    Toast.makeText(MainActivity.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();

                    currentUserId = myDb.getUserIdByEmail(email);

                    Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                    startActivity(intent);
                    finish();
                }

                else
                {
                    Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void hello(View view)
    {

        Intent intent = new Intent(this,SignupActivity.class);
        startActivity(intent);

    }





}