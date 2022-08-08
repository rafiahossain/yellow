package com.example.yellow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyLoginActivity extends AppCompatActivity {

    private EditText edittextemail, edittextpassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_login);

        edittextemail = (EditText) findViewById(R.id.email1);
        edittextpassword = (EditText) findViewById(R.id.password1);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
    }

    public void forgotpass(View view) {
        Intent i = new Intent(getApplicationContext(), ForgotPassword.class);
        startActivity(i);
    }

    public void login(View view) {
        String email = edittextemail.getText().toString().trim();
        String password = edittextpassword.getText().toString().trim();

        //check if any field empty
        if (email.equals("") || password.equals("")) {
            Toast.makeText(MyLoginActivity.this, "Please enter values for all fields", Toast.LENGTH_SHORT).show();
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edittextemail.setError("Please provide valid email address");
                edittextemail.requestFocus();
                return;
            }

            // Check if password is at least 6 characters long
            if (password.length() < 6) {
                edittextpassword.setError("Password should be minimum 6 characters");
                edittextpassword.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //check if user has confirmed email verification
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user.isEmailVerified()) {
                            //redirect to home page/main activity
                            //Passing user information
//                            String uid = user.getUid();
//                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//                            reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    if(snapshot.exists()){
//                                        String nameFromDB = snapshot.child(user.getUid()).child("name").getValue(String.class);
//                                        String usernameFromDB = snapshot.child(user.getUid()).child("username").getValue(String.class);
//                                        String emailFromDB = snapshot.child(user.getUid()).child("emailadd").getValue(String.class);
//                                        String startDateFromDB = snapshot.child(user.getUid()).child("startDate").getValue(String.class);
//                                        String passwordFromDB = snapshot.child(user.getUid()).child("password").getValue(String.class);
//
                            Intent i = new Intent(getApplicationContext(), UserProfile.class);
//
//                                        i.putExtra("name", nameFromDB);
//                                        i.putExtra("username", usernameFromDB);
//                                        i.putExtra("emailadd", emailFromDB);
//                                        i.putExtra("startDate", startDateFromDB);
//                                        i.putExtra("password", passwordFromDB);
//
                            startActivity(i);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MyLoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
                        } else {
                            user.sendEmailVerification();
                            Toast.makeText(MyLoginActivity.this, "Please check email to verify account, check spam folder as well", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MyLoginActivity.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void toregister(View view) {
        Intent i = new Intent(getApplicationContext(), MyRegisterPage.class);
        startActivity(i);
    }
}