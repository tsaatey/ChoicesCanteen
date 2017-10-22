package com.artlib.choicescanteen;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

public class RecordSalesActivity extends AppCompatActivity {

    private EditText foodPrice;
    private Spinner spinner;
    private Button recordSaleButton;

    private ProgressDialog progressDialog;
    private UserLocalDataStore userLocalDataStore;

    private DatabaseReference recordSalesDatabaseReference;
    private DatabaseReference foodDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_sales);

        foodPrice = (EditText) findViewById(R.id.food_price);
        spinner = (Spinner) findViewById(R.id.food_list);
        recordSaleButton = (Button) findViewById(R.id.record_sales_button);

        progressDialog = new ProgressDialog(this);

        recordSalesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Sales");
        recordSalesDatabaseReference.keepSynced(true);

        foodDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Food_Items");
        foodDatabaseReference.keepSynced(true);

        userLocalDataStore = new UserLocalDataStore(this);

        fetchDataForSpinner();

        recordSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordSales();
            }
        });

    }

    /**
     * A method to populate the dropdown list (spinner)
     * @param foodItems
     */
    private void loadDataIntoSpinner(ArrayList<String> foodItems) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, foodItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void recordSales() {
        final String food = spinner.getSelectedItem().toString();
        final String amount = foodPrice.getText().toString();
        if (!TextUtils.isEmpty(amount) && !TextUtils.isEmpty(food) && !food.equals("Select food item")) {
            Map<String, String> saleData = new HashMap<>();
            String dateOfSale = getSalesDate();
            saleData.put("dateOfSale", dateOfSale);
            saleData.put("saleDateAndTime", getSalesDateAndTime());
            saleData.put("foodItem", food);
            saleData.put("foodItem_date", dateOfSale.concat(food));
            saleData.put("price", amount);

            DatabaseReference recordSale = recordSalesDatabaseReference.push();
            recordSale.setValue(saleData);

            Toast.makeText(RecordSalesActivity.this, "Sale recorded!", Toast.LENGTH_SHORT).show();

            foodPrice.setText("");

        } else {
            Toast.makeText(RecordSalesActivity.this, "Please select food and enter food price", Toast.LENGTH_SHORT).show();
        }

    }

    private void fetchDataForSpinner() {
        foodDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> foods = new ArrayList<String>();
                    foods.add("Select food item");
                    Iterator<DataSnapshot> foodItems = dataSnapshot.getChildren().iterator();

                    while (foodItems.hasNext()) {
                        foods.add(foodItems.next().child("foodItem").getValue().toString());
                    }
                    loadDataIntoSpinner(foods);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getSalesDate() {
        final java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("dd-MM-yyy");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        final String date = simpleDateFormat.format(new Date()).toString();
        return date;
    }

    private String getSalesDateAndTime() {
        final java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("dd-MM-yyy HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        final String date = simpleDateFormat.format(new Date()).toString();
        return date;
    }
}
