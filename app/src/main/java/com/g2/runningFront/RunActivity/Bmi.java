package com.g2.runningFront.RunActivity;

import java.io.Serializable;

public class Bmi implements Serializable {
    private double height, weight;

    public Bmi(double height, double weight) {
        this.height = height;
        this.weight = weight;
    }

    public double getValue() {
        return weight / Math.pow(height / 100, 2);
    }

    @Override
    public String toString() {
        double value = getValue();
        value = (double) Math.round(value * 100) / 100;

        String text = value + "\n";

        if (value >= 30) {
            text += "您的體重已屬肥胖\n請更加注意飲食";
        } else if (value >= 25) {
            text += "您的體重稍重\n請多注意飲食";
        } else if (value >= 18.5) {
            text += "很不錯，很標準\n請您繼續保持";
        } else {
            text += "您的體重太輕了";
        }

        return text;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
