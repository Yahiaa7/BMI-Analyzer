package com.example.bmianalyzer.Activities;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bmianalyzer.Models.Record;
import com.example.bmianalyzer.Models.User;
import com.example.bmianalyzer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


public class CompleteSignUpActivity extends AppCompatActivity {

    private RadioGroup radioGroup;

    private EditText picker_weight, picker_height, date;
    private int mYear, mMonth, mDay;
    private final Calendar calendar = Calendar.getInstance();

    private ProgressBar progressBar;
    private Button button_complete_signUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_sign_up);

        radioGroup = findViewById(R.id.radioGender);

        picker_weight = findViewById(R.id.picker_weight);


        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        picker_height = findViewById(R.id.picker_height);

        picker_height.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                picker_height.setCursorVisible(true);
                picker_height.setSelection(picker_height.getText().length());
            } else {
                picker_height.setCursorVisible(false);
            }
        });

        date = findViewById(R.id.date);
        date.setCursorVisible(false);
        date.setFocusable(false);

        button_complete_signUp = findViewById(R.id.saveData);
        progressBar = findViewById(R.id.pb_complete_signUp);

    }

    public void SaveData(View view) {
        RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());

        String gender = radioButton.getText().toString();
        String weight = picker_weight.getText().toString();
        String height = picker_height.getText().toString();
        String dateOfBirth = date.getText().toString();

        if (weight.isEmpty() || weight.equals("0") || weight.contains("-")) {
            picker_weight.setError("Please enter a valid value.");
            picker_weight.requestFocus();
            return;
        }
        if (height.isEmpty() || height.equals("0") || height.contains("-")) {
            picker_height.setError("Please enter a valid value.");
            picker_height.requestFocus();
            return;
        }
        if (dateOfBirth.isEmpty()) {
            date.setError("Please fill this field.");
            date.requestFocus();
            return;
        }
        if (calendar.get(Calendar.YEAR) - mYear < 2) {
            date.setError("The Age must be 2 years or older.");
            date.requestFocus();
            return;
        }
        button_complete_signUp.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        User.getUser().setGender(gender);
        User.getUser().setWeight(weight);
        User.getUser().setHeight(height);
        User.getUser().setDateOfBirth(dateOfBirth);

        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR);
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

        String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        Record record = new Record(User.getUser().getWeight(), User.getUser().getHeight(), date, time);
        HashMap<String, Object> record_data = new HashMap<>();
        record_data.put("weight", record.getWeight());
        record_data.put("height", record.getHeight());
        record_data.put("date", record.getDate());
        record_data.put("time", record.getTime());
        record.setStatus(record.CalculateBMI());
        record_data.put("status", record.getStatus());
        record.setTimestamp(new Date().getTime());
        record_data.put("timestamp", record.getTimestamp());
        record_data.put("bmi_value", record.getBmi_value());
        DocumentReference record_reference = FirebaseFirestore.getInstance().collection("users").document(UID).collection("records").document(String.valueOf(record.getTimestamp()));
        record_reference.set(record_data).addOnCompleteListener(CompleteSignUpActivity.this, task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(CompleteSignUpActivity.this, "Error!\n" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                button_complete_signUp.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

        HashMap<String, Object> data = new HashMap<>();
        data.put("gender", User.getUser().getGender());
        data.put("weight", User.getUser().getWeight());
        data.put("height", User.getUser().getHeight());
        data.put("dateOfBirth", User.getUser().getDateOfBirth());

        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(UID);
        reference.update(data).addOnCompleteListener(CompleteSignUpActivity.this, task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(CompleteSignUpActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(CompleteSignUpActivity.this, "Error!\n" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                button_complete_signUp.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void MinusWeight(View view) {
        int value = Integer.parseInt(picker_weight.getText().toString()) - 1;
        if (value >= 50) {
            picker_weight.setText(String.valueOf(value));
            picker_weight.setSelection(picker_weight.getText().length());
        }
    }

    public void AddWeight(View view) {
        int value = Integer.parseInt(picker_weight.getText().toString()) + 1;
        if (value <= 250) {
            picker_weight.setText(String.valueOf(value));
            picker_weight.setSelection(picker_weight.getText().length());
        }
    }

    public void MinusHeight(View view) {
        int value = Integer.parseInt(picker_height.getText().toString()) - 1;
        if (value >= 50) {
            picker_height.setText(String.valueOf(value));
            picker_height.setSelection(picker_height.getText().length());
        }
    }

    public void AddHeight(View view) {
        int value = Integer.parseInt(picker_height.getText().toString()) + 1;
        if (value <= 250) {
            picker_height.setText(String.valueOf(value));
            picker_height.setSelection(picker_height.getText().length());
        }
    }

    public void DatePicker(View view) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(CompleteSignUpActivity.this, (view1, year, month, dayOfMonth) -> {
            view1.setFirstDayOfWeek(Calendar.SATURDAY);
            view1.setMaxDate(new Date().getTime());
            mYear = year;
            mMonth = month + 1;
            mDay = dayOfMonth;
            String mDate = mDay + "/" + mMonth + "/" + mYear;
            date.setText(mDate);
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
}
