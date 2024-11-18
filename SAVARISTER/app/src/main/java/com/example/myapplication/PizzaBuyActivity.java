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

public class PizzaBuyActivity extends AppCompatActivity {

    private Spinner pizzaTypeSpinner;
    private ImageView pizzaImageView;
    private TextView pizzaDescriptionTextView;
    private RadioGroup sizeRadioGroup;
    private CheckBox toppingCheese, toppingOlives, toppingMushrooms, toppingPeppers;
    private TextView totalPriceTextView;
    private Button addToCartButton;

    private DatabaseHelper dbHelper;

    private HashMap<String, PizzaDetails> pizzaDetailsMap = new HashMap<>();

    private double currentPizzaPrice;

    // Hardcoded topping prices
    private final double cheesePrice = 500;
    private final double olivesPrice = 700;
    private final double mushroomsPrice = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_buy);

        // Initialize UI components
        pizzaTypeSpinner = findViewById(R.id.pizzaTypeSpinner);
        pizzaImageView = findViewById(R.id.pizzaImageView);
        pizzaDescriptionTextView = findViewById(R.id.pizzaDescriptionTextView);
        sizeRadioGroup = findViewById(R.id.sizeRadioGroup);
        toppingCheese = findViewById(R.id.toppingCheese);
        toppingOlives = findViewById(R.id.toppingOlives);
        toppingMushrooms = findViewById(R.id.toppingMushrooms);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        addToCartButton = findViewById(R.id.addToCartButton);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Load pizza details from the database
        loadPizzaDetailsFromDatabase();

        // Set up spinner listener
        pizzaTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPizza = parent.getItemAtPosition(position).toString();
                updatePizzaDetails(selectedPizza);
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
            String selectedPizzaName = pizzaTypeSpinner.getSelectedItem().toString();
            PizzaDetails pizzaDetails = pizzaDetailsMap.get(selectedPizzaName);

            if (pizzaDetails != null) {
                // Get current selected size
                int selectedSizeId = sizeRadioGroup.getCheckedRadioButtonId();
                String itemSize = "";
                if (selectedSizeId == R.id.radioSmall) {
                    itemSize = "Small";
                } else if (selectedSizeId == R.id.radioMedium) {
                    itemSize = "Medium";
                } else if (selectedSizeId == R.id.radioLarge) {
                    itemSize = "Large";
                }

                // Get current total price
                double itemTotalPrice = currentPizzaPrice;
                if (toppingCheese.isChecked()) itemTotalPrice += cheesePrice;
                if (toppingOlives.isChecked()) itemTotalPrice += olivesPrice;
                if (toppingMushrooms.isChecked()) itemTotalPrice += mushroomsPrice;

                // Insert the item into the cart
                boolean isInserted = dbHelper.insertIntoCart(
                        pizzaDetails.getPhoto(),
                        selectedPizzaName,
                        pizzaDetails.getDescription(),
                        itemSize,
                        itemTotalPrice
                );

                if (isInserted) {
                    Toast.makeText(PizzaBuyActivity.this, "Pizza added to cart!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PizzaBuyActivity.this, "Failed to add pizza to cart.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadPizzaDetailsFromDatabase() {
        Cursor cursor = dbHelper.getAllPizzas();
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PIZZA_NAME_COL));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PIZZA_DESCRIPTION_COL));
                byte[] photoBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.PIZZA_PHOTO_COL));
                double smallPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PIZZA_SMALLPRICE_COL));
                double mediumPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PIZZA_MEDIUMPRICE_COL));
                double largePrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.PIZZA_LARGEPRICE_COL));

                PizzaDetails pizzaDetails = new PizzaDetails(name, description, photoBytes, smallPrice, mediumPrice, largePrice);
                pizzaDetailsMap.put(name, pizzaDetails);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Populate spinner with pizza names
        ArrayList<String> pizzaNames = new ArrayList<>(pizzaDetailsMap.keySet());
        pizzaTypeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pizzaNames));
    }

    private void updatePizzaDetails(String pizzaName) {
        PizzaDetails pizzaDetails = pizzaDetailsMap.get(pizzaName);

        if (pizzaDetails != null) {
            // Update description
            pizzaDescriptionTextView.setText(pizzaDetails.getDescription());

            // Update image using the getPhotoBitmap() method
            Bitmap pizzaImageBitmap = pizzaDetails.getPhotoBitmap();
            if (pizzaImageBitmap != null) {
                pizzaImageView.setImageBitmap(pizzaImageBitmap);
            } else {
                pizzaImageView.setImageResource(R.drawable.pizza); // Use a default image if conversion fails
            }

            // Update prices
            sizeRadioGroup.check(R.id.radioSmall);
            currentPizzaPrice = pizzaDetails.getSmallPrice();

            calculateTotalPrice();
        }
    }

    private void calculateTotalPrice() {
        double totalPrice = currentPizzaPrice;

        // Get selected size
        int selectedSizeId = sizeRadioGroup.getCheckedRadioButtonId();
        String selectedPizzaName = pizzaTypeSpinner.getSelectedItem().toString();
        PizzaDetails pizzaDetails = pizzaDetailsMap.get(selectedPizzaName);

        if (pizzaDetails != null) {
            if (selectedSizeId == R.id.radioMedium) {
                totalPrice = pizzaDetails.getMediumPrice();
            } else if (selectedSizeId == R.id.radioLarge) {
                totalPrice = pizzaDetails.getLargePrice();
            } else if (selectedSizeId == R.id.radioSmall) {
                totalPrice = pizzaDetails.getSmallPrice();
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

class PizzaDetails {
    private final String name;
    private final String description;
    private final byte[] photo;
    private final double smallPrice;
    private final double mediumPrice;
    private final double largePrice;

    public PizzaDetails(String name, String description, byte[] photo, double smallPrice, double mediumPrice, double largePrice) {
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.smallPrice = smallPrice;
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

    public double getSmallPrice() {
        return smallPrice;
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
