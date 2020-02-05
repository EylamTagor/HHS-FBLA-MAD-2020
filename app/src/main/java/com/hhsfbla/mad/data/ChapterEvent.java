package com.hhsfbla.mad.data;

import java.util.ArrayList;

public class ChapterEvent {

    private String name;
    private ArrayList<String> attendees;
    private String date;
    private String time;
    private String location;
    private String description;
    private int pic;

    public ChapterEvent() {
        this("", "", "", "", "", 0);
    }

    public ChapterEvent(String name, String date, String time, String location, String description, int pic) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
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

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }
}
