package com.example.bmianalyzer.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bmianalyzer.Models.User;
import com.example.bmianalyzer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        ProgressBar progressBar = findViewById(R.id.splash_pb);
        TextView textView_Next = findViewById(R.id.splash_next);

        textView_Next.setEnabled(true);
        textView_Next.setVisibility(View.VISIBLE);

//        new Handler().postDelayed(() -> {
//            progressBar.setVisibility(View.INVISIBLE);
//            textView_Next.setEnabled(true);
//            textView_Next.setVisibility(View.VISIBLE);
//        }, 1500);

        textView_Next.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            textView_Next.setEnabled(false);
            textView_Next.setVisibility(View.GONE);
            if (firebaseUser != null) {
                DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());
                reference.get().addOnCompleteListener(SplashScreen.this, task -> {
                    if (task.isSuccessful()) {
                        User mUser = Objects.requireNonNull(task.getResult()).toObject(User.class);
                        assert mUser != null;
                        User.getUser().setName(mUser.getName());
                        User.getUser().setEmail(mUser.getEmail());
                        User.getUser().setGender(mUser.getGender());
                        User.getUser().setWeight(mUser.getWeight());
                        User.getUser().setHeight(mUser.getHeight());
                        User.getUser().setDateOfBirth(mUser.getDateOfBirth());
                        System.out.println("splassssssssssssssssssssh user date of birth: " + mUser.getDateOfBirth());
                        Intent intent = new Intent(v.getContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
//                reference.addSnapshotListener(SplashScreen.this, (value, error) -> {
//                    assert value != null;
//                    if (value.exists()) {
//                        User mUser = value.toObject(User.class);
//                        assert mUser != null;
//                        User.getUser().setName(mUser.getName());
//                        User.getUser().setEmail(mUser.getEmail());
//                        User.getUser().setGender(mUser.getGender());
//                        User.getUser().setWeight(mUser.getWeight());
//                        User.getUser().setHeight(mUser.getHeight());
//                        User.getUser().setDateOfBirth(mUser.getDateOfBirth());
//                        System.out.println("splashsssssssssssssssssssssssssss user date of birth: " + mUser.getDateOfBirth());
//                    }
//                });

            } else {
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

        });

    }
}