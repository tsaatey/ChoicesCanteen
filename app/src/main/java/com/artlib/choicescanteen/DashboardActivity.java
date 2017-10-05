package com.artlib.choicescanteen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.telecom.Call;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

public class DashboardActivity extends AppCompatActivity {

    private TextView recordSalesView;
    private TextView viewSalesView;
    private TextView setupView;
    private TextView totalSalesDisplay;
    private ImageButton imageButton;
    private GridLayout gridLayout;

    private UserLocalDataStore userLocalDataStore;
    private DatabaseReference salesDatabaseReference;
    private DatabaseReference foodDatabaseReference;

    private FirebaseAuth firebaseAuth;

    private double total;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recordSalesView = (TextView)findViewById(R.id.record_sales_view);
        viewSalesView = (TextView)findViewById(R.id.view_sales_view);
        setupView = (TextView)findViewById(R.id.setup_view);
        imageButton = (ImageButton)findViewById(R.id.imageButton);
        totalSalesDisplay = (TextView) findViewById(R.id.total_sales_display);
        gridLayout = (GridLayout) findViewById(R.id.dashbaord_sales_grid);

        firebaseAuth = FirebaseAuth.getInstance();

        userLocalDataStore = new UserLocalDataStore(this);
        salesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Sales");
        salesDatabaseReference.keepSynced(true);

        foodDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Food_Items");
        foodDatabaseReference.keepSynced(true);

        controlDashboard();

        getFoodItems();


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPopUpMenu(v);
            }
        });

        recordSalesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, RecordSalesActivity.class));
            }
        });

        viewSalesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ViewSalesActivity.class));
            }
        });

        setupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, SetupActivity.class));
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        gridLayout.removeAllViews();
        getFoodItems();
    }

    private void controlDashboard(){
        if (!userLocalDataStore.getIsAdminLoggedIn()) {
            setupView.setVisibility(View.INVISIBLE);
        }
    }

    private void openPopUpMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), imageButton);
        popupMenu.inflate(R.menu.more_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        logout();
                        break;
                }
                return false;

            }
        });
        popupMenu.show();
    }

    private void logout() {
        firebaseAuth.signOut();
        userLocalDataStore.clearUserData();
        Intent mainIntent = new Intent(DashboardActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }

    private void getFoodPrices(final ArrayList<String> soldFoodItems) {
        totalSalesDisplay.setText("Total Sales on " + getSalesDate());

        for (final String food: soldFoodItems) {
            salesDatabaseReference.orderByChild("foodItem_date").equalTo(getSalesDate()+""+ food).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        Iterator<DataSnapshot> data = dataSnapshot.getChildren().iterator();
                        if (!data.equals(null)){
                            while (data.hasNext()) {
                                total += Double.parseDouble((String) data.next().child("price").getValue());
                            }
                        }

                        for (int i = 1; i <= 2; i++) {
                            if (i % 2 == 1) {
                                TextView sold_food_text_view = new TextView(DashboardActivity.this);
                                sold_food_text_view.setId(counter);
                                sold_food_text_view.setText(food);
                                sold_food_text_view.setWidth(140);
                                gridLayout.addView(sold_food_text_view);

                                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                                param.height = 80;
                                param.width = 400;
                                param.leftMargin = 80;
                                sold_food_text_view.setLayoutParams(param);

                            } else if (i % 2 == 0) {
                                TextView sold_food_text_view = new TextView(DashboardActivity.this);
                                sold_food_text_view.setId(counter);
                                sold_food_text_view.setText("GHS "+total + "0");
                                gridLayout.addView(sold_food_text_view);

                                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                                param.height = 80;
                                param.leftMargin = 60;
                                sold_food_text_view.setLayoutParams(param);

                            }
                        }
                        total = 0;
                        counter++;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
    }

    private void getFoodItems() {
        final ArrayList<String> foodItems = new ArrayList<String>();
        salesDatabaseReference.orderByChild("dateOfSale").equalTo(getSalesDate()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                    while(dataSnapshotIterator.hasNext()) {
                        foodItems.add(dataSnapshotIterator.next().child("foodItem").getValue(String.class));
                    }
                }
                Set<String> noDuplicates = new HashSet<String>();
                noDuplicates.addAll(foodItems);
                foodItems.clear();
                foodItems.addAll(noDuplicates);

                if (foodItems.size() > 0) {
                    getFoodPrices(foodItems);
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

}
