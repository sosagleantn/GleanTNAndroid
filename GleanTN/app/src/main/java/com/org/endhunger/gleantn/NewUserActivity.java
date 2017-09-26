package com.org.endhunger.gleantn;
/**
 * Created by LuisChunga on 9/23/2017.
 */
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewUserActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    /* New user field declaration*/
    private TextView FullName_TextView;
    private TextView Email_TextView;
    private TextView Password_TextView;
    private TextView PhoneNumber_TextView;
    private TextView Street_TextView;
    private TextView City_TextView;
    private TextView State_TextView;
    private TextView ZipCode_TextView;

    String UID = "no uid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        FullName_TextView =(TextView) findViewById(R.id.New_User_FullName_EditText);
        Email_TextView =(TextView) findViewById(R.id.New_User_Email_EditText);
        Password_TextView =(TextView) findViewById(R.id.New_User_Password_EditText);
        PhoneNumber_TextView =(TextView) findViewById(R.id.New_User_Phone_EditText);
        Street_TextView =(TextView) findViewById(R.id.New_User_Street_EditText);
        City_TextView =(TextView) findViewById(R.id.New_User_City_EditText);
        State_TextView =(TextView) findViewById(R.id.New_User_State_EditText);
        ZipCode_TextView=(TextView) findViewById(R.id.New_User_ZipCode_EditText);


    }

    public void SummitNewUserAccount(View view) {

        firebaseAuth = FirebaseAuth.getInstance();

        // Converting all of the EditText fields into strings.
        final String FullName = FullName_TextView.getText().toString();
        final String Email = Email_TextView.getText().toString();
        final String Password = Password_TextView.getText().toString();
        final String Phone = PhoneNumber_TextView.getText().toString();
        final String Street = Street_TextView.getText().toString();
        final String City = City_TextView.getText().toString();
        final String State = State_TextView.getText().toString();
        final String Zipcode = ZipCode_TextView.getText().toString();

        if (TextUtils.isEmpty(FullName)) {
            Toast.makeText(getApplicationContext(), "Enter your Full Name!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(Password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Password.length() < 6) {
            Password_TextView.setError(getString(R.string.minimum_password));
        }

        firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(NewUserActivity.this, "A user with this email already exist.",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            firebaseAuth = FirebaseAuth.getInstance();
                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                            UID = user.getUid();
                            Create_New_User(FullName, Email, Password,Phone, Street, City, State, Zipcode);

                            Toast.makeText(NewUserActivity.this, "New User Created Successfully!",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(NewUserActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }// end of SummitNewUserAccount function

    /*This function creates a new user in firebase */
   private void Create_New_User(String FullName, String Email, String Password, String Phone,
                                String Street, String City, String State, String Zipcode){

       firebaseDatabase = FirebaseDatabase.getInstance();
       DatabaseReference myRef = firebaseDatabase.getReference("farmers");

       //Loading the information into the Database.
       myRef.child(UID).child("city").setValue(City);
       myRef.child(UID).child("email").setValue(Email);
       myRef.child(UID).child("name").setValue(FullName);
       myRef.child(UID).child("phone").setValue(Phone);
       myRef.child(UID).child("state").setValue(State);
       myRef.child(UID).child("street").setValue(Street);
       myRef.child(UID).child("zip").setValue(Zipcode);

    }

} // end of newUserActivity function
