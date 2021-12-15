package com.example.bmianalyzer.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.bmianalyzer.Activities.EditFood;
import com.example.bmianalyzer.Models.Food;
import com.example.bmianalyzer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> {

    private final List<Food> mFood;
    private onItemClickList mListener;

    public interface onItemClickList {
        void onItemClick(int position);
    }

    public void setOnItemClickList(onItemClickList mListener) {
        this.mListener = mListener;
    }

    public FoodListAdapter(List<Food> mFood) {
        this.mFood = mFood;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View vew = inflater.inflate(R.layout.food_row, viewGroup, false);
        return new ViewHolder(vew);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = mFood.get(position);

        Glide.with(holder.itemView.getContext()).applyDefaultRequestOptions(new RequestOptions().placeholder(R.mipmap.ic_launcher)).
                load(food.getFood_image()).thumbnail(0.1f).into(holder.imageView);

        holder.textView_food_name.setText(food.getFood_name());
        holder.textView_food_category.setText(food.getFood_category());
        holder.textView_food_calorie.setText(String.format("%s cal/g", food.getFood_calorie()));

        holder.edit_food.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditFood.class);
            intent.putExtra("itemToEdit", food);
            holder.itemView.getContext().startActivity(intent);
        });

        holder.delete_food.setOnClickListener(v -> {
            mFood.remove(position);
            notifyItemRemoved(position);
            DocumentReference reference = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser())
                            .getUid())
                    .collection("Food")
                    .document(food.getFood_name());
            reference.delete();
        });
    }

    @Override
    public int getItemCount() {
        return mFood.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView_food_name, textView_food_category, textView_food_calorie, edit_food, delete_food;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.food_row_image);
            textView_food_name = itemView.findViewById(R.id.food_row_name);
            textView_food_category = itemView.findViewById(R.id.food_row_category);
            textView_food_calorie = itemView.findViewById(R.id.food_row_calorie);
            edit_food = itemView.findViewById(R.id.edit_food_item);
            delete_food = itemView.findViewById(R.id.delete_food_item);

            itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position);
                    }
                }
            });
        }
    }
}
