package com.hhsfbla.mad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hhsfbla.mad.R;

public class SignupActivity extends AppCompatActivity {


    private ImageButton backBtn;
    private TextView areyouTxtView;
    private RadioButton advisorRadioBtn;
    private RadioButton officerRadioBtn;
    private RadioButton studentRadioBtn;
    private Button okBtn;
    private TextView createTxtView;
    private EditText nameEditTxt;
    private Button nextBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        backBtn = findViewById(R.id.backBtn);
        areyouTxtView = findViewById(R.id.areyouTxtView);
        advisorRadioBtn = findViewById(R.id.advisorRadioBtn);
        officerRadioBtn = findViewById(R.id.officerRadioBtn);
        studentRadioBtn = findViewById(R.id.studentRadioBtn);
        okBtn = findViewById(R.id.okBtn);
        createTxtView = findViewById(R.id.createTxtView);
        nameEditTxt = findViewById(R.id.nameEditTxt);
        nextBtn = findViewById(R.id.nextBtn);


    }
}
