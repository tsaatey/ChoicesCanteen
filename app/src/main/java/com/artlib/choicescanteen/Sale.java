package com.artlib.choicescanteen;

/**
 * Created by ARTLIB on 21/10/2017.
 */

public class Sale {

    private String dateOfSale;
    private String foodItem;
    private String amount;

    public Sale(String dateOfSale, String foodItem, String amount) {
        this.dateOfSale = dateOfSale;
        this.foodItem = foodItem;
        this.amount = amount;
    }

    public String getDateOfSale() {
        return dateOfSale;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public String getAmount() {
        return amount;
    }
}
