package com.example.myapplication;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;

public class BitmapUtils {

    // Convert Bitmap to byte array
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
}