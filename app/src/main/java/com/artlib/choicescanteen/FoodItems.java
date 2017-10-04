package com.artlib.choicescanteen;

import java.util.ArrayList;

/**
 * Created by ARTLIB on 03/10/2017.
 */

public class FoodItems {

    private ArrayList<String> foodStuff;
    private double total;

    public FoodItems() {
    }

    public ArrayList<String> getFoodStuff() {
        return foodStuff;
    }

    public void setFoodStuff(ArrayList<String> foodStuff) {
        this.foodStuff = foodStuff;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
