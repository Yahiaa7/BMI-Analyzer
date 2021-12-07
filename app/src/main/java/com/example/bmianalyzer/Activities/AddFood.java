package com.example.bmianalyzer.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bmianalyzer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class AddFood extends AppCompatActivity {

    EditText editText_name, editText_calorie;
    Spinner spinner;
    ImageView imageView, image_preview;

    ActivityResultLauncher<Intent> getPhoto;
    Uri uri_photo;
    TextView textView_drop_down;
    ActivityResultLauncher<Intent> getCameraPhoto;
    RelativeLayout loadingView;
    ProgressBar progressBar;
    TextView progress;
    StorageReference storageReference;

    String mUri = "Default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        editText_name = findViewById(R.id.food_name);
        editText_calorie = findViewById(R.id.calorie);
        spinner = findViewById(R.id.spinner_category);
        textView_drop_down = findViewById(R.id.spinner_dropdown);
        imageView = findViewById(R.id.food_image);
        loadingView = findViewById(R.id.uploadingSession);
        progressBar = findViewById(R.id.PB_Loading);
        progress = findViewById(R.id.progress);

        storageReference = FirebaseStorage.getInstance().getReference("Food Photos");

        textView_drop_down.setOnClickListener(v -> {
            spinner.performClick();
        });


        getPhoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        assert result.getData() != null;
                        uri_photo = result.getData().getData();
                        image_preview.setVisibility(View.VISIBLE);
                        image_preview.setImageURI(result.getData().getData());
                    }
                });

        getCameraPhoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    assert result.getData() != null;
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        image_preview.setVisibility(View.VISIBLE);
//                        Bitmap photo = (Bitmap) result.getData().getExtras().get("data");


                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        image_preview.setImageBitmap(imageBitmap);
                        Toast.makeText(AddFood.this, "Here " + result.getData().getData(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }


    public void UploadPhoto(View view) {
        AlertDialog dialog = new AlertDialog.Builder(view.getContext()).create();
        dialog.setTitle("Upload Photo");
        final View pick_photo_options = getLayoutInflater().inflate(R.layout.pick_image_options, null);
        dialog.setView(pick_photo_options);
        image_preview = pick_photo_options.findViewById(R.id.image_preview);
        Button camera = pick_photo_options.findViewById(R.id.camera);
        camera.setOnClickListener(v -> {
            Intent intent_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent_camera, 0);
//            setResult(1,intent_camera);

            getPhoto.launch(intent_camera);
        });
        Button gallery = pick_photo_options.findViewById(R.id.gallery);
        gallery.setOnClickListener(v -> {
            Intent intent_gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            getPhoto.launch(intent_gallery);
        });
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Upload", (dialog1, which) -> {


            imageView.setImageURI(uri_photo);


        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                (dialog12, which) -> dialog12.dismiss());
        dialog.show();
    }

    public void Save(View view) {
        String name, category, calorie;

        name = editText_name.getText().toString();
        category = spinner.getSelectedItem().toString();
        calorie = editText_calorie.getText().toString();

        if (name.isEmpty()) {
            editText_name.setError("Please fill this field");
            editText_name.requestFocus();
            return;
        }
        if (calorie.isEmpty()) {
            editText_calorie.setError("Please fill this field");
            editText_calorie.requestFocus();
            return;
        }

    }

    private void UploadImage() {


        findViewById(R.id.background_dim).setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        if (uri_photo != null) {
            StorageReference image_reference = storageReference
                    .child(UID)
                    .child(editText_name.getText().toString() + "//" + System.currentTimeMillis() + "." + getFileExtension(uri_photo));

            image_reference.putFile(uri_photo).addOnCompleteListener(AddFood.this,
                    task -> {
                        if (task.isSuccessful()) {
                            image_reference.getDownloadUrl().addOnCompleteListener(AddFood.this, new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri uri=task.getResult();
                                        assert uri != null;
                                        mUri=uri.toString();

                                        loadingView.setVisibility(View.GONE);
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        findViewById(R.id.background_dim).setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(AddFood.this, "Error!\n" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(AddFood.this, "Error!\n" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(AddFood.this, "Please upload an image!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}