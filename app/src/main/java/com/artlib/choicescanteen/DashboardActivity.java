package com.artlib.choicescanteen;

import android.content.DialogInterface;
import android.content.Intent;;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
    private ImageButton adminMenu;
    private GridLayout gridLayout;
    private TextView totalAmountTextView;
    private Switch aSwitch;
    private TextView username_display;

    private UserLocalDataStore userLocalDataStore;
    private DatabaseReference salesDatabaseReference;
    private DatabaseReference foodDatabaseReference;

    private FirebaseAuth firebaseAuth;

    private double total;
    private int counter = 0;
    private double overallTotal;
    private boolean displaySalesForToday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recordSalesView = (TextView) findViewById(R.id.record_sales_view);
        viewSalesView = (TextView) findViewById(R.id.view_sales_view);
        setupView = (TextView) findViewById(R.id.setup_view);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        totalSalesDisplay = (TextView) findViewById(R.id.total_sales_display);
        gridLayout = (GridLayout) findViewById(R.id.dashbaord_sales_grid);
        totalAmountTextView = (TextView) findViewById(R.id.overall_total_amount);
        aSwitch = (Switch) findViewById(R.id.today_only_switch);
        username_display = (TextView) findViewById(R.id.display_user);
        adminMenu = (ImageButton) findViewById(R.id.imageButton1);

        firebaseAuth = FirebaseAuth.getInstance();

        userLocalDataStore = new UserLocalDataStore(this);
        salesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Sales");
        salesDatabaseReference.keepSynced(true);

        foodDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Food_Items");
        foodDatabaseReference.keepSynced(true);

        controlDashboard();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserPopUpMenu(v);
            }
        });

        adminMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openAdminPopUpMenu(v);
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

        displaySalesFromStartToFinish();

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    // display sales for current day
                    displaySalesForToday = true;
                    overallTotal = 0;
                    totalAmountTextView.setText("");
                    totalSalesDisplay.setText("");
                    gridLayout.removeAllViews();
                    displaySalesForCurrentDay();

                } else {
                    // display sales from start to finish
                    displaySalesForToday = false;
                    overallTotal = 0;
                    totalSalesDisplay.setText("");
                    totalAmountTextView.setText("");
                    gridLayout.removeAllViews();
                    displaySalesFromStartToFinish();
                }
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        if (displaySalesForToday == true) {
            overallTotal = 0;
            gridLayout.removeAllViews();
            totalAmountTextView.setText("");
            displaySalesForCurrentDay();

        } else {
            // display sales from start to finish
            overallTotal = 0;
            gridLayout.removeAllViews();
            displaySalesFromStartToFinish();
        }
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    private void controlDashboard() {
        if (!userLocalDataStore.getIsAdminLoggedIn()) {
            setupView.setVisibility(View.INVISIBLE);
            username_display.setText(firebaseAuth.getCurrentUser().getEmail().toString());
            imageButton.setVisibility(View.VISIBLE);
            adminMenu.setVisibility(View.GONE);

        } else {
            username_display.setText(userLocalDataStore.getUser());
            imageButton.setVisibility(View.VISIBLE);
        }

    }

    private void openUserPopUpMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), imageButton);
        popupMenu.inflate(R.menu.more_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        logout();
                        break;

                    case R.id.add_food:
                        startActivity(new Intent(DashboardActivity.this, AddFoodActivity.class));
                        break;

                    case R.id.delete_food:
                        startActivity(new Intent(DashboardActivity.this, DisplayFoodItemsActivity.class));
                        break;

                    case R.id.record_sale:
                        startActivity(new Intent(DashboardActivity.this, RecordSalesActivity.class));
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void openAdminPopUpMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), adminMenu);
        popupMenu.inflate(R.menu.admin_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logout:
                        logout();
                        break;

                    case R.id.delete_user:
                        // // TODO: 18/10/2017  
                        break;

                    case R.id.add_food:
                        startActivity(new Intent(DashboardActivity.this, AddFoodActivity.class));
                        break;

                    case R.id.delete_food:
                        startActivity(new Intent(DashboardActivity.this, DisplayFoodItemsActivity.class));
                        break;

                    case R.id.add_user:
                        startActivity(new Intent(DashboardActivity.this, AddUserActivity.class));
                        break;

                    case R.id.add_sale:
                        startActivity(new Intent(DashboardActivity.this, RecordSalesActivity.class));
                        break;

                    case R.id.delete_sale:
                        startActivity(new Intent(DashboardActivity.this, DeleteSalesActivity.class));
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

    private void getFoodPricesForToday(final ArrayList<String> soldFoodItems) {
        totalSalesDisplay.setText("Today's Sales");

        for (final String food : soldFoodItems) {
            salesDatabaseReference.orderByChild("foodItem_date").equalTo(getSalesDate() + "" + food).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        Iterator<DataSnapshot> data = dataSnapshot.getChildren().iterator();
                        if (!data.equals(null)) {
                            while (data.hasNext()) {
                                total += Double.parseDouble((String) data.next().child("price").getValue());
                            }
                        }

                        overallTotal += total;

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
                                sold_food_text_view.setText("GHS " + total + "0");
                                gridLayout.addView(sold_food_text_view);

                                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                                param.height = 80;
                                param.leftMargin = 60;
                                sold_food_text_view.setLayoutParams(param);

                            }
                        }
                        totalAmountTextView.setText("GHS " + overallTotal + "0");
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

    private void displaySalesForCurrentDay() {
        final ArrayList<String> foodItems = new ArrayList<String>();
        salesDatabaseReference.orderByChild("dateOfSale").equalTo(getSalesDate()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                    while (dataSnapshotIterator.hasNext()) {
                        foodItems.add(dataSnapshotIterator.next().child("foodItem").getValue(String.class));
                    }
                }
                Set<String> noDuplicates = new HashSet<String>();
                noDuplicates.addAll(foodItems);
                foodItems.clear();
                foodItems.addAll(noDuplicates);

                if (foodItems.size() > 0) {
                    getFoodPricesForToday(foodItems);
                } else {
                    totalSalesDisplay.setText("Today's Sales");
                    totalAmountTextView.setText("GHS 00.00");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displaySalesFromStartToFinish() {
        final ArrayList<String> foodItems = new ArrayList<String>();
        salesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                    while (dataSnapshotIterator.hasNext()) {
                        foodItems.add(dataSnapshotIterator.next().child("foodItem").getValue(String.class));
                    }
                }
                Set<String> noDuplicates = new HashSet<String>();
                noDuplicates.addAll(foodItems);
                foodItems.clear();
                foodItems.addAll(noDuplicates);

                if (foodItems.size() > 0) {
                    getFoodPricesFromStartToFinish(foodItems);
                } else {
                    totalSalesDisplay.setText("Today's Sales");
                    totalAmountTextView.setText("GHS 00.00");
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFoodPricesFromStartToFinish(ArrayList<String> foodStuffs) {
        totalSalesDisplay.setText("Total Sales For Entire Period");

        for (final String food : foodStuffs) {
            salesDatabaseReference.orderByChild("foodItem").equalTo(food).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        Iterator<DataSnapshot> data = dataSnapshot.getChildren().iterator();
                        if (!data.equals(null)) {
                            while (data.hasNext()) {
                                total += Double.parseDouble((String) data.next().child("price").getValue());
                            }
                        }

                        overallTotal += total;

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
                                sold_food_text_view.setText("GHS " + total + "0");
                                gridLayout.addView(sold_food_text_view);

                                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                                param.height = 80;
                                param.leftMargin = 60;
                                sold_food_text_view.setLayoutParams(param);

                            }
                        }
                        totalAmountTextView.setText("GHS " + overallTotal + "0");
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

    private String getSalesDate() {
        final java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("dd-MM-yyy");
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        final String date = simpleDateFormat.format(new Date()).toString();
        return date;
    }

    private void createDeleteDialog(final String[] foodItems) {
        final boolean[] checkedItems = new boolean[foodItems.length];
        final ArrayList<Integer> selectedItems = new ArrayList<>();
        final AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setTitle("Available food items");
        builder.setMultiChoiceItems(foodItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                int mCounter = 0;
                if (isChecked) {
                    if (!selectedItems.contains(which)) {
                        selectedItems.add(which);
                        mCounter++;

                    }
                } else if(selectedItems.contains(mCounter)) {
                    selectedItems.remove(mCounter);

                }
            }
        });
        builder.setCancelable(false);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete items here
                for (Integer i: selectedItems) {
                    foodDatabaseReference.orderByChild("foodItem").equalTo(foodItems[i]).addValueEventListener(new ValueEventListener() {
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

        builder.create();
        builder.show();

    }

    private void launchDeleteDialog() {

        foodDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String[] foodItems = new String[(int)dataSnapshot.getChildrenCount()];
                    Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                    int count = 0;
                    while (count < ((int)dataSnapshot.getChildrenCount())) {
                        foodItems[count] = (dataSnapshotIterator.next().child("foodItem").getValue(String.class));
                        count++;
                    }

                    if (foodItems.length > 0) {
                        createDeleteDialog(foodItems);

                    } else {
                        Toast.makeText(DashboardActivity.this, "No food item to display", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
