package com.artlib.choicescanteen;

/**
 * Created by ARTLIB on 30/09/2017.
 */

public class DashboardSales {

    private String foodItem;
    private String amount;

    public DashboardSales(String foodItem, String amount) {
        this.foodItem = foodItem;
        this.amount = amount;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public String getAmount() {
        return amount;
    }
}
