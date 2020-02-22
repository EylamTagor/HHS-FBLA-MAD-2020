package com.hhsfbla.mad.data;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;

public class ChapterEvent {

    private String name;
    private ArrayList<String> attendees;
    private String date;
    private String time;
    private String location;
    private String description;
    private String facebookLink;
    private String pic;

    public ChapterEvent() {
        this("", "", "", "", "", "", null);
    }

    public ChapterEvent(String name, String date, String time, String location, String description, String facebookLink, String pic) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.facebookLink = facebookLink;
        this.pic = pic;
        attendees = new ArrayList<String>();
    }

    public void addAttendee(String user) {
        attendees.add(user);
    }

    public void removeAttendee(User user) {
        attendees.remove(user);
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(ArrayList<String> attendees) {
        this.attendees = attendees;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }
}
