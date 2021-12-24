package com.example.bmianalyzer.Models;

import java.io.Serializable;

public class Food implements Serializable {
    private String food_name, food_category, food_calorie, food_image;
    private long food_timestamp;


    public Food() {
    }

    public Food(String food_name, String food_category, String food_calorie, String food_image) {

        this.food_name = food_name;
        this.food_category = food_category;
        this.food_calorie = food_calorie;
        this.food_image = food_image;
    }

    public String getFood_name() {
        return food_name;
    }

    public String getFood_category() {
        return food_category;
    }

    public String getFood_calorie() {
        return food_calorie;
    }

    public String getFood_image() {
        return food_image;
    }

    public long getFood_timestamp() {
        return food_timestamp;
    }

    public void setFood_timestamp(long food_timestamp) {
        this.food_timestamp = food_timestamp;
    }
}
