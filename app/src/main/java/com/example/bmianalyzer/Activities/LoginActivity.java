package com.example.bmianalyzer.Activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bmianalyzer.R;


import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editText_username, editText_password;
    private Button button_login;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_username = findViewById(R.id.EditText_username);
        editText_password = findViewById(R.id.EditText_password);

        button_login = findViewById(R.id.button_login);
        progressBar = findViewById(R.id.pb_login);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void Login(View view) {
        String username = Objects.requireNonNull(editText_username.getText()).toString();
        String password = Objects.requireNonNull(editText_password.getText()).toString();

        if (username.isEmpty()) {
            editText_username.setError("Please fill this field!");
            editText_username.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editText_password.setError("Please fill this field!");
            editText_password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        button_login.setVisibility(View.GONE);

        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else if (Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()).startsWith("There is no user")) {
                Toast.makeText(LoginActivity.this, "There is NO Account with this Email.", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                button_login.setVisibility(View.VISIBLE);
            } else if (Objects.requireNonNull(task.getException().getMessage()).startsWith("The password is invalid")) {
                Toast.makeText(LoginActivity.this, "The password is incorrect.", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                button_login.setVisibility(View.VISIBLE);
            } else if (task.getException().getMessage().startsWith("The user account")) {
                Toast.makeText(LoginActivity.this, "This account has been disabled.", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                button_login.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void ToSignUp(View view) {
        Intent toSignUp = new Intent(this, SignUpActivity.class);
        startActivity(toSignUp);
    }
}