package com.example.yellow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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

    //Variables
    private static int SPLASH_SCREEN = 2000;
    Animation topAnim, bottomAnim;
    ImageView logo;
    TextView banner, slogan;

    //Firebase user
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Hook xml items
        logo = findViewById(R.id.logo);
        banner = findViewById(R.id.banner);
        slogan = findViewById(R.id.slogan);

        //Assign animations to items
        logo.setAnimation(topAnim);
        banner.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                user = FirebaseAuth.getInstance().getCurrentUser();
                //check if user is logged in
                if (user != null){
                    String userID = user.getUid();
                    String dentistID = "ARZWrJlFEgToMUYScCWexldGPmC3";
                    if (!(userID.equals(dentistID))){
                        Intent i = new Intent(MainActivity.this, UserProfile.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(MainActivity.this, DentistProfile.class);
                        startActivity(i);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(MainActivity.this, MyLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_SCREEN);

    }
}