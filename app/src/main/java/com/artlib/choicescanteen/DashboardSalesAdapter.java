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
 * Created by ARTLIB on 30/09/2017.
 */

public class DashboardSalesAdapter extends ArrayAdapter<DashboardSales> {
    public DashboardSalesAdapter(Activity context, ArrayList<DashboardSales> dashboardSales){
        super(context, 0, dashboardSales);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_items, parent, false);
        }
        DashboardSales currentSale = getItem(position);

        TextView foodItemTextView = (TextView)listItemView.findViewById(R.id.food_text_view);
        foodItemTextView.setText(currentSale.getFoodItem());

        TextView amountTextView = (TextView)listItemView.findViewById(R.id.amount_text_view);
        amountTextView.setText(currentSale.getAmount());

        return listItemView;
    }
}
