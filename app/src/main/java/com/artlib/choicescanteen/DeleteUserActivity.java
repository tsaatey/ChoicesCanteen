package com.artlib.choicescanteen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteUserActivity extends AppCompatActivity {

    private EditText usernameText;
    private EditText passwordText;
    private Button deleteUserButton;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private UserLocalDataStore userLocalDataStore;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        usernameText = (EditText) findViewById(R.id.username);
        passwordText = (EditText) findViewById(R.id.password);
        deleteUserButton = (Button) findViewById(R.id.delete_user_button);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        userLocalDataStore = new UserLocalDataStore(this);

        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uName = usernameText.getText().toString();
                final String uPass = passwordText.getText().toString();

                if (!TextUtils.isEmpty(uName)) {
                    if (!TextUtils.isEmpty(uPass)) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(DeleteUserActivity.this);
                        alert.setMessage("Are you sure you want to delete this user?")
                                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteUser(uName, uPass);
                                    }
                                })
                                .create()
                                .show();

                    } else {
                        Toast.makeText(DeleteUserActivity.this, "Provide password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DeleteUserActivity.this, "Provide username!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void deleteUser(String username, String password) {
        progressDialog.setMessage("Deleting user...please wait");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseAuth.signInWithEmailAndPassword(userLocalDataStore.getAdminEmail(), userLocalDataStore.getAdminPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                    }
                                });

                                dismissWhenNeeded(progressDialog);

                                Toast.makeText(DeleteUserActivity.this, "User successfully deleted", Toast.LENGTH_SHORT).show();

                            } else {
                                dismissWhenNeeded(progressDialog);
                                Toast.makeText(DeleteUserActivity.this, "Delete failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {
                    dismissWhenNeeded(progressDialog);
                    Toast.makeText(DeleteUserActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void dismissDialog(ProgressDialog dialog) {
        try{
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {

        } catch (final Exception e) {

        } finally {
            dialog = null;
        }
    }

    private void dismissWhenNeeded(ProgressDialog dialog) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                Context context = ((ContextWrapper)dialog.getContext()).getBaseContext();
                if (context instanceof Activity) {
                    // API level 17
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                        if (!((Activity)context).isFinishing() && !((Activity)context).isDestroyed()) {
                            dismissDialog(dialog);
                        }

                    } else {
                        // API less 17
                        if (!((Activity)context).isFinishing()) {
                            dismissDialog(dialog);
                        }
                    }

                } else {
                    dismissDialog(dialog);
                }
            }
            dialog = null;
        }
    }
}
