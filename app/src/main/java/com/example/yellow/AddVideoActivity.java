package com.example.yellow;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class AddVideoActivity extends AppCompatActivity {

    //Drop down menu for trimester input
    String[] items = {"1", "2", "3"};
    AutoCompleteTextView trimesterAuto;
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        //Drop down menu for trimester input
        trimesterAuto = findViewById(R.id.trimesterAuto);
        adapterItems = new ArrayAdapter<String>(this, R.layout.trimester_list_item, items);
        trimesterAuto.setAdapter(adapterItems);
        trimesterAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }
        });


    }
}