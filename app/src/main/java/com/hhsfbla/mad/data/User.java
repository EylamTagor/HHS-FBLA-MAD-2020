package com.hhsfbla.mad.data;

import java.util.ArrayList;

public class User {

    private String name;
    private Chapter chapter;
    private UserType userType;
    private String email;
    private ArrayList<Event> myEvents;
    private String blurb;  // Officers
    private String phoneNumber;
    private ArrayList<Competition> comps;

    public User() {
        this("", null, "");
    }

    public User(String name, Chapter chapter, String email) {
        this.name = name;
        this.userType = UserType.MEMBER;
        this.chapter = chapter;
        this.email = email;
        myEvents = new ArrayList<Event>();
        comps = new ArrayList<Competition>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChapter() {
        return chapter.getName();
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getMyEvents() {
        ArrayList<String> names = new ArrayList<String>();
        for(Event event : myEvents) {
            names.add(event.getName());
        }
        return names;
    }

    public void setMyEvents(ArrayList<Event> myEvents) {
        this.myEvents = myEvents;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<Competition> getComps() {
        return comps;
    }

    public void setComps(ArrayList<Competition> comps) {
        this.comps = comps;
    }
}
