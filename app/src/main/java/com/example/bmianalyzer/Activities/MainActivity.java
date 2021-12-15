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

        TextView status = findViewById(R.id.bmi_Status_Change);

        RecyclerView recyclerView = findViewById(R.id.RecyclerView_Records);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Record> mRecords = new ArrayList<>();

        CollectionReference reference = FirebaseFirestore.getInstance()
                .collection("users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance()
                        .getCurrentUser()).getUid()).collection("records");
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
                if (mRecords.size() >= 2) {
                    double difference = mRecords.get(0).getBmi_value() - mRecords.get(1).getBmi_value();
                    String change = "";
                    switch (getChange(difference, mRecords.get(0).getStatus())) {
                        case "LC":
                            change = "Little Changes";
                            break;
                        case "SG":
                            change = "Still Good";
                            break;
                        case "GA":
                            change = "Go Ahead";
                            break;
                        case "BC":
                            change = "Be Careful";
                            break;
                        case "SB":
                            change = "So Bad";
                            break;
                        default:
                            System.out.println("neverMind");
                    }
                    status.setText(String.format("%s (%s)", mRecords.get(0).getStatus(), change));
                } else {
                    status.setText(String.format("%s (No Changes Yet!)", mRecords.get(0).getStatus()));
                }
                HomeScreenRecordsAdapter recordsAdapter = new HomeScreenRecordsAdapter(mRecords);
                recyclerView.setAdapter(recordsAdapter);
            }
        });

    }

    private String getChange(double difference, String status) {
        double[] ranges = {-1000, -1, -0.6, -0.3, 0, 0.3, 0.6, 1, 1000};
        String[] mStatus = {"Underweight", "Healthy Weight", "Overweight", "Obesity"};
        String[][] sc = {{"SB", "SB", "SB", "LC", "LC", "SG", "GA", "GA"},
                {"SB", "BC", "BC", "LC", "LC", "BC", "BC", "BC"},
                {"BC", "GA", "SG", "LC", "LC", "BC", "SB", "SB"},
                {"GA", "GA", "LC", "LC", "BC", "SB", "SB", "SB"}};

        for (int i = 0; i < mStatus.length; i++) {
            if (status.trim().equalsIgnoreCase(mStatus[i].trim())) {
                for (int j = 0; j < ranges.length; j++) {
                    if (IsItIn(ranges[j], ranges[j + 1], difference)) {
                        return sc[i][j];
                    }
                }
            }
        }
        return "";
    }

    private boolean IsItIn(double in, double ex, double value) {
        return value >= in && value < ex;
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
        Intent intent = new Intent(view.getContext(), FoodList.class);
        startActivity(intent);
    }

    public void Logout(View view) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        Intent intent = new Intent(view.getContext(), SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}