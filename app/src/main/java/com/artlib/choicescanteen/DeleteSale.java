package com.artlib.choicescanteen;

/**
 * Created by ARTLIB on 02/11/2017.
 */

public class DeleteSale {

    private String dateOfSale;
    private String foodItem;
    private String amount;
    private boolean isChecked;

    public DeleteSale(String dateOfSale, String foodItem, String amount) {
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

    public boolean isChecked() {
        return isChecked;
    }
}
