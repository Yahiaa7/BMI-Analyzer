package com.example.bmianalyzer.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bmianalyzer.Models.Food;
import com.example.bmianalyzer.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class EditFood extends AppCompatActivity {

    Food food;

    EditText editText_name, editText_calorie;
    Spinner spinner;
    ImageView imageView, image_preview;
    TextView textView_drop_down;

    TableRow tableRow;
    ProgressBar foodProgressBar;

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


        tableRow = findViewById(R.id.edit_food_buttons);
        foodProgressBar = findViewById(R.id.edit_food_progress);

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


    }

    public void UploadPhoto(View view) {
    }

    public void Save(View view) {
    }
}