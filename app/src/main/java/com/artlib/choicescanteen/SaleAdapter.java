package com.artlib.choicescanteen;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ARTLIB on 21/10/2017.
 */

public class SaleAdapter extends ArrayAdapter<Sale> {

    public SaleAdapter(Activity context, int resource, ArrayList<Sale> sales) {
        super(context, resource, 0, sales);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_items, parent, false);
        }

        Sale currentSale = getItem(position);

        TextView dateTextView = (TextView)listItemView.findViewById(R.id.date_text_view);
        dateTextView.setText(currentSale.getDateOfSale());

        TextView foodItemTextView = (TextView)listItemView.findViewById(R.id.food_text_view);
        foodItemTextView.setText(currentSale.getFoodItem());

        TextView amountTextView = (TextView)listItemView.findViewById(R.id.amount_text_view);
        amountTextView.setText(currentSale.getAmount());

        return listItemView;
    }
}
