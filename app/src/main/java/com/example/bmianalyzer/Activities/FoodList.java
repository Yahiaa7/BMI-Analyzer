package com.example.bmianalyzer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.bmianalyzer.Adapters.FoodListAdapter;
import com.example.bmianalyzer.Models.Food;
import com.example.bmianalyzer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.Objects;

public class FoodList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        RecyclerView recyclerView = findViewById(R.id.RecyclerView_FoodList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Food> mFood = new ArrayList<>();

        CollectionReference reference = FirebaseFirestore.getInstance()
                .collection("users").document(Objects
                        .requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .collection("Food");
        reference.addSnapshotListener(FoodList.this, (snapshots, error) -> {
            mFood.clear();
            assert snapshots != null;
            if (snapshots.size() != 0) {
                for (QueryDocumentSnapshot snapshot : snapshots) {
                    Food food = snapshot.toObject(Food.class);
                    System.out.println(food.getFood_timestamp());
                    mFood.add(food);
                }
//                Collections.reverse(mFood);
                FoodListAdapter adapter = new FoodListAdapter(mFood);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}