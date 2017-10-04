package com.artlib.choicescanteen;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.telecom.Call;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

    private UserLocalDataStore userLocalDataStore;
    private DatabaseReference salesDatabaseReference;
    private DatabaseReference foodDatabaseReference;

    private FirebaseAuth firebaseAuth;

    private FoodItems food_stuffs;

    private double total;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recordSalesView = (TextView)findViewById(R.id.record_sales_view);
        viewSalesView = (TextView)findViewById(R.id.view_sales_view);
        setupView = (TextView)findViewById(R.id.setup_view);
        imageButton = (ImageButton)findViewById(R.id.imageButton);
        totalSalesDisplay = (TextView) findViewById(R.id.total_sales_display);

        firebaseAuth = FirebaseAuth.getInstance();

        userLocalDataStore = new UserLocalDataStore(this);
        salesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Sales");
        salesDatabaseReference.keepSynced(true);

        foodDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Food_Items");
        foodDatabaseReference.keepSynced(true);

        food_stuffs = new FoodItems();

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
        final ArrayList<Double> totalAmounts = new ArrayList<>();
        totalSalesDisplay.setText("Total Sales on " + getSalesDate());

        for (String food: soldFoodItems) {
                salesDatabaseReference.orderByChild("foodItem_date").equalTo(getSalesDate().concat(food)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            int counter = 0;
                            Iterator<DataSnapshot> data = dataSnapshot.getChildren().iterator();

                            while (data.hasNext()) {
                                total = 0;
                                total += Double.parseDouble(data.next().child("price").getValue(String.class));
                            }

                            if (counter == soldFoodItems.size()) {
                                createTotalSalesArrayList(total, true);
                            } else {
                                createTotalSalesArrayList(total, false);
                            }
                            counter++;
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

            }
    }

    private void createTotalSalesArrayList(double price, boolean outLoopFinished) {
        ArrayList<Double> totalPricesList = new ArrayList<>();
        totalPricesList.add(price);

        if (outLoopFinished == true) {
            displaySalesOnDashboard(totalPricesList);
        }
    }

    private void displaySalesOnDashboard(ArrayList<Double> completeSales) {
        ArrayList<String> soldFoodItems = getFoodItems();
        ArrayList<DashboardSales> dashboardSales = new ArrayList<>();
        for (int l = 0, n = soldFoodItems.size(); l < n; l++) {
            dashboardSales.add(new DashboardSales(soldFoodItems.get(l), ""+completeSales.get(l)));
        }

        DashboardSalesAdapter dashboardSalesAdapter = new DashboardSalesAdapter(DashboardActivity.this, dashboardSales);

        ListView listView = (ListView)findViewById(R.id.total_sales_list);
        listView.setAdapter(dashboardSalesAdapter);
    }

    private ArrayList<String> getFoodItems() {
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

                getFoodPrices(foodItems);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return foodItems;
    }

    private String getSalesDate() {
        final java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("dd-MM-yyy");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        final String date = simpleDateFormat.format(new Date()).toString();
        return date;
    }

    private Set<String> getRidOfDuplicates(ArrayList<String> duplicateList) {
        final Set<String> listToReturn = new HashSet<>();
        final Set<String> set1 = new HashSet<>();
         for (String food: duplicateList) {
             if (!set1.add(food)) {
                 listToReturn.add(food);
             }
         }
         return listToReturn;
    }
}
