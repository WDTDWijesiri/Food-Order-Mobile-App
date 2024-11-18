package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private ArrayList<CartItem> cartItems;
    private DatabaseHelper dbHelper;

    private EditText editTextPromotionCode;
    private TextView textViewSubtotalValue;
    private TextView textViewPromotionValue;
    private TextView textViewTotalValue;
    private double subtotal;
    private double total;
    private double promotionDiscount = 0; // Default value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view_cart);
        editTextPromotionCode = findViewById(R.id.editTextText9);
        textViewSubtotalValue = findViewById(R.id.textViewSubtotalValue);
        textViewPromotionValue = findViewById(R.id.textViewPromotionValue);
        textViewTotalValue = findViewById(R.id.textViewTotalValue);
        Button buttonCheckout = findViewById(R.id.buttonCheckout);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        cartItems = new ArrayList<>();

        loadCartItems();

        adapter = new CartAdapter(this, cartItems);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CartAdapter.OnItemClickListener() {
            @Override
            public void onRemoveClick(int position) {
                CartItem cartItem = cartItems.get(position);
                dbHelper.removeItemFromCart(cartItem.getId());
                cartItems.remove(position);
                adapter.notifyItemRemoved(position);
                calculateSubtotalAndTotal();
            }
        });

        // Add a TextWatcher to the promotion code EditText
        editTextPromotionCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyPromotionCode(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set up the button to send an email
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailWithCartDetails();
            }
        });
    }

    private void loadCartItems() {
        Cursor cursor = dbHelper.getAllCartItems();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                byte[] itemImage = cursor.getBlob(1);
                String itemName = cursor.getString(2);
                String itemDescription = cursor.getString(3);
                String itemSize = cursor.getString(4);
                double itemTotalPrice = cursor.getDouble(5);

                cartItems.add(new CartItem(id, itemImage, itemName, itemDescription, itemSize, itemTotalPrice));
            } while (cursor.moveToNext());
        }
        cursor.close();

        calculateSubtotalAndTotal();
    }

    private void calculateSubtotalAndTotal() {
        subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getItemTotalPrice();
        }
        textViewSubtotalValue.setText(String.format("Rs.%.2f", subtotal));
        calculateTotal();
    }

    private void calculateTotal() {
        total = subtotal - promotionDiscount;
        textViewTotalValue.setText(String.format("Rs.%.2f", total));
    }

    private void applyPromotionCode(String code) {
        // Example of promotion logic
        if (code.equals("FB10")) {
            promotionDiscount = subtotal * 0.10; // 10% discount
        } else if (code.equals("FB20")) {
            promotionDiscount = subtotal * 0.20; // 20% discount
        } else if (code.equals("FB30")) {
            promotionDiscount = subtotal * 0.30; // 30% discount
        } else {
            promotionDiscount = 0; // No discount
        }
        textViewPromotionValue.setText(String.format("Rs.%.2f", promotionDiscount));
        calculateTotal();
    }

    private void sendEmailWithCartDetails() {
        String promotionCode = editTextPromotionCode.getText().toString().trim();
        String subtotalText = textViewSubtotalValue.getText().toString().trim();
        String promotionValueText = textViewPromotionValue.getText().toString().trim();
        String totalValueText = textViewTotalValue.getText().toString().trim();

        String emailSubject = "Order Details from Your Cart";
        String emailBody = "Promotion Code: " + promotionCode + "\n"
                + "Subtotal: " + subtotalText + "\n"
                + "Promotion Discount: " + promotionValueText + "\n"
                + "Total: " + totalValueText;

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);

        try {
            startActivity(Intent.createChooser(emailIntent, "Choose an email client"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void backtologin(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}