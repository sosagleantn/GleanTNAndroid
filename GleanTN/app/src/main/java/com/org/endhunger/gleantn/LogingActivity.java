package com.org.endhunger.gleantn;

/**
 * Created by LuisChunga on 9/23/2017.
 */


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LogingActivity extends AppCompatActivity {


    private EditText Email_EditText;
    private EditText Password_EdiText;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loging);

        firebaseAuth = FirebaseAuth.getInstance();

        Email_EditText = (EditText) findViewById(R.id.edit_email_text);
        Password_EdiText = (EditText) findViewById(R.id.edit_password_text);


        // This if statement sign out the current user if the account still active.
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseAuth.signOut();
        }

    }// end of onCreate

    public void Login(View view){

        final String email = Email_EditText.getText().toString();
        final String password = Password_EdiText.getText().toString();

        // if the user does not input anything this if statement will display a message
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        // hide the keyboard after user enter his password
        ((InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                Password_EdiText.getWindowToken(), 0);


        //authenticate user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LogingActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (!task.isSuccessful()) {

                            if (password.length() < 6) {
                                Password_EdiText.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LogingActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {

                            Toast.makeText(LogingActivity.this, "WELCOME!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LogingActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    // This method starts the new user activity
    public void CreateNewUser(View view)  {
        Intent intent = new Intent(LogingActivity.this, NewUserActivity.class);
        startActivity(intent);
        finish();
    }

}
