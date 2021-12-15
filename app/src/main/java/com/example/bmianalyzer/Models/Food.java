package com.example.bmianalyzer.Models;

import java.io.Serializable;

public class Food implements Serializable {
    private String food_name,food_category,food_calorie,food_image;


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

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public void setFood_category(String food_category) {
        this.food_category = food_category;
    }

    public void setFood_calorie(String food_calorie) {
        this.food_calorie = food_calorie;
    }

    public void setFood_image(String food_image) {
        this.food_image = food_image;
    }
}
