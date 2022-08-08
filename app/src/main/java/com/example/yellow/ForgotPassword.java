package com.example.yellow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailx;
    private Button btnresetpass;
    private ProgressBar progressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailx = (EditText)findViewById(R.id.email);
        btnresetpass = (Button)findViewById(R.id.resetpass);
        progressBar =(ProgressBar)findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        btnresetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

    }

    private void resetPassword(){
        String email = emailx.getText().toString().trim();

        if(email.isEmpty()){
            emailx.setError("Email is required");
            emailx.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailx.setError("Provide a valid email");
            emailx.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this, "Check your email to reset password", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), MyLoginActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(ForgotPassword.this, "Please try again, something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}