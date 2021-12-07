package com.example.bmianalyzer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.bmianalyzer.Adapters.HomeScreenRecordsAdapter;
import com.example.bmianalyzer.Models.Record;
import com.example.bmianalyzer.Models.User;
import com.example.bmianalyzer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView user = findViewById(R.id.user);
        user.setText(String.format("Hi, %s!", User.getUser().getName()));

        RecyclerView recyclerView = findViewById(R.id.RecyclerView_Records);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Record> mRecords = new ArrayList<>();

        CollectionReference reference = FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection("records");
//        reference.orderBy("timestamp", Query.Direction.ASCENDING);
        reference.addSnapshotListener(MainActivity.this, (snapshots, error) -> {
            assert snapshots != null;
            System.out.println("before:     " + mRecords.size());
            mRecords.clear();
            if (snapshots.size() != 0) {
//                Toast.makeText(MainActivity.this, "fetching!", Toast.LENGTH_SHORT).show();
                for (QueryDocumentSnapshot snapshot : snapshots) {
                    Record record = snapshot.toObject(Record.class);
                    mRecords.add(record);

                }
                Collections.reverse(mRecords);
                HomeScreenRecordsAdapter recordsAdapter = new HomeScreenRecordsAdapter(mRecords);
                recyclerView.setAdapter(recordsAdapter);
//                mRecords.addAll(snapshots.toObjects(Record.class));
                System.out.println("after:    " + mRecords.size());
            }
        });

    }

    public void AddFood(View view) {
        Intent intent = new Intent(view.getContext(), AddFood.class);
        startActivity(intent);
    }

    public void AddRecord(View view) {
        Intent intent = new Intent(view.getContext(), AddRecord.class);
        startActivity(intent);
    }

    public void ViewFood(View view) {
    }

    public void Logout(View view) {
    }


}