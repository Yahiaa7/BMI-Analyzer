package com.example.bmianalyzer.Activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.bmianalyzer.Models.Record;
import com.example.bmianalyzer.R;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class AddRecord extends AppCompatActivity {

    private EditText picker_weight, picker_height, date, time;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);


        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        picker_weight = findViewById(R.id.pickerWeight);
        picker_height = findViewById(R.id.pickerHeight);


        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);


        picker_height.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                picker_height.setCursorVisible(true);
                picker_height.setSelection(picker_height.getText().length());
            } else {
                picker_height.setCursorVisible(false);
            }
        });


        date = findViewById(R.id.Date);
        date.setCursorVisible(false);
        date.setFocusable(false);

        time = findViewById(R.id.Time);
        time.setCursorVisible(false);
        time.setFocusable(false);


    }


    public void SaveData(View view) {
        String weight = picker_weight.getText().toString();
        String height = picker_height.getText().toString();
        String mDate = date.getText().toString();
        String mTime = time.getText().toString();


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
        if (mDate.isEmpty()) {
            date.setError("Please fill this field.");
            date.requestFocus();
            return;
        }
        if (mTime.isEmpty()) {
            time.setError("Please fill this field.");
            time.requestFocus();
            return;
        }

        Record record = new Record(weight, height, mDate, mTime);
        record.setStatus(record.CalculateBMI());
        record.setTimestamp(new Date().getTime());

        HashMap<String, Object> data = new HashMap<>();
        data.put("weight", record.getWeight());
        data.put("height", record.getHeight());
        data.put("status", record.getStatus());
        data.put("date", record.getDate());
        data.put("time", record.getTime());
        data.put("timestamp", record.getTimestamp());

        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection("records").document(String.valueOf(record.getTimestamp()));
        reference.set(data).addOnCompleteListener(AddRecord.this, task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(AddRecord.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(AddRecord.this, "Error!\n" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Minus_Weight(View view) {
        int value = Integer.parseInt(picker_weight.getText().toString()) - 1;
        if (value >= 50) {
            picker_weight.setText(String.valueOf(value));
            picker_weight.setSelection(picker_weight.getText().length());
        }
    }

    public void Add_Weight(View view) {
        int value = Integer.parseInt(picker_weight.getText().toString()) + 1;
        if (value <= 250) {
            picker_weight.setText(String.valueOf(value));
            picker_weight.setSelection(picker_weight.getText().length());
        }
    }

    public void Minus_Height(View view) {
        int value = Integer.parseInt(picker_height.getText().toString()) - 1;
        if (value >= 50) {
            picker_height.setText(String.valueOf(value));
            picker_height.setSelection(picker_height.getText().length());
        }
    }

    public void Add_Height(View view) {
        int value = Integer.parseInt(picker_height.getText().toString()) + 1;
        if (value <= 250) {
            picker_height.setText(String.valueOf(value));
            picker_height.setSelection(picker_height.getText().length());
        }
    }

    public void Date_Picker(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddRecord.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                view.setFirstDayOfWeek(Calendar.SATURDAY);
                view.setMaxDate(new Date().getTime());
                mYear = year;
                mMonth = month + 1;
                mDay = dayOfMonth;
                String mDate = mDay + "/" + mMonth + "/" + mYear;
                date.setText(mDate);

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void Time_Picker(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddRecord.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour = hourOfDay;
                mMinute = minute;
                String mTime = mHour + ":" + mMinute;
                time.setText(mTime);
            }
        }, mHour, mMinute, true);
        timePickerDialog.show();
    }
}