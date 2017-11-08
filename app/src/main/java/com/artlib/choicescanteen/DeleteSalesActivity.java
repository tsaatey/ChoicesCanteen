package com.artlib.choicescanteen;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Iterator;

public class DeleteSalesActivity extends AppCompatActivity {

    private Button viewDataButton;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private DatePickerDialog datePickerDialog;
    private ListView listView;
    private ImageButton imageButton;
    private TextView counterTextView;
    private DeleteSalesAdapter saleAdapter;

    private DatabaseReference salesDatabaseReference;

    private ArrayList<String> dateOfSale;
    private ArrayList<String> foodStuffs;
    private ArrayList<String> prices;
    private ArrayList<String> saleKeys;
    private ArrayList<String> checkedSaleKeys;
    private ArrayList<String> checkFoodItems;
    private ArrayList<String> dateAndTime;
    private ArrayList<String> checkedDateAndTime;

    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_sales);

        salesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Sales");
        salesDatabaseReference.keepSynced(true);

        dateOfSale = new ArrayList<>();
        foodStuffs = new ArrayList<>();
        prices = new ArrayList<>();
        saleKeys = new ArrayList<>();
        checkedSaleKeys = new ArrayList<>();
        checkFoodItems = new ArrayList<>();
        dateAndTime = new ArrayList<>();
        checkedDateAndTime = new ArrayList<>();

        startDateEditText = (EditText) findViewById(R.id.first_date_picker);
        endDateEditText = (EditText) findViewById(R.id.second_date_picker);
        viewDataButton = (Button) findViewById(R.id.preview_records);
        imageButton = (ImageButton) findViewById(R.id.delete_sale_button);
        counterTextView = (TextView) findViewById(R.id.number_of_items);
        listView = (ListView) findViewById(R.id.delete_sales_list_view);
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE_MODAL);

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
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

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
                        displaySalesOnListView();

                    } else {
                        Toast.makeText(DeleteSalesActivity.this, "Provide an end date", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(DeleteSalesActivity.this, "Provide a start date", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox)listView.getChildAt(position).findViewById(R.id.checkbox);
                if (!checkBox.isChecked()) {
                    checkBox.setChecked(true);
                    checkFoodItems.add(foodStuffs.get(position));
                    checkedDateAndTime.add(dateAndTime.get(position));
                    counter++;

                } else {
                    checkBox.setChecked(false);
                    checkFoodItems.remove(foodStuffs.get(position));
                    checkedDateAndTime.remove(dateAndTime.get(position));
                    counter--;
                }

                if (counter > 0) {
                    if (counter > 1) {
                        counterTextView.setText(counter + " items selected");
                    } else if (counter == 1) {
                        counterTextView.setText("1 item selected");
                    }
                } else {
                    counterTextView.setText("");
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFoodItems.size() > 0) {
                    for (String date_time: checkedDateAndTime) {
                        deleteSale(date_time);
                    }
                    counterTextView.setText("");
                    displaySalesOnListView();

                } else {
                    Toast.makeText(DeleteSalesActivity.this, "No sale selected!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void displaySalesOnListView() {
        String startDate = getStartDate();
        String endDate = getEndDate();

        salesDatabaseReference.orderByChild("dateOfSale").startAt(startDate).endAt(endDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data: dataSnapshot.getChildren()) {
                        saleKeys.add(data.getKey());
                        dateOfSale.add(data.child("saleDateAndTime").getValue(String.class));
                        foodStuffs.add(data.child("foodItem").getValue(String.class));
                        prices.add(data.child("price").getValue(String.class));
                        dateAndTime.add(data.child("foodItem_date_time").getValue(String.class));
                    }

                    ArrayList<DeleteSale> sales = new ArrayList<DeleteSale>();

                    for (int i = 0, n = prices.size(); i < n; i++) {
                        sales.add(new DeleteSale(dateOfSale.get(i), foodStuffs.get(i), "GHS " + Double.parseDouble(prices.get(i))));
                    }

                    if (sales.size() > 0) {
                        DeleteSalesAdapter saleAdapter = new DeleteSalesAdapter(DeleteSalesActivity.this, sales);
                        listView.setAdapter(saleAdapter);

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

    private void deleteSale(String food) {
        salesDatabaseReference.orderByChild("foodItem_date_time").equalTo(food).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String key = "";
                    Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                    if (dataSnapshotIterator.hasNext()) {
                        key = dataSnapshotIterator.next().getKey();
                        salesDatabaseReference.child(key).removeValue();
                    }
                }

                DeleteSalesAdapter deleteSalesAdapter = (DeleteSalesAdapter)listView.getAdapter();
                deleteSalesAdapter.clear();
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
