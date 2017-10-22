package com.artlib.choicescanteen;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by ARTLIB on 21/10/2017.
 */

public class SaleAdapter extends ArrayAdapter<Sale> {

    public SaleAdapter(Activity context, ArrayList<Sale> sales) {
        super(context, 0, sales);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
