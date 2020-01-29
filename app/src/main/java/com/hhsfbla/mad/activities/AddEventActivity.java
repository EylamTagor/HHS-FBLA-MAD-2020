package com.hhsfbla.mad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.hhsfbla.mad.R;

public class AddEventActivity extends AppCompatActivity {


    private ImageButton backBtn2;
    private Button doneBtn;
    private Button editBtn;
    private EditText nameEditTxt;
    private EditText dateEditTxt;
    private EditText timeEditTxt;
    private EditText locaEditTxt;
    private EditText descrEditTxt;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        backBtn2 = findViewById(R.id.backBtn2);
        doneBtn = findViewById(R.id.doneBtn);
        editBtn = findViewById(R.id.editBtn);
        nameEditTxt = findViewById(R.id.nameEditTxt);
        dateEditTxt = findViewById(R.id.dateEditTxt);
        timeEditTxt = findViewById(R.id.timeEditTxt);
        locaEditTxt = findViewById(R.id.locaEditTxt);
        descrEditTxt = findViewById(R.id.descrEditTxt);
        imageView = findViewById(R.id.imageView);


        backBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddEventActivity.this, HomeActivity.class));
            }
        });


        doneBtn.setOnClickListener(new View.OnClickListener() {

            //TODO: save information typed on this page
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddEventActivity.this, HomeActivity.class));
            }
        });

    }


}
