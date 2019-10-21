package com.g2.runningFront.RunActivity.Run;

import java.io.Serializable;

public class UserBasic implements Serializable {

    int height;
    int weight;
    int age;
    int gender;
    int bmi;
    String bmiSuggest;

    public int getBmi() {
        return bmi;
    }

    public void setBmi(int bmi) {
        this.bmi = bmi;
    }



    public UserBasic(int height, int weight, int age, int gender) {
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.gender = gender;
        bmi = weight / (height * height / 10000);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getBMISuggest(){
        if (bmi > 24) {
            bmiSuggest = "BMI偏重，建議您可以調整飲食";
        } else if (bmi <= 24 & bmi > 18.5) {
            bmiSuggest = "BMI正常，建議您維持運動習慣";
        } else if (bmi <= 18.5) {
            bmiSuggest = "BMI偏輕，建議您增加訓練";
        }
        return bmiSuggest;
    }


}
