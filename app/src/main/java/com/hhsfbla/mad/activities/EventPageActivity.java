package com.hhsfbla.mad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hhsfbla.mad.R;
import com.hhsfbla.mad.data.ChapterEvent;

public class EventPageActivity extends AppCompatActivity {
    private ChapterEvent event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        String eventPosition = getIntent().getStringExtra("EVENT_POSITION");
        event = HomeActivity.getEventList().get(Integer.parseInt(eventPosition));
    }
}
