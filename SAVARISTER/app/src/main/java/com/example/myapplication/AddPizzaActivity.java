package com.example.myapplication;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddPizzaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView imagePizzaPhoto;
    private EditText pizzaNameEdt, pizzaPriceLargeEdt, pizzaPriceMediumEdt, pizzaPriceSmallEdt, pizzaDescriptionEdt;
    private Button addpizzaBtn;
    private DatabaseHelper dbHelper;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pizza);

        // Enable Edge to Edge and handle window insets
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        pizzaNameEdt = findViewById(R.id.idEdtPizzaName);
        pizzaPriceLargeEdt = findViewById(R.id.idEdtLPrice);
        pizzaPriceMediumEdt = findViewById(R.id.idEdtMPrice);
        pizzaPriceSmallEdt = findViewById(R.id.idEdtSPrice);
        pizzaDescriptionEdt = findViewById(R.id.idEdtDescription);
        imagePizzaPhoto = findViewById(R.id.idImagePizzaPhoto);
        addpizzaBtn = findViewById(R.id.idBtnAddPizza);

        // Initialize the database handler
        dbHelper = new DatabaseHelper(this);

        // Set onClickListener for the add pizza button
        addpizzaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPizza();
            }
        });

        // Set onClickListener for the image view to pick an image
        imagePizzaPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
    }

    private void pickImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Set the image to ImageView
                imagePizzaPhoto.setImageBitmap(bitmap);

                // Convert image to byte[]
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();

                // Store the image bytes
                imageUri = Uri.fromFile(saveImageToInternalStorage(imageBytes));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Image not found.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File saveImageToInternalStorage(byte[] imageBytes) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory, System.currentTimeMillis() + ".jpg");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imageBytes);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    private void addPizza() {
        // Getting data from all edit text fields
        String pizzaName = pizzaNameEdt.getText().toString();
        String pizzaLargePrice = pizzaPriceLargeEdt.getText().toString();
        String pizzaMediumPrice = pizzaPriceMediumEdt.getText().toString();
        String pizzaSmallPrice = pizzaPriceSmallEdt.getText().toString();
        byte[] pizzaPhoto = imageUri != null ? getImageBytes(imageUri) : new byte[0]; // Get byte[] from image URI
        String pizzaDescription = pizzaDescriptionEdt.getText().toString();

        // Validating if the text fields are empty or not
        if (isEmpty(pizzaName) || isEmpty(pizzaLargePrice) || isEmpty(pizzaMediumPrice) || isEmpty(pizzaSmallPrice) || isEmpty(pizzaDescription)) {
            Toast.makeText(this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validating if the price fields contain only numbers
        if (!isNumeric(pizzaLargePrice) || !isNumeric(pizzaMediumPrice) || !isNumeric(pizzaSmallPrice)) {
            Toast.makeText(this, "Please enter valid prices..", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Calling a method to add new pizza to sqlite data and pass all our values to it
            dbHelper.addNewPizza(pizzaName, pizzaLargePrice, pizzaMediumPrice, pizzaSmallPrice, pizzaPhoto, pizzaDescription);

            // Displaying a toast message after adding the data
            Toast.makeText(this, "Pizza has been added.", Toast.LENGTH_SHORT).show();

            // Clearing the edit text fields
            clearEditTexts();
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getImageBytes(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private boolean isEmpty(String text) {
        return text.isEmpty();
    }

    private boolean isNumeric(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void clearEditTexts() {
        pizzaNameEdt.setText("");
        pizzaPriceLargeEdt.setText("");
        pizzaPriceMediumEdt.setText("");
        pizzaPriceSmallEdt.setText("");
        pizzaDescriptionEdt.setText("");
        imagePizzaPhoto.setImageResource(R.drawable.pizza111); // Assuming a default image
    }
}
