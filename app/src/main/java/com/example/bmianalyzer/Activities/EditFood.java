package com.example.bmianalyzer.Activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bmianalyzer.Models.Food;
import com.example.bmianalyzer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;

public class EditFood extends AppCompatActivity {

    Food food;

    EditText editText_name, editText_calorie;
    Spinner spinner;
    ImageView imageView, image_preview;
    TextView textView_drop_down;

    TableRow tableRow;
    ProgressBar foodProgressBar;

    StorageReference storageReference;

    String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    String mUri = "Default";

    ActivityResultLauncher<Intent> getPhoto;
    Uri uri_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

        if (getIntent() != null) {
            food = (Food) getIntent().getSerializableExtra("itemToEdit");
        }
        Toast.makeText(EditFood.this, food.getFood_name(), Toast.LENGTH_SHORT).show();


        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        editText_name = findViewById(R.id.edit_food_name);
        editText_calorie = findViewById(R.id.edit_calorie);
        spinner = findViewById(R.id.edit_spinner_category);
        textView_drop_down = findViewById(R.id.edit_spinner_dropdown);
        imageView = findViewById(R.id.edit_food_image);

        editText_name.setText(food.getFood_name());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_entries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setSelection(adapter.getPosition(food.getFood_category()));

        editText_calorie.setText(food.getFood_calorie());

        Glide.with(getApplicationContext())
                .applyDefaultRequestOptions(new RequestOptions().placeholder(R.mipmap.ic_launcher))
                .load(food.getFood_image()).into(imageView);


        storageReference = FirebaseStorage.getInstance().getReference("Food Photos");

        tableRow = findViewById(R.id.edit_food_buttons);
        foodProgressBar = findViewById(R.id.edit_food_progress);

        textView_drop_down.setOnClickListener(v -> spinner.performClick());

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


    }


    public void Save(View view) {
        tableRow.setVisibility(View.GONE);
        foodProgressBar.setVisibility(View.VISIBLE);

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

        Food food1 = new Food(name, category, calorie, mUri);

        HashMap<String, Object> data = new HashMap<>();
        data.put("food_name", food1.getFood_name());
        data.put("food_category", food1.getFood_category());
        data.put("food_calorie", food1.getFood_calorie());
        data.put("food_timestamp", food1.getFood_timestamp());

        UploadImage(data);

    }

    private void UploadData(HashMap<String, Object> data) {
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection("users")
                .document(UID)
                .collection("Food")
                .document(String.valueOf(food.getFood_timestamp()));
        documentReference.update(data).addOnCompleteListener(EditFood.this, task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(EditFood.this, FoodList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(EditFood.this, "Error!\n" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                tableRow.setVisibility(View.VISIBLE);
                foodProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void UploadImage(HashMap<String, Object> data) {
        if (uri_photo != null) {
            StorageReference image_reference = storageReference
                    .child(UID)
                    .child(editText_name.getText().toString() +
                            "." + getFileExtension(uri_photo));

            image_reference.putFile(uri_photo).addOnCompleteListener(EditFood.this,
                    task -> {
                        if (task.isSuccessful()) {
                            image_reference.getDownloadUrl()
                                    .addOnCompleteListener(EditFood.this, task1 -> {
                                        if (task1.isSuccessful()) {
                                            Uri uri = task1.getResult();
                                            assert uri != null;
                                            mUri = uri.toString();

                                            data.put("food_image", mUri);
                                            UploadData(data);


                                        } else {

                                            Toast.makeText(EditFood.this, "Error!\n" +
                                                            Objects.requireNonNull(task1.getException()).getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(EditFood.this, "Error!\n" +
                                            Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            UploadData(data);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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

            getPhoto.launch(intent_camera);
        });
        Button gallery = pick_photo_options.findViewById(R.id.gallery);
        gallery.setOnClickListener(v -> {
            Intent intent_gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            getPhoto.launch(intent_gallery);
        });
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Upload", (dialog1, which) -> imageView.setImageURI(uri_photo));
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                (dialog12, which) -> dialog12.dismiss());
        dialog.show();
    }
}