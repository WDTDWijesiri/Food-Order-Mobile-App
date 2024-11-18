package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class BurgerBuyActivity extends AppCompatActivity {

    private Spinner burgerTypeSpinner;
    private ImageView burgerImageView;
    private TextView burgerDescriptionTextView;
    private RadioGroup sizeRadioGroup;
    private CheckBox toppingCheese, toppingOlives, toppingMushrooms;
    private TextView totalPriceTextView;
    private Button addToCartButton;

    private DatabaseHelper dbHelper;

    private HashMap<String, BurgerDetails> burgerDetailsMap = new HashMap<>();

    private double currentBurgerPrice;

    // Hardcoded topping prices
    private final double cheesePrice = 50;
    private final double olivesPrice = 70;
    private final double mushroomsPrice = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_burger_buy);

        // Initialize UI components
        burgerTypeSpinner = findViewById(R.id.BurgerTypeSpinner1);
        burgerImageView = findViewById(R.id.BurgerImageView1);
        burgerDescriptionTextView = findViewById(R.id.BurgerDescriptionTextView1);
        sizeRadioGroup = findViewById(R.id.sizeRadioGroup);
        toppingCheese = findViewById(R.id.toppingCheese);
        toppingOlives = findViewById(R.id.toppingOlives);
        toppingMushrooms = findViewById(R.id.toppingMushrooms);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        addToCartButton = findViewById(R.id.addToCartButton);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Load burger details from the database
        loadBurgerDetailsFromDatabase();

        // Set up spinner listener
        burgerTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBurger = parent.getItemAtPosition(position).toString();
                updateBurgerDetails(selectedBurger);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set up radio group listener
        sizeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> calculateTotalPrice());

        // Set up checkbox listeners
        toppingCheese.setOnCheckedChangeListener((buttonView, isChecked) -> calculateTotalPrice());
        toppingOlives.setOnCheckedChangeListener((buttonView, isChecked) -> calculateTotalPrice());
        toppingMushrooms.setOnCheckedChangeListener((buttonView, isChecked) -> calculateTotalPrice());

        // Add to cart button listener
        addToCartButton.setOnClickListener(v -> {
            String selectedBurgerName = burgerTypeSpinner.getSelectedItem().toString();
            BurgerDetails burgerDetails = burgerDetailsMap.get(selectedBurgerName);

            if (burgerDetails != null) {
                // Get current selected size
                int selectedSizeId = sizeRadioGroup.getCheckedRadioButtonId();
                String itemSize = "";
                if (selectedSizeId == R.id.radioMedium) {
                    itemSize = "Medium";
                } else if (selectedSizeId == R.id.radioLarge) {
                    itemSize = "Large";
                }

                // Get current total price
                double itemTotalPrice = currentBurgerPrice;
                if (toppingCheese.isChecked()) itemTotalPrice += cheesePrice;
                if (toppingOlives.isChecked()) itemTotalPrice += olivesPrice;
                if (toppingMushrooms.isChecked()) itemTotalPrice += mushroomsPrice;

                // Insert the item into the cart
                boolean isInserted = dbHelper.insertIntoCart(
                        burgerDetails.getPhoto(),
                        selectedBurgerName,
                        burgerDetails.getDescription(),
                        itemSize,
                        itemTotalPrice
                );

                if (isInserted) {
                    Toast.makeText(BurgerBuyActivity.this, "Burger added to cart!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BurgerBuyActivity.this, "Failed to add burger to cart.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadBurgerDetailsFromDatabase() {
        Cursor cursor = dbHelper.getAllBurgers();
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.FOOD_NAME_COL));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.FOOD_DESCRIPTION_COL));
                byte[] photoBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.FOOD_PHOTO_COL));
                double mediumPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.FOOD_MPRICE_COL));
                double largePrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.FOOD_LPRICE_COL));

                BurgerDetails burgerDetails = new BurgerDetails(name, description, photoBytes, mediumPrice, largePrice);
                burgerDetailsMap.put(name, burgerDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Populate spinner with burger names
        ArrayList<String> burgerNames = new ArrayList<>(burgerDetailsMap.keySet());
        burgerTypeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, burgerNames));
    }

    private void updateBurgerDetails(String burgerName) {
        BurgerDetails burgerDetails = burgerDetailsMap.get(burgerName);

        if (burgerDetails != null) {
            // Update description
            burgerDescriptionTextView.setText(burgerDetails.getDescription());

            // Update image using the getPhotoBitmap() method
            Bitmap burgerImageBitmap = burgerDetails.getPhotoBitmap();
            if (burgerImageBitmap != null) {
                burgerImageView.setImageBitmap(burgerImageBitmap);
            } else {
                burgerImageView.setImageResource(R.drawable.burgericon); // Use a default image if conversion fails
            }

            // Update prices
            sizeRadioGroup.check(R.id.radioMedium);
            currentBurgerPrice = burgerDetails.getMediumPrice();

            calculateTotalPrice();
        }
    }

    private void calculateTotalPrice() {
        double totalPrice = currentBurgerPrice;

        // Get selected size
        int selectedSizeId = sizeRadioGroup.getCheckedRadioButtonId();
        String selectedBurgerName = burgerTypeSpinner.getSelectedItem().toString();
        BurgerDetails burgerDetails = burgerDetailsMap.get(selectedBurgerName);

        if (burgerDetails != null) {
            if (selectedSizeId == R.id.radioMedium) {
                totalPrice = burgerDetails.getMediumPrice();
            } else if (selectedSizeId == R.id.radioLarge) {
                totalPrice = burgerDetails.getLargePrice();
            }
        }

        // Add topping prices
        if (toppingCheese.isChecked()) totalPrice += cheesePrice;
        if (toppingOlives.isChecked()) totalPrice += olivesPrice;
        if (toppingMushrooms.isChecked()) totalPrice += mushroomsPrice;

        // Update total price text view
        totalPriceTextView.setText(String.format("Total: Rs.%.2f", totalPrice));
    }
}

class BurgerDetails {
    private final String name;
    private final String description;
    private final byte[] photo;
    private final double mediumPrice;
    private final double largePrice;

    public BurgerDetails(String name, String description, byte[] photo, double mediumPrice, double largePrice) {
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.mediumPrice = mediumPrice;
        this.largePrice = largePrice;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getPhotoBitmap() {
        // Convert the byte array to a Bitmap
        return BitmapFactory.decodeByteArray(photo, 0, photo.length);
    }

    public double getMediumPrice() {
        return mediumPrice;
    }

    public double getLargePrice() {
        return largePrice;
    }

    public byte[] getPhoto() {
        return photo;
    }
}
