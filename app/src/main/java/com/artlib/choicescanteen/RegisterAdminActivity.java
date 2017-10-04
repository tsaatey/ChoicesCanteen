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

import org.w3c.dom.Text;

public class RegisterAdminActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText admin_name;
    private EditText admin_email;
    private EditText admin_password;
    private EditText confirm_admin_password;

    private DatabaseReference adminDatabaseReference;
    private FirebaseAuth adminAuth;

    private UserLocalDataStore userLocalDataStore;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);

        createAccountButton = (Button) findViewById(R.id.create_account_button);
        admin_name = (EditText) findViewById(R.id.admin_name);
        admin_email = (EditText) findViewById(R.id.username);
        admin_password = (EditText) findViewById(R.id.password);
        confirm_admin_password = (EditText) findViewById(R.id.confirm_password_field);

        progressDialog = new ProgressDialog(this);

        adminDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Admin");
        adminDatabaseReference.keepSynced(true);

        adminAuth = FirebaseAuth.getInstance();

        userLocalDataStore = new UserLocalDataStore(this);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAdmin();
            }
        });
    }

    private void registerAdmin() {
        final String name = admin_name.getText().toString();
        final String email = admin_email.getText().toString();
        final String password = admin_password.getText().toString();
        final String confirmPassword = confirm_admin_password.getText().toString();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)) {
            if (password.equals(confirmPassword)) {
                progressDialog.setMessage("Creating admin account, please wait...");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                adminAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = adminAuth.getCurrentUser().getUid();
                            DatabaseReference databaseReference = adminDatabaseReference.child(id);
                            databaseReference.child("name").setValue(name);

                            progressDialog.dismiss();

                            userLocalDataStore.isAdminLoggedIn(true);
                            Intent dashboardIntent = new Intent(RegisterAdminActivity.this, DashboardActivity.class);
                            dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(dashboardIntent);

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterAdminActivity.this, "Error in creating account", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(RegisterAdminActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(RegisterAdminActivity.this, "Make sure all fields are not empty", Toast.LENGTH_LONG).show();
        }
    }
}
