package com.example.bmianalyzer.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bmianalyzer.Models.User;
import com.example.bmianalyzer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText editText_name, editText_email, editText_password, editText_re_password;
    private String name;
    private String email;
    private Button button_signUp;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private DocumentReference reference;
    String UID;

    private static final Pattern password_pattern =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    // "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editText_name = findViewById(R.id.editText_name);
        editText_email = findViewById(R.id.editText_email);
        editText_password = findViewById(R.id.editText_password);
        editText_re_password = findViewById(R.id.editText_re_password);

        button_signUp = findViewById(R.id.button_SignUp);
        progressBar = findViewById(R.id.pb_signUp);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void SignUp(View view) {
        name = Objects.requireNonNull(editText_name.getText()).toString();
        email = Objects.requireNonNull(editText_email.getText()).toString();
        String password = Objects.requireNonNull(editText_password.getText()).toString();
        String re_password = Objects.requireNonNull(editText_re_password.getText()).toString();

        if (name.isEmpty()) {
            editText_name.setError("Please Fill this Field!");
            editText_name.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editText_email.setError("Please Fill this Field!");
            editText_email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editText_email.setError("Please Enter a valid Email!");
            editText_email.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editText_password.setError("Please Fill this Field!");
            editText_password.requestFocus();
            return;
        }
        if (!password_pattern.matcher(password).matches()) {
            editText_password.setError("Please Enter a Stronger Password!(at least 6 letters and digits)");
            editText_password.requestFocus();
            return;
        }
        if (re_password.isEmpty()) {
            editText_re_password.setError("Please Fill this Field!");
            editText_re_password.requestFocus();
            return;
        }
        if (!password.equals(re_password)) {
            editText_re_password.setError("Re Password Doesn't match Password!");
            editText_re_password.requestFocus();
            return;
        }

        button_signUp.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

//        Toast.makeText(this, "Name: " + name + "\nEmail: " + email + "\nPassword: " + password, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Signed Up Successfully", Toast.LENGTH_SHORT).show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    UID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

                    User.getUser().setName(name);
                    User.getUser().setEmail(email);

                    HashMap<String, String> data = new HashMap<>();
                    data.put("name", User.getUser().getName());
                    data.put("email", User.getUser().getEmail());
                    data.put("gender", "gender");
                    data.put("weight", "weight");
                    data.put("height", "height");
                    data.put("dateOfBirth", "dateOfBirth");

                    reference = FirebaseFirestore.getInstance().collection("users").document(UID);
                    reference.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(SignUpActivity.this, CompleteSignUpActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                button_signUp.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignUpActivity.this, "Error!\n" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    button_signUp.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "Error!\n" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    public void ToLogin(View view) {
        Intent toLogin = new Intent(this, LoginActivity.class);
        startActivity(toLogin);
    }
}