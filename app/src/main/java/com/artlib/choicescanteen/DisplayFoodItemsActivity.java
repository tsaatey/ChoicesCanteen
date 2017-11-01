package com.artlib.choicescanteen;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DisplayFoodItemsActivity extends AppCompatActivity {

    private DatabaseReference foodDatabaseReference;

    private ListView listView;
    private ArrayList<String> foodItems;
    private ArrayList<String> checkedFoodItems;
    private ArrayList<String> foodItemKeys;
    private ArrayList<String> checkedFoodItemKeys;
    private ArrayAdapter arrayAdapter;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_food_items);
        listView = (ListView) findViewById(R.id.food_items);

        foodItems = new ArrayList<>();
        checkedFoodItems = new ArrayList<>();
        foodItemKeys = new ArrayList<>();
        checkedFoodItemKeys = new ArrayList<>();

        foodDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Food_Items");
        foodDatabaseReference.keepSynced(true);

        foodDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data: dataSnapshot.getChildren()){
                        foodItems.add(data.child("foodItem").getValue(String.class));
                        foodItemKeys.add(data.getKey());
                    }
                }

                arrayAdapter = new ArrayAdapter<>(DisplayFoodItemsActivity.this, android.R.layout.simple_list_item_multiple_choice, foodItems);
                listView.setAdapter(arrayAdapter);

                listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE_MODAL);

                listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                        if (checked) {
                            checkedFoodItems.add(foodItems.get(position));
                            checkedFoodItemKeys.add(foodItemKeys.get(position));
                            counter += 1;
                            if (counter > 1)
                                mode.setTitle(counter + " items selected");
                            else
                                mode.setTitle(counter + " item selected");
                        } else {
                            counter -= 1;
                        }
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        MenuInflater menuInflater = mode.getMenuInflater();
                        menuInflater.inflate(R.menu.delete_menu, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.deleteMenu:
                                for (String selectedFood: checkedFoodItems) {
                                    delete(selectedFood);
                                }

                                if (counter > 1)
                                    Toast.makeText(DisplayFoodItemsActivity.this, counter + " items deleted", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(DisplayFoodItemsActivity.this, counter + " item deleted", Toast.LENGTH_SHORT).show();

                                counter = 0;
                                return true;

                            default:
                                return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void delete(final String food) {
        foodDatabaseReference.orderByChild("foodItem").equalTo(food).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String key = "";
                    Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                    if (dataSnapshotIterator.hasNext()) {
                        key = dataSnapshotIterator.next().getKey();
                        foodDatabaseReference.child(key).removeValue();
                    }
                }
                ArrayAdapter adapter = (ArrayAdapter)listView.getAdapter();
                adapter.clear();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
