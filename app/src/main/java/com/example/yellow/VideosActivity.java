package com.example.yellow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class VideosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);



    }

    public void goBack(View view) {
        finish();
    }

    public void addVideo(View view) {
        Intent i = new Intent(VideosActivity.this, AddVideoActivity.class);
        startActivity(i);
    }
}