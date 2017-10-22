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

public class FoodAdapter extends ArrayAdapter<Food> {

    public FoodAdapter(Activity context, ArrayList<Food> foods) {
        super(context, 0, foods);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.list_items, parent, false);
        }

        Food currentFood = getItem(position);

        TextView foodItemTextView = (TextView)listItem.findViewById(R.id.food_text_view);
        foodItemTextView.setText(currentFood.getFoodItem());

        return listItem;
    }
}
