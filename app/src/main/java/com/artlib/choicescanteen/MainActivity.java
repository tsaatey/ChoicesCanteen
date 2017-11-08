package com.artlib.choicescanteen;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button loginButton;

    private ProgressDialog progressDialog;

    private DatabaseReference adminDatabaseReference;
    private DatabaseReference userDatabaseReference;
    private UserLocalDataStore userLocalDataStore;

    private FirebaseAuth loginAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.login_button);

        progressDialog = new ProgressDialog(this);

        adminDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Admin");
        adminDatabaseReference.keepSynced(true);

        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userDatabaseReference.keepSynced(true);

        loginAuth = FirebaseAuth.getInstance();

        userLocalDataStore = new UserLocalDataStore(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent dashboardIntent = new Intent(MainActivity.this, DashboardActivity.class);
                    dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(dashboardIntent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        isAdminAccountExist();

        loginAuth.addAuthStateListener(authStateListener);
    }

    private void isAdminAccountExist() {

        adminDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 1) {
                    Intent registerIntent = new Intent(MainActivity.this, RegisterAdminActivity.class);
                    registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void login() {
        final String usernameText = username.getText().toString();
        final String passwordText = password.getText().toString();
        if (!TextUtils.isEmpty(usernameText) && !TextUtils.isEmpty(passwordText)) {
            progressDialog.setMessage("You are being logged in, please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();

            loginAuth.signInWithEmailAndPassword(usernameText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        final String id = loginAuth.getCurrentUser().getUid();
                        adminDatabaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(id)){
                                    userLocalDataStore.isAdminLoggedIn(true);
                                    userLocalDataStore.storeUser("Admin");
                                    userLocalDataStore.storeAdminId(id);
                                    userLocalDataStore.storeAdminEmail(usernameText);
                                    userLocalDataStore.storeAdminPassword(passwordText);
                                    progressDialog.dismiss();

                                    Intent dashboardIntent = new Intent(MainActivity.this, DashboardActivity.class);
                                    dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(dashboardIntent);

                                } else {
                                    userLocalDataStore.isAdminLoggedIn(false);
                                    progressDialog.dismiss();
                                    Intent dashboardIntent = new Intent(MainActivity.this, DashboardActivity.class);
                                    dashboardIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(dashboardIntent);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                progressDialog.dismiss();

                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setMessage("Login failed!")
                                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        login();
                                    }
                                })
                                .create()
                                .show();
                    }
                }
            });
        }
    }

}
