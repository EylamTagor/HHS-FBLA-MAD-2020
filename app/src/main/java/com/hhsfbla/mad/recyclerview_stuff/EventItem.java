package com.hhsfbla.mad.recyclerview_stuff;

import java.util.Date;

public class EventItem {
    private String title, date, time, location;
    private int pic;

    public EventItem(String title, String date, String time, String location, int pic) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }
}
