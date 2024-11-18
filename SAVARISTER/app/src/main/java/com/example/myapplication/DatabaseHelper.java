package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "userDB.db";
    private static final int DB_VERSION = 3;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "ID";
    private static final String COL_USERNAME = "USERNAME";
    private static final String COL_EMAIL = "EMAIL";
    private static final String COL_PASSWORD = "PASSWORD";
    private static final String COL_MOBILE_NUMBER = "MOBILE_NUMBER";
    private static final String COL_PHOTO = "PHOTO";

    // Admin table
    private static final String TABLE_ADMIN = "admin";
    private static final String COL_ADMIN_ID = "ID";
    private static final String COL_ADMIN_EMAIL = "EMAIL";
    private static final String COL_ADMIN_PASSWORD = "PASSWORD";

    // Constants for the "foods" table
    public static final String TABLE_FOODS = "foods";
    public static final String FOOD_ID_COL = "id";
    public static final String FOOD_NAME_COL = "foodname";
    public static final String FOOD_MPRICE_COL = "mediumprice";
    public static final String FOOD_LPRICE_COL = "largeprice";
    public static final String FOOD_DESCRIPTION_COL = "description";
    public static final String FOOD_PHOTO_COL = "BARGERPHOTO"; // Changed to BLOB


    // Constants for the "pizza" table
    public static final String TABLE_PIZZA = "pizza";
    public static final String PIZZA_ID_COL = "id";
    public static final String PIZZA_NAME_COL = "pizzaname";
    public static final String PIZZA_LARGEPRICE_COL = "largeprice";
    public static final String PIZZA_MEDIUMPRICE_COL = "mediumprice";
    public static final String PIZZA_SMALLPRICE_COL = "smallprice";
    public static final String PIZZA_DESCRIPTION_COL = "description";
    public static final String PIZZA_PHOTO_COL = "PIZZAPHOTO"; // Changed to BLOB



    // Cart table
    private static final String TABLE_CART = "Cart";
    private static final String CART_ID_COL = "id";
    private static final String CART_ITEM_IMAGE_COL = "item_image";
    private static final String CART_ITEM_NAME_COL = "item_name";
    private static final String CART_ITEM_DESCRIPTION_COL = "item_description";
    private static final String CART_ITEM_SIZE_COL = "item_size";
    private static final String CART_ITEM_TOTAL_PRICE_COL = "item_total_price";



    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT DEFAULT NULL, %s TEXT)",
                TABLE_USERS, COL_USER_ID, COL_USERNAME, COL_EMAIL, COL_PASSWORD, COL_MOBILE_NUMBER, COL_PHOTO
        );

        String createAdminTable = String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT)",
                TABLE_ADMIN, COL_ADMIN_ID, COL_ADMIN_EMAIL, COL_ADMIN_PASSWORD
        );

        String createFoodsTable = String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s BLOB, %s TEXT)",
                TABLE_FOODS, FOOD_ID_COL, FOOD_NAME_COL, FOOD_MPRICE_COL, FOOD_LPRICE_COL, FOOD_PHOTO_COL, FOOD_DESCRIPTION_COL
        );

        String createPizzaTable = String.format(
                "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s BLOB, %s TEXT)",
                TABLE_PIZZA, PIZZA_ID_COL, PIZZA_NAME_COL, PIZZA_LARGEPRICE_COL, PIZZA_MEDIUMPRICE_COL, PIZZA_SMALLPRICE_COL, PIZZA_PHOTO_COL, PIZZA_DESCRIPTION_COL
        );

        db.execSQL(createUsersTable);
        db.execSQL(createAdminTable);
        db.execSQL(createFoodsTable);
        db.execSQL(createPizzaTable);

        db.execSQL("CREATE TABLE Cart ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "item_image BLOB, "
                + "item_name TEXT, "
                + "item_description TEXT, "
                + "item_size TEXT, "
                + "item_total_price REAL);");

        Log.d(TAG, "Creating database and tables");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOODS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PIZZA);
            onCreate(db);
        }
        Log.d(TAG, "Creating database and tables");

    }

    // Users table methods
    public boolean insertUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_EMAIL, email);
        contentValues.put(COL_PASSWORD, password);
        contentValues.putNull(COL_MOBILE_NUMBER);  // Set MOBILE_NUMBER as NULL
        contentValues.putNull(COL_PHOTO);          // Set PHOTO as NULL
        long result = db.insert(TABLE_USERS, null, contentValues);
        db.close();
        return result != -1;
    }

    public boolean updateUser(int id, String username, String email, String mobileNumber, byte[] photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (username != null) contentValues.put(COL_USERNAME, username);
        if (email != null) contentValues.put(COL_EMAIL, email);
        if (mobileNumber != null) contentValues.put(COL_MOBILE_NUMBER, mobileNumber);
        if (photo != null) contentValues.put(COL_PHOTO, photo);
        return db.update(TABLE_USERS, contentValues, COL_USER_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ? AND " + COL_PASSWORD + " = ?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkAdmin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ADMIN + " WHERE " + COL_ADMIN_EMAIL + " = ? AND " + COL_ADMIN_PASSWORD + " = ?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        }
        if (cursor != null) cursor.close();
        return -1;
    }

    public Cursor getUserDetails(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    // Admin table methods
    public boolean insertAdmin(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ADMIN_EMAIL, email);
        contentValues.put(COL_ADMIN_PASSWORD, password);
        long result = db.insert(TABLE_ADMIN, null, contentValues);
        db.close();
        return result != -1;
    }

    public boolean deleteAdmin(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_ADMIN, COL_ADMIN_EMAIL + " = ?", new String[]{email});
        db.close();
        return rowsAffected > 0;
    }

    public Cursor getAdminDetails(int adminId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ADMIN + " WHERE " + COL_ADMIN_ID + " = ?", new String[]{String.valueOf(adminId)});
    }

    // Foods table methods
    public void addNewFood(String foodName, String foodMPrice, String foodLPrice, byte[] photo, String foodDescription) {
        if (!isFoodItemExists(foodName)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FOOD_NAME_COL, foodName);
            values.put(FOOD_MPRICE_COL, foodMPrice);
            values.put(FOOD_LPRICE_COL, foodLPrice);
            values.put(FOOD_DESCRIPTION_COL, foodDescription);
            values.put(FOOD_PHOTO_COL, photo);
            db.insert(TABLE_FOODS, null, values);
            db.close();
        }
    }

    private boolean isFoodItemExists(String foodName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FOODS + " WHERE " + FOOD_NAME_COL + "=?", new String[]{foodName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Pizza table methods
    public void addNewPizza(String pizzaName, String mediumPrice, String largePrice, String smallPrice, byte[] photo, String pizzaDescription) {
        if (!isPizzaItemExists(pizzaName)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PIZZA_NAME_COL, pizzaName);
            values.put(PIZZA_MEDIUMPRICE_COL, mediumPrice);
            values.put(PIZZA_LARGEPRICE_COL, largePrice);
            values.put(PIZZA_SMALLPRICE_COL, smallPrice);
            values.put(PIZZA_DESCRIPTION_COL, pizzaDescription);
            values.put(PIZZA_PHOTO_COL, photo);
            db.insert(TABLE_PIZZA, null, values);
            db.close();
        }
    }

    private boolean isPizzaItemExists(String pizzaName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PIZZA + " WHERE " + PIZZA_NAME_COL + "=?", new String[]{pizzaName});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean updateMobileNumber(int id, String mobileNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_MOBILE_NUMBER, mobileNumber);
        try {
            return db.update(TABLE_USERS, contentValues, COL_USER_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating mobile number: " + e.getMessage());
            return false;
        }
    }


    // Insert a new pizza into the database
    public void insertPizza(String name, String description, String photoPath, double smallPrice, double mediumPrice, double largePrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO " + TABLE_PIZZA + " (" +
                        PIZZA_NAME_COL + ", " +
                        PIZZA_DESCRIPTION_COL + ", " +
                        PIZZA_PHOTO_COL + ", " +
                        PIZZA_SMALLPRICE_COL + ", " +
                        PIZZA_MEDIUMPRICE_COL + ", " +
                        PIZZA_LARGEPRICE_COL + ") VALUES (?, ?, ?, ?, ?, ?)",
                new Object[]{name, description, photoPath, smallPrice, mediumPrice, largePrice});
    }


    // Get all pizza details from the database
    public Cursor getAllPizzas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PIZZA, null);
    }
    public Cursor getAllBurgers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_FOODS, null);
    }

    // Get pizza details by name
    public Cursor getPizzaByName(String pizzaName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PIZZA + " WHERE " + PIZZA_NAME_COL + "=?", new String[]{pizzaName});
    }


    public boolean insertIntoCart(byte[] itemImage, String itemName, String itemDescription, String itemSize, double itemTotalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("item_image", itemImage);
        contentValues.put("item_name", itemName);
        contentValues.put("item_description", itemDescription);
        contentValues.put("item_size", itemSize);
        contentValues.put("item_total_price", itemTotalPrice);

        long result = db.insert("Cart", null, contentValues);
        return result != -1; // returns true if insert is successful
    }

    public Cursor getAllCartItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CART, null);
    }

    public boolean removeItemFromCart(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_CART, CART_ID_COL + " = ?", new String[]{String.valueOf(itemId)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_CART, null, null);
        db.close();
        return rowsAffected > 0;
    }


}