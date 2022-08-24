package com.example.yellow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MyRegisterPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText fullname, username, password, confirmpassword, email, startDate;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_register_page);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.confirmpassword);

        startDate = findViewById(R.id.startDate);
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }

            private void updateCalendar() {
                String Format = "dd/MM/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.US);
                startDate.setText(sdf.format(calendar.getTime()));
            }
        };

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MyRegisterPage.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    public void register(View view) {
        String name = fullname.getText().toString().trim();
        String emx = email.getText().toString().trim();
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String confpass = confirmpassword.getText().toString().trim();
        String stDate = startDate.getText().toString().trim();

        //check if user exists or not
        if (name.equals("") || emx.equals("") || user.equals("") || pass.equals("") || confpass.equals("") || stDate.equals(""))
            Toast.makeText(MyRegisterPage.this, "Please enter values for all fields", Toast.LENGTH_SHORT).show();
        else {
            // Check if user entered a real valid email address
            if (!Patterns.EMAIL_ADDRESS.matcher(emx).matches()) {
                email.setError("Please provide valid email address");
                email.requestFocus();
                return;
            }

            String noWhiteSpace = "\\A\\w{4,20}\\z";

            if (user.isEmpty()) {
                username.setError("Field cannot be empty");
                username.requestFocus();
                return;
            }else if(user.length()>=15){
                username.setError("Username is too long");
                username.requestFocus();
                return;
            }else if(!user.matches(noWhiteSpace)){
                username.setError("White spaces and symbols are not allowed");
                username.requestFocus();
                return;
            }

            // Check if password is at least 6 characters long
            if (pass.length() < 6) {
                password.setError("Password should be minimum 6 characters");
                password.requestFocus();
                return;
            }

            //check if user exists or not
            if (pass.equals(confpass)) {
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(emx, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    String userID = firebaseUser.getUid();
                                    UserClass u = new UserClass(userID, name, emx, user, stDate, pass);
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(MyRegisterPage.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(MyRegisterPage.this, MyLoginActivity.class);
                                                startActivity(i);
                                            } else {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(MyRegisterPage.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(MyRegisterPage.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(MyRegisterPage.this, "Password does not match", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void tologin(View view) {
        Intent i = new Intent(getApplicationContext(), MyLoginActivity.class);
        startActivity(i);
    }

}