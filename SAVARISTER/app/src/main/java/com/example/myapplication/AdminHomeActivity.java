package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void addburger(View view)
    {
        Intent intent = new Intent(this, AddBurgerActivity.class);
        startActivity(intent);
    }

    public void addpizza(View view)
    {
        Intent intent = new Intent(this, AddPizzaActivity.class);
        startActivity(intent);
    }

    public void addAdmin(View view)
    {
        Intent intent = new Intent(this, AdminRegisterActivity.class);
        startActivity(intent);
    }

    public void logoute(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}