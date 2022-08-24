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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
                            FirebaseUser userF = mAuth.getCurrentUser();
                            String userID = userF.getUid();
                            String dentistID = "ARZWrJlFEgToMUYScCWexldGPmC3";
                            if (!(userID.equals(dentistID))){
                                Intent i = new Intent(MyLoginActivity.this, UserProfile.class);
                                startActivity(i);
                                finish();
                            } else {
                                Intent i = new Intent(MyLoginActivity.this, DentistProfile.class);
                                startActivity(i);
                                finish();
                            }

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