package com.example.yellow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UserProfile extends AppCompatActivity {

    //Variables
    TextInputLayout fullname, trimester, currentDate, startDate, email, username;
    TextView fullnameLabel, usernameLabel;

    //Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String userID = user.getUid();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Textview
        fullnameLabel = findViewById(R.id.fullname_field);
        usernameLabel = findViewById(R.id.username_field);

        //TextInputLayout
        fullname = findViewById(R.id.full_name_profile);
        trimester = findViewById(R.id.trimester_profile);
        currentDate = findViewById(R.id.current_date_profile);
        startDate = findViewById(R.id.start_date_profile);
        email = findViewById(R.id.email_profile);
        username = findViewById(R.id.username_profile);

        //set simple date format varible and set current date
        String Format = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.US);
        Date dateNow = new Date();
        currentDate.getEditText().setText(sdf.format(dateNow));

        //Calculate Trimester
        String dateStartTemp = String.valueOf(startDate.getEditText().getText());
        try {
            Date dateStart = sdf.parse(dateStartTemp);
            trimester.getEditText().setText((CharSequence) dateStart);
        } catch (ParseException e) {
            e.printStackTrace();
            trimester.getEditText().setText("Error");
        }

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
                    String sd = userProfile.startDate;

                    fullnameLabel.setText(name);
                    usernameLabel.setText(un);

                    fullname.getEditText().setText(name);
                    startDate.getEditText().setText(sd);
                    email.getEditText().setText(em);
                    username.getEditText().setText(un);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }


    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(getApplicationContext(), MyLoginActivity.class);
        startActivity(i);
        finish();
    }

    public void chat(View view) {
        Intent i = new Intent(UserProfile.this, MessageActivity.class);
        startActivity(i);
    }

    public void tips(View view) {
        Intent i = new Intent(UserProfile.this, VideosActivity.class);
        startActivity(i);
    }

}