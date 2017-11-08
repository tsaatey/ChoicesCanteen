package com.artlib.choicescanteen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class AddUserActivity extends AppCompatActivity {

    private EditText full_name;
    private EditText email_address;
    private EditText password;
    private EditText confirmed_password;
    private Button addUserButton;
    private Button backButton;

    private ProgressDialog progressDialog;

    private DatabaseReference addUserDatabaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        full_name = (EditText) findViewById(R.id.name_field);
        email_address = (EditText) findViewById(R.id.email_field);
        password = (EditText) findViewById(R.id.password_field);
        confirmed_password = (EditText) findViewById(R.id.confirm_password_field);
        addUserButton = (Button) findViewById(R.id.add_user_button);
        backButton = (Button) findViewById(R.id.cancel_action_button);

        progressDialog = new ProgressDialog(this);

        addUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        addUserDatabaseReference.keepSynced(true);

        firebaseAuth = FirebaseAuth.getInstance();

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = full_name.getText().toString();
                String email = email_address.getText().toString();
                String pass1 = password.getText().toString();
                String pass2 = confirmed_password.getText().toString();

                addUserToDatabase(name, email, pass1, pass2);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddUserActivity.this, DashboardActivity.class));
            }
        });
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
                            DatabaseReference storeUser = addUserDatabaseReference.child(userId);
                            Map<String, String> userData = new HashMap<String, String>();
                            userData.put("name", name);
                            storeUser.setValue(userData);

                            if (progressDialog != null && progressDialog.isShowing())
                                progressDialog.dismiss();

                        } else {
                            if (progressDialog != null && progressDialog.isShowing())
                                progressDialog.dismiss();

                            Toast.makeText(AddUserActivity.this, "Error in adding user. Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } else {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                Toast.makeText(AddUserActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AddUserActivity.this, "No field should be empty", Toast.LENGTH_SHORT).show();
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
