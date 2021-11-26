package com.example.bmianalyzer.Models;

import java.util.Map;

public class Record {
    private String weight, height, status, date, time;
    private long timestamp;

    public Record(String weight, String height, String date, String time) {

        this.weight = weight;
        this.height = height;
        this.date = date;
        this.time = time;
    }

    public Record() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String CalculateBMI() {
        int weight = Integer.parseInt(this.getWeight());
        System.out.println("weight = " + weight);
        double height = Integer.parseInt(this.getHeight());
        height = height / 100;
        System.out.println("height = " + height);
        int agePercentage=User.getUser().getAgePercentage()/100;
        System.out.println("agePercentage = " + agePercentage);
        double bmi = (weight / Math.pow(height, 2)) * agePercentage;

        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi >= 18.5 && bmi < 25) {
            return "Healthy Weight";
        } else if (bmi >= 25 && bmi < 30) {
            return "Overweight";
        } else {
            System.out.println("bmi = " + bmi);
            return "Obesity";
        }
    }
}
