package com.artlib.choicescanteen;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class DeleteFoodItemActivity extends AppCompatActivity {

    private DatabaseReference foodDatabaseReference;

    private ListView listView;
    private ArrayList<String> foodItems;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_food_item);

        listView = (ListView) findViewById(R.id.food_items_list);

        foodItems = new ArrayList<>();

        foodDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Food_Items");
        foodDatabaseReference.keepSynced(true);

        getFoodItemsFromDatabase();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(DeleteFoodItemActivity.this);
                alert.setMessage("Are you sure you want to delete this item?")
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String food = listView.getItemAtPosition(position).toString();
                                deleteFoodItem(food);
                                ArrayAdapter adapter = (ArrayAdapter)listView.getAdapter();
                                adapter.clear();
                            }
                        })
                        .create()
                        .show();
            }
        });

    }

    private void getFoodItemsFromDatabase() {
        foodDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                    while (dataSnapshotIterator.hasNext()) {
                        foodItems.add(dataSnapshotIterator.next().child("foodItem").getValue(String.class));
                    }
                    arrayAdapter = new ArrayAdapter<String>(DeleteFoodItemActivity.this, android.R.layout.simple_list_item_1, foodItems);
                    listView.setAdapter(arrayAdapter);
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
                String key = "";
                Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                if (dataSnapshotIterator.hasNext()) {
                    key = dataSnapshotIterator.next().getKey();
                }
                if (!TextUtils.isEmpty(key))
                    foodDatabaseReference.child(key).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
