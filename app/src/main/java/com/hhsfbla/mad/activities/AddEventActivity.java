package com.hhsfbla.mad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hhsfbla.mad.R;

public class AddEventActivity extends AppCompatActivity {


    private ImageButton backBtn2 = findViewById(R.id.backBtn2);
    private Button doneBtn = findViewById(R.id.doneBtn);
    private Button editBtn = findViewById(R.id.editBtn);
    private EditText nameEditTxt = findViewById(R.id.nameEditTxt);
    private EditText dateEditTxt = findViewById(R.id.dateEditTxt);
    private EditText timeEditTxt = findViewById(R.id.timeEditTxt);
    private EditText locaEditTxt = findViewById(R.id.locaEditTxt);
    private EditText descrEditTxt = findViewById(R.id.descrEditTxt);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
    }


}
