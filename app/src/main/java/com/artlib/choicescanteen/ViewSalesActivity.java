package com.artlib.choicescanteen;

import android.app.DatePickerDialog;
import android.os.Build;
import android.support.annotation.IntegerRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ViewSalesActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private Button viewDataButton;
    private TextView periodic_sales_text_view;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private DatePickerDialog datePickerDialog;
    private LinearLayout linearLayout;

    private DatabaseReference salesDatabaseReference;

    private double totalSalesForAllFoodItems;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sales);

        gridLayout = (GridLayout) findViewById(R.id.display_sales_grid);

        salesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Sales");
        salesDatabaseReference.keepSynced(true);

        startDateEditText = (EditText) findViewById(R.id.firstDatePicker);
        endDateEditText = (EditText) findViewById(R.id.secondDatePicker);
        periodic_sales_text_view = (TextView) findViewById(R.id.periodic_sales_amount);
        viewDataButton = (Button) findViewById(R.id.continue_to_view_records);
        linearLayout = (LinearLayout) findViewById(R.id.display_sales_linear_layout);
        linearLayout.setVisibility(View.GONE);

        startDateEditText.setFocusable(false);
        startDateEditText.setClickable(true);
        startDateEditText.setLongClickable(false);

        endDateEditText.setFocusable(false);
        endDateEditText.setClickable(true);
        startDateEditText.setLongClickable(false);


        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                datePickerDialog = new DatePickerDialog(ViewSalesActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startDateEditText.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

                datePickerDialog = new DatePickerDialog(ViewSalesActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endDateEditText.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }

        });

        viewDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(startDateEditText.getText().toString())) {
                    if (!TextUtils.isEmpty(startDateEditText.getText().toString())) {
                        linearLayout.setVisibility(View.VISIBLE);
                        gridLayout.removeAllViews();
                        displaySalesInGridLayout();

                    } else {
                        Toast.makeText(ViewSalesActivity.this, "Provide an end date", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(ViewSalesActivity.this, "Provide a start date", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displaySalesInGridLayout() {
        String startDate = getStartDate();
        String endDate = getEndDate();

        final ArrayList<String> dateOfSale = new ArrayList<>();
        final ArrayList<String> foodStuffs = new ArrayList<>();
        final ArrayList<String> prices = new ArrayList<>();

        salesDatabaseReference.orderByChild("dateOfSale").startAt(startDate).endAt(endDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                    while (dataSnapshotIterator.hasNext()) {

                        dateOfSale.add(dataSnapshotIterator.next().child("dateOfSale").getValue(String.class));
                    }

                    Iterator<DataSnapshot> foodStuffsIterator = dataSnapshot.getChildren().iterator();
                    while (foodStuffsIterator.hasNext()) {
                        foodStuffs.add(foodStuffsIterator.next().child("foodItem").getValue(String.class));
                    }

                    Iterator<DataSnapshot> PricesIterator = dataSnapshot.getChildren().iterator();
                    while (PricesIterator.hasNext()) {
                        prices.add(PricesIterator.next().child("price").getValue(String.class));
                    }

                    Iterator<DataSnapshot> totalSalesIterator = dataSnapshot.getChildren().iterator();
                    while (totalSalesIterator.hasNext()) {
                        totalSalesForAllFoodItems += Double.parseDouble(totalSalesIterator.next().child("price").getValue(String.class));
                    }

                    int count = 0;
                    for (String food : foodStuffs) {

                        for (int i = 1; i <= 3; i++) {

                            if (i == 1) {
                                TextView sold_food_text_view = new TextView(ViewSalesActivity.this);
                                sold_food_text_view.setId(counter);
                                sold_food_text_view.setText(dateOfSale.get(count));
                                sold_food_text_view.setWidth(140);
                                gridLayout.addView(sold_food_text_view);

                                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                                param.height = 80;
                                param.width = 200;
                                param.leftMargin = 80;
                                sold_food_text_view.setLayoutParams(param);

                            } else if (i == 2) {
                                TextView sold_food_text_view = new TextView(ViewSalesActivity.this);
                                sold_food_text_view.setId(counter);
                                sold_food_text_view.setText(food);
                                sold_food_text_view.setWidth(140);
                                gridLayout.addView(sold_food_text_view);

                                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                                param.height = 80;
                                param.width = 300;
                                param.leftMargin = 80;
                                sold_food_text_view.setLayoutParams(param);

                            } else if (i == 3) {
                                TextView sold_food_text_view = new TextView(ViewSalesActivity.this);
                                sold_food_text_view.setId(counter);
                                sold_food_text_view.setText("GHS " + Double.parseDouble(prices.get(count)) + "0");
                                gridLayout.addView(sold_food_text_view);

                                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                                param.height = 80;
                                param.leftMargin = 25;
                                sold_food_text_view.setLayoutParams(param);
                            }
                        }
                        count++;
                        counter++;
                    }

                    periodic_sales_text_view.setText("GHS " + totalSalesForAllFoodItems + "0");
                    totalSalesForAllFoodItems = 0;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getStartDate() {
        String date = startDateEditText.getText().toString();
        String[] dateArray = date.split("-");
        String a = dateArray[0];
        String b = dateArray[1];
        String c = dateArray[2];

        int d = Integer.parseInt(a);
        if (d < 10) {
            a = "0".concat(a);
        }

        String formattedDate = a.concat("-").concat(b).concat("-").concat(c);
        return formattedDate;

    }

    private String getEndDate() {
        String date = endDateEditText.getText().toString();
        String[] dateArray = date.split("-");
        String a = dateArray[0];
        String b = dateArray[1];
        String c = dateArray[2];

        int d = Integer.parseInt(a);
        if (d < 10) {
            a = "0".concat(a);
        }

        String formattedDate = a.concat("-").concat(b).concat("-").concat(c);
        return formattedDate;

    }

}
