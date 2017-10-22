package com.artlib.choicescanteen;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class DisplayFoodItemsActivity extends AppCompatActivity {

    private DatabaseReference foodDatabaseReference;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_food_items);
        listView = (ListView) findViewById(R.id.food_items);

        foodDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Food_Items");
        foodDatabaseReference.keepSynced(true);

        displayFoodItems();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(DisplayFoodItemsActivity.this);
                alert.setMessage("Delete this food item?")
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String food  = ((TextView)view).getText().toString();
                                deleteFoodItem(food);
                                ArrayAdapter<String> arrayAdapter = (ArrayAdapter<String>)listView.getAdapter();
                                arrayAdapter.clear();
                            }

                        })
                        .create()
                        .show();
            }

        });
    }

    private void displayFoodItems() {
        final ArrayList<String> foodItems = new ArrayList<>();

        foodDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                    while (dataSnapshotIterator.hasNext()) {
                        foodItems.add(dataSnapshotIterator.next().child("foodItem").getValue(String.class));
                    }

                    ArrayAdapter<String> foodArrayAdapter = new ArrayAdapter<String>(DisplayFoodItemsActivity.this, android.R.layout.simple_list_item_1, foodItems);
                    ListView listView = (ListView) findViewById(R.id.food_items);
                    listView.setAdapter(foodArrayAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void deleteFoodItem(String food) {
         foodDatabaseReference.orderByChild("foodItem").equalTo(food).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String key = "";
                    Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();

                    if (dataSnapshotIterator.hasNext()) {
                        key = dataSnapshotIterator.next().getKey();
                    }

                    if (!TextUtils.isEmpty(key))
                        foodDatabaseReference.child(key).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
