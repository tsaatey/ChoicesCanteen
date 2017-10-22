package com.artlib.choicescanteen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class AddFoodActivity extends AppCompatActivity {

    private EditText foodItem;
    private Button addFoodButton;
    private Button backButton;

    private DatabaseReference foodItemDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        foodItem = (EditText) findViewById(R.id.food_type_field);
        addFoodButton = (Button) findViewById(R.id.food_type_button);
        backButton = (Button) findViewById(R.id.cancel_button);

        foodItemDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Food_Items");
        foodItemDatabaseReference.keepSynced(true);

        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String food = foodItem.getText().toString();
                addFoodToDatabase(food);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddFoodActivity.this, DashboardActivity.class));
            }
        });
    }

    private void addFoodToDatabase(String food) {
        if (!TextUtils.isEmpty(food)){
            DatabaseReference storeFood = foodItemDatabaseReference.push();
            Map<String, String> foodData = new HashMap<>();
            foodData.put("foodItem", food);
            storeFood.setValue(foodData);

            Toast.makeText(AddFoodActivity.this, "Food item saved!", Toast.LENGTH_SHORT).show();
            foodItem.setText("");
        }

    }

}
