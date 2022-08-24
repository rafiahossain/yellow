package com.example.yellow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DentistProfile extends AppCompatActivity {

    //Variables
    TextView fullnameLabel, emailLabel, usernameLabel;

    //Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String userID = user.getUid();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dentist_profile);

        //Textview
        fullnameLabel = findViewById(R.id.fullname_field);
        usernameLabel = findViewById(R.id.username_field);
        emailLabel = findViewById(R.id.email_field);

        //Method to show all data
        showAllUserData();
    }

    private void showAllUserData() {

        DatabaseReference reference = database.getReference("Users");
        //get data from real time database
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserClass userProfile = snapshot.getValue(UserClass.class);

                if (userProfile != null) {
                    String name = userProfile.name;
                    String em = userProfile.emailadd;
                    String un = userProfile.username;

                    fullnameLabel.setText(name);
                    emailLabel.setText(em);
                    usernameLabel.setText(un);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DentistProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(DentistProfile.this, MyLoginActivity.class);
        startActivity(i);
        finish();
    }

    public void chatList(View view) {

    }

    public void uploadTips(View view) {

    }

}