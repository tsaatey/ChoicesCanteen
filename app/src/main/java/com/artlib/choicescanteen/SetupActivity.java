package com.artlib.choicescanteen;

import android.app.ProgressDialog;
import android.opengl.Visibility;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.SecureRandom;
import java.security.SecurityPermission;
import java.util.HashMap;
import java.util.Map;

public class SetupActivity extends AppCompatActivity {

    private EditText fullName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText foodItem;
    private Button addUserButton;
    private Button addFoodItemButton;
    private TextView addUserTextView;
    private TextView addFoodTextView;
    private Button cancelUserButton;
    private Button cancelFoodButton;

    private LinearLayout foodLayout;
    private LinearLayout userLayout;

    private ProgressDialog progressDialog;

    private DatabaseReference foodItemDatabaseReference;
    private DatabaseReference addUserDatabaseReference;

    private FirebaseAuth firebaseAuth;

    private UserLocalDataStore userLocalDataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        fullName = (EditText)findViewById(R.id.name_field);
        email = (EditText) findViewById(R.id.email_field);
        password = (EditText) findViewById(R.id.password_field);
        confirmPassword = (EditText) findViewById(R.id.confirm_password_field);
        foodItem = (EditText) findViewById(R.id.food_type_field);
        addUserButton = (Button) findViewById(R.id.add_user_button);
        addFoodItemButton = (Button) findViewById(R.id.food_type_button);

        addUserTextView = (TextView) findViewById(R.id.add_user);
        addFoodTextView = (TextView) findViewById(R.id.add_food_type);

        cancelFoodButton = (Button) findViewById(R.id.cancel_button);
        cancelUserButton = (Button) findViewById(R.id.cancel_action_button);

        foodLayout = (LinearLayout)findViewById(R.id.food_type_layout);
        userLayout = (LinearLayout)findViewById(R.id.user_layout);

        foodLayout.setVisibility(View.GONE);
        userLayout.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(this);

        foodItemDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Food_Items");
        foodItemDatabaseReference.keepSynced(true);

        addUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        addUserDatabaseReference.keepSynced(true);

        firebaseAuth = FirebaseAuth.getInstance();

        userLocalDataStore = new UserLocalDataStore(this);

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String full_name = fullName.getText().toString();
                String mail = email.getText().toString();
                String pass1 = password.getText().toString();
                String pass2 = confirmPassword.getText().toString();

                addUserToDatabase(full_name, mail, pass1, pass2);
            }
        });

        addFoodItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String food = foodItem.getText().toString();
                addFoodToDatabase(food);
            }
        });

        addUserTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLayout.setVisibility(View.VISIBLE);
                foodLayout.setVisibility(View.GONE);
            }
        });

        addFoodTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodLayout.setVisibility(View.VISIBLE);
                userLayout.setVisibility(View.GONE);
            }
        });

        cancelFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodItem.setText("");
                foodLayout.setVisibility(View.GONE);
            }
        });

        cancelUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setText("");
                fullName.setText("");
                password.setText("");
                confirmPassword.setText("");
                userLayout.setVisibility(View.GONE);
            }
        });
    }

    private void addFoodToDatabase(String food) {
        if (!TextUtils.isEmpty(food)){

            String foodId = generateId();
            userLocalDataStore.storeFoodItemId(foodId);
            DatabaseReference storeFood = foodItemDatabaseReference.push();
            Map<String, String> foodData = new HashMap<>();
            foodData.put("foodItem", food);
            storeFood.setValue(foodData);

            Toast.makeText(SetupActivity.this, "Food item saved!", Toast.LENGTH_SHORT).show();
            foodItem.setText("");
        }

    }

    private void addUserToDatabase(final String name, String user_mail, String password1, String password2) {

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(user_mail) && !TextUtils.isEmpty(password1) && !TextUtils.isEmpty(password2)) {
            progressDialog.setMessage("Creating user account...please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();

            if (password1.equals(password2)) {
                final String userId = generateId();

                firebaseAuth.createUserWithEmailAndPassword(user_mail, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            DatabaseReference storeUser = addUserDatabaseReference.child(userId);
//                            Map<String, String> userData = new HashMap<String, String>();
//                            userData.put("name", name);
//                            storeUser.setValue(userData);
                            if (progressDialog != null && progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText(SetupActivity.this, "User account created!", Toast.LENGTH_SHORT).show();

                        } else {
                            if (progressDialog != null && progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText(SetupActivity.this, "Error in adding user. Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                Toast.makeText(SetupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SetupActivity.this, "No field should be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateId(){
        String characters = "012EFG3456789ABCDHI56789JKLMNOqrstuVWXYZabcdefghijPQRSTUklmnopvwxyz";
        int idLength = 6;
        String generatedId = "";
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder(idLength);

        for (int i = 0; i < idLength; i++) {
            builder.append(characters.charAt(random.nextInt(characters.length())));
        }
        generatedId = builder.toString();

        return generatedId;
    }
}
