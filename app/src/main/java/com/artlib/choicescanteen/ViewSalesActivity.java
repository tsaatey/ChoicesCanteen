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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.List;
import java.util.Set;

public class ViewSalesActivity extends AppCompatActivity {

    private ListView listView;
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

        listView = (ListView) findViewById(R.id.display_sales_grid);

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
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

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
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

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
                        displaySalesInListView();

                    } else {
                        Toast.makeText(ViewSalesActivity.this, "Provide an end date", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(ViewSalesActivity.this, "Provide a start date", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displaySalesInListView() {
        String startDate = getStartDate();
        String endDate = getEndDate();

        final ArrayList<String> dateOfSale = new ArrayList<>();
        final ArrayList<String> foodStuffs = new ArrayList<>();
        final ArrayList<String> prices = new ArrayList<>();

        salesDatabaseReference.orderByChild("dateOfSale").startAt(startDate).endAt(endDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data: dataSnapshot.getChildren()) {
                        dateOfSale.add(data.child("saleDateAndTime").getValue(String.class));
                        foodStuffs.add(data.child("foodItem").getValue(String.class));
                        prices.add(data.child("price").getValue(String.class));
                        totalSalesForAllFoodItems += Double.parseDouble(data.child("price").getValue(String.class));
                    }

                    ArrayList<Sale> sales = new ArrayList<Sale>();
                    for (int i = 0; i < foodStuffs.size(); i++) {
                        sales.add(new Sale(dateOfSale.get(i), foodStuffs.get(i), "GHS "+ Double.parseDouble(prices.get(i))));
                    }

                    SaleAdapter arrayAdapter = new SaleAdapter(ViewSalesActivity.this, android.R.layout.simple_list_item_1, sales);
                    listView.setAdapter(arrayAdapter);

                    periodic_sales_text_view.setText("GHS " + totalSalesForAllFoodItems + "0");
                    totalSalesForAllFoodItems = 0;

                } else {
                    Toast.makeText(ViewSalesActivity.this, "No sales data to display", Toast.LENGTH_SHORT).show();
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
