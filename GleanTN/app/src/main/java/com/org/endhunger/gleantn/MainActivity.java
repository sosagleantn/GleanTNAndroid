package com.org.endhunger.gleantn;

/**
 * Created by LuisChunga on 9/23/2017.
 */
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseUser mUser;

    TextView userNameTextView;
    EditText descriptionEditText;
    Button btnSend;
    Button btnSignOut;
    private String mUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mUser = mFirebaseAuth.getCurrentUser();
        mUID = mUser.getUid();

        // TextView
        userNameTextView = (TextView) findViewById(R.id.userNameTextView);
        // EditText
        descriptionEditText = (EditText) findViewById(R.id.editTextDescribe);

        // Button
        btnSend = (Button) findViewById(R.id.SendButton);
        btnSignOut = (Button) findViewById(R.id.singOutButton);

        // create an object that will contain the user information
        final UserInformation userInfo = new UserInformation();


        mAuthStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChance: signed in with "+ user.getUid());
                    longToastMessage("Successfully singed in with : "+ user.getEmail());
                }else{
                    // User is signed out
                    Log.d(TAG,"onAuthStateChange: signed out" );
                    longToastMessage("Successfully signed out");
                }
            }};


        /**
         This is the sign out button
         **/
        btnSignOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LogingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /** This is the button the send emails**/
        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sendEmail(userInfo);

            }
        });


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                readData( dataSnapshot , userInfo);
                userNameTextView.setText(userInfo.getName());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }// end of onCreate

    private void sendEmail(UserInformation uInfo) {
        // create an object that will contain the user information

        final String description = descriptionEditText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"gleantn@endhunger.org"});
                intent.putExtra(Intent.EXTRA_SUBJECT, uInfo.getName());
        intent.putExtra(Intent.EXTRA_TEXT, "\n** This is an email sent from android app **\n"
        +"User Information \nName: " + uInfo.getName()+ "\nPhone: " + uInfo.getPhone()+
                "\nEmail: "+ uInfo.getEmail() + "\nAddress: "+uInfo.getStreet()+" "+
        uInfo.getCity()+" "+ uInfo.getState()+" "+ uInfo.getZip()+
                "\nDescription: "+description );

        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Select Email Sending App :"));


    }
    /***
     * this method reads data of the user from Firebase**/
    public void readData(DataSnapshot dataSnapshot, UserInformation uInfo){

        for (DataSnapshot ds: dataSnapshot.getChildren() ){

            // These get the info from Firebase and is store in the object.
            uInfo.setName(ds.child(mUID).getValue(UserInformation.class).getName());
            uInfo.setEmail(ds.child(mUID).getValue(UserInformation.class).getEmail());
            uInfo.setPhone(ds.child(mUID).getValue(UserInformation.class).getPhone());
            uInfo.setStreet(ds.child(mUID).getValue(UserInformation.class).getStreet());
            uInfo.setCity(ds.child(mUID).getValue(UserInformation.class).getCity());
            uInfo.setState(ds.child(mUID).getValue(UserInformation.class).getState());
            uInfo.setZip(ds.child(mUID).getValue(UserInformation.class).getZip());

        }
    }



    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    /*** Customizable toast @parameter message  ***/
    private void longToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
