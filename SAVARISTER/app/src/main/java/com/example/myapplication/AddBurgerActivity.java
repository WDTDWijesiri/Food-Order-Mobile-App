package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddBurgerActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText foodNameEdt, mfoodPriceEdt, lfoodPriceEdt, foodDescriptionEdt;
    private Button addfoodBtn;
    private ImageView foodImageView;
    private Uri imageUri;
    private DatabaseHelper dbHelper;
    private byte[] imageByteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_burger);

        // Handle window insets for edge-to-edge mode
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupDatabase();
        setupButtonClickListener();
    }

    private void initViews() {
        foodNameEdt = findViewById(R.id.idEdtFoodName);
        mfoodPriceEdt = findViewById(R.id.idEdtmPrice);
        lfoodPriceEdt = findViewById(R.id.idEdtLPrice);
        foodDescriptionEdt = findViewById(R.id.idEdtFoodDescription);
        addfoodBtn = findViewById(R.id.idBtnAddFood);
        foodImageView = findViewById(R.id.idImgFood);

        Button selectImageBtn = findViewById(R.id.idBtnSelectImage);
        selectImageBtn.setOnClickListener(v -> openImageSelector());
    }

    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
    }

    private void setupButtonClickListener() {
        addfoodBtn.setOnClickListener(v -> {
            String foodName = foodNameEdt.getText().toString();
            String foodMPrice = mfoodPriceEdt.getText().toString();
            String foodLPrice = lfoodPriceEdt.getText().toString();
            String foodDescription = foodDescriptionEdt.getText().toString();

            if (foodName.isEmpty() || foodMPrice.isEmpty() || foodLPrice.isEmpty() || foodDescription.isEmpty() || imageByteArray == null) {
                Toast.makeText(this, "Please fill all the fields and select an image", Toast.LENGTH_SHORT).show();
            } else {
                dbHelper.addNewFood(foodName, foodMPrice, foodLPrice, imageByteArray, foodDescription);
                Toast.makeText(this, "Food added successfully", Toast.LENGTH_SHORT).show();
                clearInputs();
            }
        });
    }

    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                foodImageView.setImageBitmap(bitmap);
                imageByteArray = convertBitmapToByteArray(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void clearInputs() {
        foodNameEdt.setText("");
        mfoodPriceEdt.setText("");
        lfoodPriceEdt.setText("");
        foodDescriptionEdt.setText("");
        foodImageView.setImageResource(0);
        imageByteArray = null;
    }
}
