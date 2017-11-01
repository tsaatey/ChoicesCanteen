package com.artlib.choicescanteen;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.Iterator;

public class DeleteSalesActivity extends AppCompatActivity {

    private Button viewDataButton;
    private TextView periodic_sales_text_view;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private DatePickerDialog datePickerDialog;

    private DatabaseReference salesDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_sales);

        salesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Sales");
        salesDatabaseReference.keepSynced(true);

        startDateEditText = (EditText) findViewById(R.id.first_date_picker);
        endDateEditText = (EditText) findViewById(R.id.second_date_picker);
        periodic_sales_text_view = (TextView) findViewById(R.id.periodic_sales_amount);
        viewDataButton = (Button) findViewById(R.id.preview_records);

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

                datePickerDialog = new DatePickerDialog(DeleteSalesActivity.this, new DatePickerDialog.OnDateSetListener() {
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

                datePickerDialog = new DatePickerDialog(DeleteSalesActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        displaySalesOnAlertDialog();

                    } else {
                        Toast.makeText(DeleteSalesActivity.this, "Provide an end date", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(DeleteSalesActivity.this, "Provide a start date", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displaySalesOnAlertDialog() {
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

                        dateOfSale.add(dataSnapshotIterator.next().child("saleDateAndTime").getValue(String.class));
                    }

                    Iterator<DataSnapshot> foodStuffsIterator = dataSnapshot.getChildren().iterator();
                    while (foodStuffsIterator.hasNext()) {
                        foodStuffs.add(foodStuffsIterator.next().child("foodItem").getValue(String.class));
                    }

                    Iterator<DataSnapshot> PricesIterator = dataSnapshot.getChildren().iterator();
                    while (PricesIterator.hasNext()) {
                        prices.add(PricesIterator.next().child("price").getValue(String.class));
                    }

                    String[] sales = new String[prices.size()];

                    for (int i = 0, n = prices.size(); i < n; i++) {
                        sales[i] = dateOfSale.get(i) + "\t" + foodStuffs.get(i) + "\t" + prices.get(i);
                    }

                    if (sales.length > 0) {
                        createDeleteSalesDialog(sales);

                    } else {
                        Toast.makeText(DeleteSalesActivity.this, "No sale to display", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createDeleteSalesDialog(final String[] sales) {
        final boolean[] checkedItems = new boolean[sales.length];
        final ArrayList<Integer> selectedItems = new ArrayList<>();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Available food items");
        builder.setCancelable(false);
        builder.setMultiChoiceItems(sales, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    if (!selectedItems.contains(which)) {
                        selectedItems.add(which);
                    } else {
                        selectedItems.remove(which);
                    }
                }
            }
        });

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete items here
                for (Integer i: selectedItems) {
                    String saleToDelete = sales[i];
                    String[] splitSalesToDelete = saleToDelete.split("\t");

                    salesDatabaseReference.orderByChild("foodItem").equalTo(splitSalesToDelete[1]).startAt(splitSalesToDelete[0]).endAt(splitSalesToDelete[0]).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String key = "";
                                Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                                if (dataSnapshotIterator.hasNext()) {
                                    key = dataSnapshotIterator.next().getKey();
                                }

                                if (!TextUtils.isEmpty(key))
                                    salesDatabaseReference.child(key).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    selectedItems.clear();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
