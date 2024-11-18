package com.example.myapplication.Ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.EmailEditActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.NumberEditActivity;
import com.example.myapplication.R;
import com.example.myapplication.BitmapUtils;
import com.example.myapplication.UsernameEditActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private static final int CAPTURE_IMAGE = 2;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private ImageView imageViewProfilePhoto;
    private DatabaseHelper dbHelper;
    private int userId;
    private Uri cameraImageUri;
    private TextView textEmail, textName, textNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button button2 = view.findViewById(R.id.button2);
        Button button3 = view.findViewById(R.id.button3);
        Button button4 = view.findViewById(R.id.button4);
        Button button6 = view.findViewById(R.id.button6);
        imageViewProfilePhoto = view.findViewById(R.id.imageView4);

        dbHelper = new DatabaseHelper(getContext());

        // Get user ID from shared preferences or arguments
        userId = MainActivity.currentUserId;

        // Load existing photo
        loadProfilePhoto();

        // Retrieve user details
        textEmail = view.findViewById(R.id.textView12);
        textName = view.findViewById(R.id.textView11);
        textNumber = view.findViewById(R.id.textView13);
        loadUserDetails();

        // Set button click listeners
        button2.setOnClickListener(v -> startActivity(new Intent(getActivity(), UsernameEditActivity.class)));
        button3.setOnClickListener(v -> startActivity(new Intent(getActivity(), EmailEditActivity.class)));
        button4.setOnClickListener(v -> startActivity(new Intent(getActivity(), NumberEditActivity.class)));
        button6.setOnClickListener(v -> showPhotoOptions());

        return view;
    }

    private void loadProfilePhoto() {
        Cursor cursor = dbHelper.getUserDetails(userId);
        if (cursor != null && cursor.moveToFirst()) {
            byte[] photoBlob = cursor.getBlob(cursor.getColumnIndex("PHOTO"));
            if (photoBlob != null && photoBlob.length > 0) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(photoBlob, 0, photoBlob.length);
                    imageViewProfilePhoto.setImageBitmap(bitmap != null ? bitmap : BitmapFactory.decodeResource(getResources(), R.drawable.user));
                } catch (Exception e) {
                    Log.e("ProfileFragment", "Error loading photo", e);
                    imageViewProfilePhoto.setImageResource(R.drawable.user); // Set a default photo
                }
            } else {
                imageViewProfilePhoto.setImageResource(R.drawable.user); // Set a default photo
            }
            cursor.close();
        } else {
            imageViewProfilePhoto.setImageResource(R.drawable.user); // Set a default photo
        }
    }

    private void loadUserDetails() {
        Cursor cursor = dbHelper.getUserDetails(userId);
        if (cursor != null && cursor.moveToFirst()) {
            textEmail.setText(cursor.getString(cursor.getColumnIndex("EMAIL")));
            textName.setText(cursor.getString(cursor.getColumnIndex("USERNAME")));
            textNumber.setText(cursor.getString(cursor.getColumnIndex("MOBILE_NUMBER")));
            cursor.close();
        }
    }

    private void showPhotoOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select or Capture Photo");
        builder.setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (checkAndRequestPermissions()) {
                            openCamera();
                        }
                        break;
                    case 1:
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, PICK_IMAGE);
                        break;
                }
            }
        });
        builder.show();
    }

    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("ProfileFragment", "Error creating image file", ex);
            }
            if (photoFile != null) {
                cameraImageUri = FileProvider.getUriForFile(getContext(), "com.example.myapplication.fileprovider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE);
            }
        } else {
            Toast.makeText(getActivity(), "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = null;

            if (requestCode == PICK_IMAGE && data != null) {
                Uri imageUri = data.getData();
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                } catch (Exception e) {
                    Log.e("ProfileFragment", "Error selecting photo", e);
                    Toast.makeText(getActivity(), "Error selecting photo", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CAPTURE_IMAGE) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), cameraImageUri);
                } catch (IOException e) {
                    Log.e("ProfileFragment", "Error capturing photo", e);
                    Toast.makeText(getActivity(), "Error capturing photo", Toast.LENGTH_SHORT).show();
                }
            }

            if (bitmap != null) {
                imageViewProfilePhoto.setImageBitmap(bitmap);

                byte[] photoBytes = BitmapUtils.getBytesFromBitmap(bitmap);
                boolean updateSuccessful = dbHelper.updateUser(userId, null, null, null, photoBytes);

                if (updateSuccessful) {
                    Toast.makeText(getActivity(), "Photo updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getActivity(), "Camera permission is required to use this feature", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
