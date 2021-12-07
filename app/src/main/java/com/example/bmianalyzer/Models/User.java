package com.example.bmianalyzer.Models;

import android.widget.Toast;

import com.example.bmianalyzer.Activities.AddRecord;
import com.example.bmianalyzer.Activities.MainActivity;

import java.util.Calendar;

public class User {
    private String name, email, password, gender, weight, height, dateOfBirth;

    private static final User user = new User();

    public static User getUser() {
        return user;
    }

    public User() {
    }

    public User(String name, String email, String gender, String weight, String height, String dateOfBirth) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.weight = weight;
        this.height = height;
        this.dateOfBirth = dateOfBirth;
    }
//
//    public User(String name, String email, String password) {
//        this.name = name;
//        this.email = email;
//        this.password = password;
//    }
//
//    public User(String gender, String weight, String height, String dateOfBirth) {
//        this.gender = gender;
//        this.weight = weight;
//        this.height = height;
//        this.dateOfBirth = dateOfBirth;
//    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getAgePercentage() {
        String[] d = User.getUser().getDateOfBirth().split("/");
        String mYear = d[2];
        int age = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(mYear);

        if (age >= 2 && age < 10) {
            return 70;
        } else if (age >= 10 && age <= 20 && User.getUser().getGender().equals("Male")) {
            return 90;

        } else if (age >= 10 && age <= 20 && User.getUser().getGender().equals("Female")) {
            return 80;

        } else if (age > 20) {
            return 100;
        } else {
            return 0;
        }
    }
}
