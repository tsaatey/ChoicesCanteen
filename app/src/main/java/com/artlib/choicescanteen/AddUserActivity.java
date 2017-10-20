package com.artlib.choicescanteen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddUserActivity extends AppCompatActivity {

    private EditText full_name;
    private EditText email_address;
    private EditText password;
    private EditText confirmed_password;
    private Button addUserButton;
    private Button backButton;

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

        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = full_name.getText().toString();
                String email = email_address.getText().toString();
                String pass1 = password.getText().toString();
                String pass2 = confirmed_password.getText().toString();

                new SetupActivity().addUserToDatabase(name, email, pass1, pass2);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddUserActivity.this, DashboardActivity.class));
            }
        });
    }
}
